package pl.dzielins42.stackoverflow.view;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * Immutable class representing state of the business logic to be renderd by {@link MainView}.
 */
@Value
@Builder(toBuilder = true)
@Accessors(prefix = "m")
public class MainModel {

    private final long mCounter;
}
