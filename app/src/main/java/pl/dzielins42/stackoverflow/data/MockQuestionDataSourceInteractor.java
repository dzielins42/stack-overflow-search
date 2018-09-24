package pl.dzielins42.stackoverflow.data;

import android.arch.paging.PagedList;
import android.arch.paging.RxPagedListBuilder;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import pl.dzielins42.stackoverflow.database.QuestionDao;
import pl.dzielins42.stackoverflow.database.model.Question;

/**
 * Mock {@link QuestionDataSourceInteractor} implementation fabricating data on the fly.
 */
public class MockQuestionDataSourceInteractor implements QuestionDataSourceInteractor {

    private static final int PAGE_SIZE = 50;
    private static final String TAG = MockQuestionDataSourceInteractor.class.getSimpleName();

    final private QuestionDao mQuestionDao;

    private BoundaryCallback mBoundaryCallback;
    private FlowableProcessor<QueryStatus> mFlowableProcessor;

    @Inject
    public MockQuestionDataSourceInteractor(QuestionDao questionDao) {
        mQuestionDao = questionDao;
    }

    @Override
    public Flowable<QueryStatus> query(@NonNull String query) {
        if (mBoundaryCallback != null) {
            mBoundaryCallback.dispose();
            mBoundaryCallback = null;
        }
        if (mFlowableProcessor != null) {
            mFlowableProcessor.onComplete();
        }

        mFlowableProcessor = PublishProcessor.create();
        Flowable<QueryStatus> dataSource = Flowable.just(query)
                .flatMap(q -> createDatabaseDataSourceFlowable(q)
                        .map(questions -> (QueryStatus) new QueryStatus.PartialResult(questions))
                );

        return Flowable.merge(mFlowableProcessor, dataSource)
                .subscribeOn(Schedulers.io());
    }

    private Flowable<PagedList<Question>> createDatabaseDataSourceFlowable(@NonNull String query) {
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(PAGE_SIZE)
                .setPrefetchDistance(3 * PAGE_SIZE)
                .build();
        mBoundaryCallback = new BoundaryCallback(query);

        return new RxPagedListBuilder<>(mQuestionDao.allDataSource(), config)
                .setBoundaryCallback(mBoundaryCallback)
                .buildFlowable(BackpressureStrategy.LATEST)
                // Clear database to force new data from BoundaryCallback
                .doOnSubscribe(s -> mQuestionDao.clear());
    }

    private class BoundaryCallback extends PagedList.BoundaryCallback<Question> {

        private final String mQuery;
        private final CompositeDisposable mDisposables = new CompositeDisposable();
        private final Random mRandom = new Random();
        private int mCreatedCount = 0;
        private final int mItemCount;

        private boolean mInitialized = false;

        private BoundaryCallback(@NonNull String query) {
            mQuery = query;
            mItemCount = mRandom.nextInt(501);
            Log.d(TAG, String.format("%s items will be fabricated", String.valueOf(mItemCount)));
        }

        @Override
        public void onZeroItemsLoaded() {
            if (mInitialized) {
                return;
            }
            mInitialized = true;

            Log.d(TAG, "onZeroItemsLoaded() called");
            mDisposables.add(Flowable.just(mRandom.nextInt(50) + 1)
                    .subscribeOn(Schedulers.io())
                    .map(count ->
                            mCreatedCount + count > mItemCount ? mItemCount - mCreatedCount : count
                    )
                    .map(count -> {
                        List<Question> questions = new ArrayList<>(count);
                        for (int i = 0; i < count; i++) {
                            questions.add(generateRandomQuestion());
                        }
                        return questions;
                    })
                    .delay(mRandom.nextInt(4) + 2, TimeUnit.SECONDS)
                    .doOnNext(mQuestionDao::replaceAll)
                    .flatMap(questions ->
                            // Delay is needed because loading end indication comes before actual
                            // data causing issues in GUI
                            Flowable.just((QueryStatus) new QueryStatus.InitialLoaded())
                                    .delay(200, TimeUnit.MILLISECONDS)
                    )
                    .startWith(new QueryStatus.InitialLoading(mQuery))
                    .doOnError(error -> Log.e(TAG, "Error in onZeroItemsLoaded: ", error))
                    .onErrorReturn(QueryStatus.Error::new)
                    .subscribe(mFlowableProcessor::onNext)
            );
        }

        @Override
        public void onItemAtEndLoaded(@NonNull Question itemAtEnd) {
            Log.d(TAG, "onItemAtEndLoaded() called with: itemAtEnd = [" + itemAtEnd + "]");
            mDisposables.add(Flowable.just(mRandom.nextInt(50) + 1)
                    .subscribeOn(Schedulers.io())
                    .map(count ->
                            mCreatedCount + count > mItemCount ? mItemCount - mCreatedCount : count
                    )
                    .map(count -> {
                        List<Question> questions = new ArrayList<>(count);
                        for (int i = 0; i < count; i++) {
                            questions.add(generateRandomQuestion());
                        }
                        return questions;
                    })
                    .delay(mRandom.nextInt(4) + 2, TimeUnit.SECONDS)
                    .doOnNext(mQuestionDao::insert)
                    .flatMap(questions ->
                            // Delay is needed because loading end indication comes before actual
                            // data causing issues in GUI
                            Flowable.just((QueryStatus) new QueryStatus.PageLoaded())
                                    .delay(200, TimeUnit.MILLISECONDS)
                    )
                    .startWith(new QueryStatus.PageLoading())
                    .doOnError(error -> Log.e(TAG, "Error in onItemAtEndLoaded: ", error))
                    .onErrorReturn(QueryStatus.Error::new)
                    .subscribe(mFlowableProcessor::onNext)
            );
        }

        private Question generateRandomQuestion() {
            final int x = mCreatedCount++;
            return Question.builder()
                    .id(x)
                    .title(String.format("%s%s", mQuery, String.valueOf(x)))
                    .authorDisplayName("Mock")
                    .authorProfileImageUrl(null)
                    .answerCount(mRandom.nextInt(21))
                    .link(String.format(
                            "https://stackoverflow.com/questions/%s", String.valueOf(x)
                    ))
                    .page(1)
                    .order(x)
                    .build();
        }

        private void dispose() {
            mDisposables.clear();
        }
    }
}
