package pl.dzielins42.stackoverflow.database.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(prefix = "m")
@Entity(tableName = "questions")
@AllArgsConstructor
@NoArgsConstructor
public class Question {

    @PrimaryKey
    @ColumnInfo(name = "question_id")
    private int mId;
    @ColumnInfo(name = "title")
    @NonNull
    private String mTitle;
    @ColumnInfo(name = "answer_count")
    private int mAnswerCount;
    @ColumnInfo(name = "author_display_name")
    @NonNull
    private String mAuthorDisplayName;
    @ColumnInfo(name = "author_profile_image_ulr")
    private String mAuthorProfileImageUrl;
    @ColumnInfo(name = "link")
    @NonNull
    private String mLink;
    @ColumnInfo(name = "page")
    private int mPage;
    @ColumnInfo(name = "ordinal")
    private int mOrder;
}
