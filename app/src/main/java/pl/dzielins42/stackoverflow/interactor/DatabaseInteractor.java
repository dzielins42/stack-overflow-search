package pl.dzielins42.stackoverflow.interactor;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import pl.dzielins42.stackoverflow.database.QuestionDao;
import pl.dzielins42.stackoverflow.database.model.Question;

public class DatabaseInteractor {

    private final QuestionDao mQuestionDao;

    @Inject
    public DatabaseInteractor(QuestionDao questionDao) {
        mQuestionDao = questionDao;
    }

    public Flowable<List<Question>> questions() {
        return mQuestionDao.all();
    }
}
