package pl.dzielins42.stackoverflow.database;

import android.arch.paging.DataSource;
import android.arch.paging.PagedList;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import io.reactivex.Flowable;
import pl.dzielins42.stackoverflow.database.model.Question;

@Dao
public abstract class QuestionDao {
    /**
     * @return {@link Flowable} emitting list of all entities in question table. In case
     * underlying data changes, new list will be emitted. Returned list is sorted by page and
     * order properties of {@link Question}.
     */
    @Query("SELECT * FROM questions ORDER BY page ASC, ordinal ASC")
    public abstract Flowable<List<Question>> all();

    /**
     * @return {@link DataSource} backing up {@link PagedList} with all entities in question
     * table. In case
     * underlying data changes, new list will be emitted. Returned list is sorted by page and
     * order properties of {@link Question}.
     */
    @Query("SELECT * FROM questions ORDER BY page ASC, ordinal ASC")
    public abstract DataSource.Factory<Integer, Question> allDataSource();

    /**
     * Inserts provided {@link Question} instances into questions table in Room database. In case
     * of conflict, {@link OnConflictStrategy#REPLACE} strategy is used.
     *
     * @param questions entities to be inserted into database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Question... questions);

    /**
     * Inserts provided {@link Question} instances into questions table in Room database. In case
     * of conflict, {@link OnConflictStrategy#REPLACE} strategy is used.
     *
     * @param questions entities to be inserted into database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<Question> questions);

    /**
     * Removes all data from questions table.
     */
    @Query("DELETE FROM questions")
    public abstract void clear();

    /**
     * Removes all data and inserts new data into questions table, in single transaction.
     *
     * @param questions entities to be inserted into database.
     */
    @Transaction
    public void replaceAll(Question... questions) {
        clear();
        insert(questions);
    }

    /**
     * Removes all data and inserts new data into questions table, in single transaction.
     *
     * @param questions entities to be inserted into database.
     */
    @Transaction
    public void replaceAll(List<Question> questions) {
        clear();
        insert(questions);
    }
}
