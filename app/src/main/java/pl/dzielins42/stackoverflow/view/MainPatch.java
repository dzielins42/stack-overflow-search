package pl.dzielins42.stackoverflow.view;

import java.util.Collections;
import java.util.List;

import lombok.Builder;
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
     * Applies no change to provided model.
     */
    @Value
    @Accessors(prefix = "m")
    @Builder
    final class NoChange implements MainPatch {

        @Override
        public MainModel apply(MainModel model) {
            return model;
        }
    }

    /**
     * Sets collection of {@link Question} objects to be displayed. Collection may be empty if
     * there was no result.
     */
    @Value
    @Accessors(prefix = "m")
    @Builder
    final class DisplayResults implements MainPatch {

        @Builder.Default
        @NonNull
        private final List<Question> mQuestions = Collections.emptyList();

        @Override
        public MainModel apply(MainModel model) {
            return model.toBuilder().questions(mQuestions).build();
        }
    }

    @Value
    @Accessors(prefix = "m")
    @Builder
    final class SetQuery implements MainPatch {

        private final int mPage;
        @NonNull
        private final String mQuery;

        @Override
        public MainModel apply(MainModel model) {
            return model.toBuilder().query(mQuery).page(mPage).build();
        }
    }

    @Value
    @Accessors(prefix = "m")
    @Builder
    final class SetLoadingResults implements MainPatch {

        private final boolean mLoading;

        @Override
        public MainModel apply(MainModel model) {
            return model.toBuilder().loadingResults(mLoading).build();
        }
    }
}
