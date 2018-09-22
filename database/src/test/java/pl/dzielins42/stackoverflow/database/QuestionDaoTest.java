package pl.dzielins42.stackoverflow.database;

import android.arch.persistence.room.Room;
import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.List;

import pl.dzielins42.stackoverflow.database.model.Question;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class QuestionDaoTest {

    private SODatabase mDb;
    private QuestionDao mQuestionDao;

    @Before
    public void setUp() throws Exception {
        final Context context = RuntimeEnvironment.application;
        mDb = Room.inMemoryDatabaseBuilder(context, SODatabase.class)
                .allowMainThreadQueries()
                .build();
        mQuestionDao = mDb.questionDao();
    }

    @After
    public void tearDown() throws Exception {
        mQuestionDao = null;
        mDb.close();
        mDb = null;
    }

    @Test
    public void insertAndGetAll() {
        Question q1 = Question.builder()
                .id(42)
                .title("The Ultimate Question of Life, the Universe and Everything")
                .answerCount(1)
                .authorDisplayName("Mickey")
                .link("")
                .build();
        Question q2 = createSimpleQuestion(2);

        mQuestionDao.insert(q1, q2);

        List<Question> questions = mQuestionDao.all().blockingFirst();

        assertThat(questions, hasItems(q1, q2));
    }

    @Test
    public void clear() {
        insertAndGetAll();

        mQuestionDao.clear();

        List<Question> questions = mQuestionDao.all().blockingFirst();

        assertTrue(questions.isEmpty());
    }

    @Test
    public void replaceAll() {
        List<Question> questions;
        Question q1 = createSimpleQuestion(1);
        Question q2 = createSimpleQuestion(2);
        Question q3 = createSimpleQuestion(3);
        Question q4 = createSimpleQuestion(4);

        mQuestionDao.insert(q1, q2);

        questions = mQuestionDao.all().blockingFirst();
        assertThat(questions, hasItems(q1, q2));

        mQuestionDao.replaceAll(q3, q4);

        questions = mQuestionDao.all().blockingFirst();
        assertThat(questions, hasItems(q3, q4));
        assertThat(questions, not(hasItems(q1, q2)));
    }

    private Question createSimpleQuestion(int i) {
        return Question.builder()
                .id(i)
                .title(String.valueOf(i))
                .answerCount(i)
                .authorDisplayName(String.valueOf(i))
                .link(String.valueOf(i))
                .build();
    }
}