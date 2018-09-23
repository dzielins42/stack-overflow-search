package pl.dzielins42.stackoverflow.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.hannesdorfmann.mosby3.mvi.MviActivity;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import io.reactivex.Flowable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;
import pl.dzielins42.stackoverflow.R;

public class MainActivity
        extends MviActivity<MainView, MainPresenter>
        implements MainView {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    MainPresenter mMainPresenter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout mSwipeRefresh;

    private QuestionAdapter mAdapter;

    private String mCurrentQuery = null;
    private int mCurrentResultsPage = 0;

    private final FlowableProcessor<CharSequence> mQueryObservable = PublishProcessor.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mAdapter = new QuestionAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefresh.setOnRefreshListener(() -> mQueryObservable.onNext(mCurrentQuery));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                mQueryObservable.onNext(text);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                mQueryObservable.onNext(text);
                return false;
            }
        });

        return true;
    }

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        return mMainPresenter;
    }

    @Override
    public Flowable<MainIntent> intents() {
        Flowable<MainIntent> listClicks = mAdapter.intents();
        Flowable<MainIntent> search = mQueryObservable
                .debounce(250, TimeUnit.MILLISECONDS)
                .filter(text -> !TextUtils.isEmpty(text))
                .map(text -> (MainIntent) MainIntent.Query.builder()
                        .query(String.valueOf(text))
                        .build()
                );

        return Flowable.merge(listClicks, search);
    }

    @Override
    public void render(MainModel model) {
        Log.d(TAG, "render: " + String.valueOf(model));

        mCurrentQuery = model.getQuery();
        mCurrentResultsPage = model.getPage();

        mSwipeRefresh.setEnabled(!TextUtils.isEmpty(mCurrentQuery));

        if (model.getQuestions().isEmpty()) {
            mEmpty.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmpty.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter.submitList(model.getQuestions());
        }

        mSwipeRefresh.setRefreshing(model.isLoadingResults());
    }
}
