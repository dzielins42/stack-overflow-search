package pl.dzielins42.stackoverflow.view;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * Interface for helper classes simplifying new {@link MainModel} instance creation.
 */
public interface MainPatch {

    MainModel apply(MainModel model);

    /**
     * Takes previous {@link MainModel} and creates new instance with changed counter value.
     */
    @Value
    @Accessors(prefix = "m")
    @Builder
    final class DummyPatch implements MainPatch {

        private final long mCounter;

        @Override
        public MainModel apply(MainModel model) {
            return model.toBuilder().counter(mCounter).build();
        }
    }

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
}
