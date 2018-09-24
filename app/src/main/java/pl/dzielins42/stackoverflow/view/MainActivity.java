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
import android.widget.Toast;

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
    private static final long QUERY_DEBOUNCE_MS = 500;

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
    public Flowable<String> queryIntent() {
        return mQueryObservable
                .filter(charSequence -> !TextUtils.isEmpty(charSequence))
                .filter(charSequence -> charSequence.length() > 0)
                .debounce(QUERY_DEBOUNCE_MS, TimeUnit.MILLISECONDS)
                .map(CharSequence::toString);
    }

    @Override
    public void render(MainModel model) {
        Log.d(TAG, "render: " + String.valueOf(model));

        mCurrentQuery = model.getQuery();

        mSwipeRefresh.setEnabled(!TextUtils.isEmpty(mCurrentQuery));

        final boolean noResult = model.getQuestions() == null || model.getQuestions().isEmpty();

        mAdapter.submitList(model.getQuestions());

        if (noResult && !model.isInitialLoading()) {
            mRecyclerView.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
        } else {
            mEmpty.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        mSwipeRefresh.setRefreshing(model.isInitialLoading());

        if (model.getError() != null) {
            Toast.makeText(
                    this,
                    String.format("Error: %s", model.getError().getLocalizedMessage()),
                    Toast.LENGTH_SHORT
            ).show();
        }

        mAdapter.setLoading(model.isPageLoading());
    }
}
