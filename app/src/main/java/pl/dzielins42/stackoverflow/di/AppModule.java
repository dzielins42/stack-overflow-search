package pl.dzielins42.stackoverflow.di;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.util.Log;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.dzielins42.stackoverflow.api.StackOverflowService;
import pl.dzielins42.stackoverflow.database.QuestionDao;
import pl.dzielins42.stackoverflow.database.SODatabase;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppModule {

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application;
    }

    @Provides
    @Singleton
    @Named("stackoverflow")
    Retrofit provideStackOverflowRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("https://api.stackexchange.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    StackOverflowService provideStackOverflowService(@Named("stackoverflow") Retrofit retrofit) {
        return retrofit.create(StackOverflowService.class);
    }

    @Provides
    @Singleton
    SODatabase provideDatabase(Context context) {
        return Room.databaseBuilder(context, SODatabase.class, "stack-verflow-search-db")
                .build();

    }

    @Provides
    @Singleton
    QuestionDao provideQuestionDao(SODatabase db) {
        QuestionDao questionDao = db.questionDao();
        // Clear here so the old results are not displayed  at the start
        Completable clearCompletable = Completable.create(emitter -> {
            questionDao.clear();
            emitter.onComplete();
        });
        clearCompletable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        return questionDao;
    }
}
