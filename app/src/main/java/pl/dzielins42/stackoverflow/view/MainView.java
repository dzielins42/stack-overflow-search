package pl.dzielins42.stackoverflow.view;

import com.hannesdorfmann.mosby3.mvp.MvpView;

import io.reactivex.Flowable;

/**
 * Presentation layer interface capable of rendering {@link MainModel} and providing user input
 * via stream of {@link MainIntent} intances.
 */
public interface MainView extends MvpView {

    Flowable<MainIntent> intents();

    void render(MainModel model);
}
