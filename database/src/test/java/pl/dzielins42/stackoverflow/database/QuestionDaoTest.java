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
import static org.junit.Assert.*;

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
                .authorProfileImageUrl("I'm out of jokes here")
                .build();
        Question q2 = Question.builder()
                .id(1)
                .title("How much wood would a woodchuck chuck?")
                .answerCount(2187)
                .authorDisplayName("RHDavis")
                .authorProfileImageUrl(null)
                .build();

        mQuestionDao.insert(q1, q2);

        List<Question> questions = mQuestionDao.all().blockingFirst();

        assertThat(questions, hasItems(q1, q2));
    }
}