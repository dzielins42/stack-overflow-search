package pl.dzielins42.stackoverflow.di;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application;
    }

    /*
    @Provides
    @Singleton
    AppHelloService provideAppHelloService() {
        return new AppHelloService();
    }

    @Provides
    @Singleton
    ActivityHelloService provideActivityHelloService() {
        return new ActivityHelloService();
    }

    @Provides
    @Singleton
    FragmentHelloService provideFragmentHelloService() {
        return new FragmentHelloService();
    }
    */
}
