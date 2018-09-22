package pl.dzielins42.stackoverflow.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.hannesdorfmann.mosby3.mvi.MviActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import io.reactivex.Flowable;
import pl.dzielins42.stackoverflow.R;
import pl.dzielins42.stackoverflow.database.model.Question;

public class MainActivity
        extends MviActivity<MainView, MainPresenter>
        implements MainView {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    MainPresenter mMainPresenter;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    private QuestionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mAdapter = new QuestionAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        List<Question> dummy = new ArrayList<>(10);
        for(int i=0;i<10;i++){
            dummy.add(
                    Question.builder()
                            .id(i)
                            .title(String.valueOf(i))
                            .authorDisplayName(String.valueOf(i))
                            .answerCount(i)
                            .build()
            );
        }
        mAdapter.submitList(dummy);
    }

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        return mMainPresenter;
    }

    @Override
    public Flowable<MainIntent> intents() {
        return Flowable.interval(1, TimeUnit.SECONDS)
                .map(l -> MainIntent.DummyIntent.builder().counter(l).build());
    }

    @Override
    public void render(MainModel model) {
        Log.d(TAG, "render: " + String.valueOf(model));
        // Eg. update TextView...
    }
}
