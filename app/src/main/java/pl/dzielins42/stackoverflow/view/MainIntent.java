package pl.dzielins42.stackoverflow.view;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import pl.dzielins42.stackoverflow.database.model.Question;

/**
 * Interface for classes representing event/action that may change business logic state, and thus
 * {@link MainModel} rendered by {@link MainView}.
 */
public interface MainIntent {

    /**
     * Intent fired when question was clicked on the list.
     */
    @Value
    @Accessors(prefix = "m")
    @Builder
    final class QuestionClicked implements MainIntent {
        @NonNull
        private final Question mQuestion;
    }

    /**
     * Intent to query for new results.
     */
    @Value
    @Accessors(prefix = "m")
    @Builder
    final class Query implements MainIntent {
        @NonNull
        private final String mQuery;
    }

    /**
     * Intent fired when result list has been updated.
     */
    @Value
    @Accessors(prefix = "m")
    @Builder
    final class ResultsUpdate implements MainIntent {
        @NonNull
        @Builder.Default
        private final List<Question> mQuestions = Collections.emptyList();
    }
}
