package pl.dzielins42.stackoverflow.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import pl.dzielins42.stackoverflow.database.model.Question;

@Dao
public interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Question... questions);

    @Query("SELECT * FROM questions")
    Flowable<List<Question>> all();
}
