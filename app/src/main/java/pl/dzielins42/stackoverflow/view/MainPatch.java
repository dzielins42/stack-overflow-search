package pl.dzielins42.stackoverflow.view;

import android.arch.paging.PagedList;
import android.support.annotation.Nullable;

import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;
import pl.dzielins42.stackoverflow.database.model.Question;

/**
 * Interface for helper classes simplifying new {@link MainModel} instance creation.
 */
public interface MainPatch {

    MainModel apply(MainModel model);

    /**
     * Sets collection of {@link Question} objects to be displayed. Collection may be empty if
     * there was no result.
     */
    @Value
    @Accessors(prefix = "m")
    final class DisplayResults implements MainPatch {

        @NonNull
        private final PagedList<Question> mQuestions;

        @Override
        public MainModel apply(MainModel model) {
            return model.toBuilder().questions(mQuestions).build();
        }
    }

    @Value
    @Accessors(prefix = "m")
    final class SetQuery implements MainPatch {

        @NonNull
        private final String mQuery;

        @Override
        public MainModel apply(MainModel model) {
            return model.toBuilder().query(mQuery).build();
        }
    }

    @Value
    @Accessors(prefix = "m")
    final class SetPageLoading implements MainPatch {

        private final boolean mPageLoading;

        @Override
        public MainModel apply(MainModel model) {
            return model.toBuilder()
                    .pageLoading(isPageLoading())
                    .build();
        }
    }

    @Value
    @Accessors(prefix = "m")
    final class SetInitialLoading implements MainPatch {

        private final boolean mInitialLoading;

        @Override
        public MainModel apply(MainModel model) {
            return model.toBuilder()
                    .initialLoading(mInitialLoading)
                    .build();
        }
    }

    @Value
    @Accessors(prefix = "m")
    final class SetError implements MainPatch {

        @Nullable
        private final Throwable mError;

        @Override
        public MainModel apply(MainModel model) {
            return model.toBuilder()
                    .error(mError)
                    .build();
        }
    }
}
