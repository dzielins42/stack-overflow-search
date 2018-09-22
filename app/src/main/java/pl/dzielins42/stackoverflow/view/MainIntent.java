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
     * Simple intent that requests change of counter value to value provided in intent.
     */
    @Value
    @Accessors(prefix = "m")
    @Builder
    final class DummyIntent implements MainIntent {
        private final long mCounter;
    }

    /**
     * Intent fired when question was clicked on the list.
     */
    @Value
    @Accessors(prefix = "m")
    @Builder
    final class QuestionClicked implements MainIntent {
        private final Question mQuestion;
    }
}
