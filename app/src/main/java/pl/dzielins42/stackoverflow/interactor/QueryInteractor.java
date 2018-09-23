package pl.dzielins42.stackoverflow.interactor;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
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
                .map(searchResult -> convertFromRestToLocal(searchResult, page))
                .doOnSuccess(questions -> {
                    if (page == 1) {
                        mQuestionDao.replaceAll(questions);
                    } else {
                        mQuestionDao.insert(questions);
                    }
                })
                .ignoreElement();
    }

    private List<Question> convertFromRestToLocal(@NonNull SearchResult searchResult, int page) {
        List<Question> list = new ArrayList(searchResult.getQuestions().size());
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
}
