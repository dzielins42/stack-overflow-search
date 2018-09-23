package pl.dzielins42.stackoverflow.view;

import java.util.Collections;
import java.util.List;

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
public class MainModel {

    private final int mPage;
    @NonNull
    @Builder.Default
    private final String mQuery = "";
    private final boolean mLoadingResults;
    @NonNull
    @Builder.Default
    private final List<Question> mQuestions = Collections.emptyList();
}
