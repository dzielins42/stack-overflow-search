package pl.dzielins42.stackoverflow.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import pl.dzielins42.stackoverflow.view.MainActivity;

@Module
public abstract class BuildersModule {

    @ContributesAndroidInjector
    abstract MainActivity bindMainActivity();

    // Add bindings for other sub-components here
}
