package pl.dzielins42.stackoverflow.view;

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
        private final Question mQuestion;
    }

    /**
     * Intent to query for new results.
     */
    @Value
    @Accessors(prefix = "m")
    @Builder
    final class Query implements MainIntent {
        private final String mQuery;
    }
}
