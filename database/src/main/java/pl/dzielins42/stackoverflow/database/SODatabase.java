package pl.dzielins42.stackoverflow.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import pl.dzielins42.stackoverflow.database.model.Question;

@Database(entities = {Question.class}, version = 1)
public abstract class SODatabase extends RoomDatabase {
    public abstract QuestionDao questionDao();
}
