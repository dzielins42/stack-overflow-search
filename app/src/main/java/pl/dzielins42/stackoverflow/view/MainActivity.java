package pl.dzielins42.stackoverflow.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.hannesdorfmann.mosby3.mvi.MviActivity;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import io.reactivex.Flowable;
import pl.dzielins42.stackoverflow.R;

public class MainActivity
        extends MviActivity<MainView, MainPresenter>
        implements MainView {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    MainPresenter mMainPresenter;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty)
    AppCompatTextView mEmpty;

    private QuestionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mAdapter = new QuestionAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        return mMainPresenter;
    }

    @Override
    public Flowable<MainIntent> intents() {
        Flowable<MainIntent> interval = Flowable.interval(1, TimeUnit.SECONDS)
                .map(l -> MainIntent.DummyIntent.builder().counter(l).build());

        Flowable<MainIntent> listClicks = mAdapter.intents();

        return Flowable.merge(interval, listClicks);
    }

    @Override
    public void render(MainModel model) {
        Log.d(TAG, "render: " + String.valueOf(model));

        if (model.getQuestions().isEmpty()) {
            mEmpty.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmpty.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter.submitList(model.getQuestions());
        }
    }
}
