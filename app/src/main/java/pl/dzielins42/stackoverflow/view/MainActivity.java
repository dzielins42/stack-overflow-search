package pl.dzielins42.stackoverflow.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.hannesdorfmann.mosby3.mvi.MviActivity;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.Flowable;
import pl.dzielins42.stackoverflow.R;

public class MainActivity
        extends MviActivity<MainView, MainPresenter>
        implements MainView {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    MainPresenter mMainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
