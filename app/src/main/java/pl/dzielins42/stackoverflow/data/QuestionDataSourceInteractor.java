package pl.dzielins42.stackoverflow.data;

import android.support.annotation.NonNull;

import io.reactivex.Flowable;

public interface QuestionDataSourceInteractor {
    Flowable<QueryStatus> query(@NonNull String query);
}
