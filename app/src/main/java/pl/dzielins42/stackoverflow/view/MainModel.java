package pl.dzielins42.stackoverflow.view;

import android.arch.paging.PagedList;
import android.support.annotation.Nullable;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;
import pl.dzielins42.stackoverflow.database.model.Question;

/**
 * Immutable class representing state of the business logic to be renderd by {@link MainView}.
 */
@Value
@Builder(toBuilder = true)
@Accessors(prefix = "m")
class MainModel {

    private final boolean mInitialLoading;
    private final boolean mPageLoading;
    @Nullable
    private final Throwable mError;
    @NonNull
    @Builder.Default
    private final String mQuery = "";
    @Nullable
    private final PagedList<Question> mQuestions;
}
