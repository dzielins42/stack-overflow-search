package pl.dzielins42.stackoverflow.interactor;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import pl.dzielins42.stackoverflow.api.StackOverflowService;
import pl.dzielins42.stackoverflow.api.model.generated.SearchResult;
import pl.dzielins42.stackoverflow.database.QuestionDao;
import pl.dzielins42.stackoverflow.database.model.Question;

public class QueryInteractor {

    private static final String TAG = QueryInteractor.class.getSimpleName();

    private final StackOverflowService mStackOverflowService;
    private final QuestionDao mQuestionDao;

    @Inject
    public QueryInteractor(StackOverflowService stackOverflowService, QuestionDao questionDao) {
        mStackOverflowService = stackOverflowService;
        mQuestionDao = questionDao;
    }

    public Completable query(@NonNull String query, int page) {
        return mStackOverflowService.search(page, query)
                .flatMapPublisher(
                        searchResult -> Flowable.fromIterable(searchResult.getQuestions())
                                .map(question -> convertFromRestToLocal(question))
                )
                .toList()
                .doOnSuccess(questions -> {
                    if (page == 1) {
                        mQuestionDao.replaceAll(questions);
                    } else {
                        mQuestionDao.insert(questions);
                    }
                })
                .ignoreElement();
    }

    private Question convertFromRestToLocal(
            @NonNull pl.dzielins42.stackoverflow.api.model.generated.Question question
    ) {
        return Question.builder()
                .id(question.getQuestionId())
                .title(question.getTitle())
                .answerCount(question.getAnswerCount())
                .authorDisplayName(question.getOwner().getDisplayName())
                .authorProfileImageUrl(question.getOwner().getProfileImage())
                .link(question.getLink())
                .build();
    }
}
