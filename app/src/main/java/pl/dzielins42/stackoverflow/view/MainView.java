package pl.dzielins42.stackoverflow.view;

import com.hannesdorfmann.mosby3.mvp.MvpView;

import io.reactivex.Flowable;

/**
 * Presentation layer interface capable of rendering {@link MainModel} and providing user input
 * via streams of intents.
 */
public interface MainView extends MvpView {

    Flowable<String> queryIntent();

    void render(MainModel model);
}
