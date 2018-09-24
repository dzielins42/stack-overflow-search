package pl.dzielins42.stackoverflow.data;

import android.arch.paging.PagedList;

import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;
import pl.dzielins42.stackoverflow.database.model.Question;

public interface QueryStatus {

    /**
     * Initial data is being loaded.
     */
    @Value
    @Accessors(prefix = "m")
    final class InitialLoading implements QueryStatus {
        private final String mQuery;
    }

    /**
     * New page of data is being loaded.
     */
    @Value
    @Accessors(prefix = "m")
    final class PageLoading implements QueryStatus {

    }

    /**
     * Loading page of data completed.
     */
    @Value
    @Accessors(prefix = "m")
    final class PageLoaded implements QueryStatus {

    }

    /**
     * Loading page of data completed.
     */
    @Value
    @Accessors(prefix = "m")
    final class InitialLoaded implements QueryStatus {

    }

    /**
     * Error occured while loading data.
     */
    @Value
    @Accessors(prefix = "m")
    final class Error implements QueryStatus {
        @NonNull
        private final Throwable mError;
    }

    /**
     * Page of data has been loaded.
     */
    @Value
    @Accessors(prefix = "m")
    final class PartialResult implements QueryStatus {
        private final PagedList<Question> mQuestions;
    }
}
