package pl.dzielins42.stackoverflow.view;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
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
    private static final String SEARCH_VIEW_KEY = "SEARCH_VIEW";

    // Saved to restore Toolbar SearchView after configuration change
    private SearchViewSavedInstance mSearchViewSavedInstance = null;

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
    SearchView mSearchView;

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

        if (savedInstanceState != null) {
            mSearchViewSavedInstance = savedInstanceState.getParcelable(SEARCH_VIEW_KEY);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        if (mSearchViewSavedInstance != null) {
            mSearchView.setIconified(mSearchViewSavedInstance.iconified);
            mSearchView.setQuery(mSearchViewSavedInstance.query, false);
            if (!mSearchViewSavedInstance.iconified) {
                mSearchView.clearFocus();
            }
        }
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                Log.d(TAG, "onQueryTextSubmit: ");
                mQueryObservable.onNext(text);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                Log.d(TAG, "onQueryTextChange: ");
                mQueryObservable.onNext(text);
                return true;
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        SearchViewSavedInstance searchViewSavedInstance = new SearchViewSavedInstance(
                mSearchView.getQuery().toString(),
                mSearchView.isIconified()
        );
        outState.putParcelable(SEARCH_VIEW_KEY, searchViewSavedInstance);
        super.onSaveInstanceState(outState);
    }

    /**
     * Parcelable to retain Toolbar SearchView through configuration change.
     */
    private static class SearchViewSavedInstance implements Parcelable {
        String query;
        boolean iconified;

        SearchViewSavedInstance(String query, boolean iconified) {
            this.query = query;
            this.iconified = iconified;
        }

        protected SearchViewSavedInstance(Parcel in) {
            query = in.readString();
            iconified = in.readByte() != 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(query);
            dest.writeByte((byte) (iconified ? 1 : 0));
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<SearchViewSavedInstance> CREATOR =
                new Creator<SearchViewSavedInstance>() {
                    @Override
                    public SearchViewSavedInstance createFromParcel(Parcel in) {
                        return new SearchViewSavedInstance(in);
                    }

                    @Override
                    public SearchViewSavedInstance[] newArray(int size) {
                        return new SearchViewSavedInstance[size];
                    }
                };
    }
}
