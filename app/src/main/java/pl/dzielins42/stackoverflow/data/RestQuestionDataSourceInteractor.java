package pl.dzielins42.stackoverflow.data;

import android.arch.paging.PagedList;
import android.arch.paging.RxPagedListBuilder;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import pl.dzielins42.stackoverflow.api.StackOverflowService;
import pl.dzielins42.stackoverflow.api.model.generated.SearchResult;
import pl.dzielins42.stackoverflow.database.QuestionDao;
import pl.dzielins42.stackoverflow.database.model.Question;

/**
 * {@link QuestionDataSourceInteractor} implementation based on Stack Overflow REST API.
 */
public class RestQuestionDataSourceInteractor implements QuestionDataSourceInteractor {

    private static final int PAGE_SIZE = 50;
    private static final String TAG = RestQuestionDataSourceInteractor.class.getSimpleName();

    final private QuestionDao mQuestionDao;
    final private StackOverflowService mStackOverflowService;

    private BoundaryCallback mBoundaryCallback;
    private FlowableProcessor<QueryStatus> mFlowableProcessor;

    @Inject
    public RestQuestionDataSourceInteractor(QuestionDao questionDao, StackOverflowService
            stackOverflowService) {
        mQuestionDao = questionDao;
        mStackOverflowService = stackOverflowService;
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

        private boolean mInitialized = false;

        private BoundaryCallback(@NonNull String query) {
            mQuery = query;
        }

        @Override
        public void onZeroItemsLoaded() {
            if (mInitialized) {
                return;
            }
            mInitialized = true;

            Log.d(TAG, "onZeroItemsLoaded() called");
            mDisposables.add(mStackOverflowService.search(1, mQuery)
                    .toFlowable()
                    .subscribeOn(Schedulers.io())
                    .map(searchResult -> convertFromRestToLocal(searchResult, 1))
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
            final int nextPage = itemAtEnd.getPage() + 1;
            Log.d(TAG, "onItemAtEndLoaded: loading page " + nextPage);
            mDisposables.add(mStackOverflowService.search(nextPage, mQuery)
                    .toFlowable()
                    .subscribeOn(Schedulers.io())
                    .map(searchResult -> convertFromRestToLocal(searchResult, nextPage))
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

        /**
         * Converts REST response to list of Room entities.
         */
        private List<Question> convertFromRestToLocal(
                @NonNull SearchResult searchResult, int page
        ) {
            List<Question> list = new ArrayList<>(searchResult.getQuestions().size());
            for (int i = 0; i < searchResult.getQuestions().size(); i++) {
                final pl.dzielins42.stackoverflow.api.model.generated.Question question =
                        searchResult.getQuestions().get(i);
                list.add(Question.builder()
                        .id(question.getQuestionId())
                        .title(question.getTitle())
                        .answerCount(question.getAnswerCount())
                        .authorDisplayName(question.getOwner().getDisplayName())
                        .authorProfileImageUrl(question.getOwner().getProfileImage())
                        .link(question.getLink())
                        .order(i)
                        .page(page)
                        .build());
            }

            return list;
        }

        private void dispose() {
            mDisposables.clear();
        }
    }
}
