package pl.dzielins42.stackoverflow.interactor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import javax.inject.Inject;

import io.reactivex.Completable;
import pl.dzielins42.stackoverflow.database.model.Question;

public class QuestionClickedInteractor {

    private static final String TAG = QuestionClickedInteractor.class.getSimpleName();

    private final Context mContext;

    @Inject
    public QuestionClickedInteractor(Context context) {
        mContext = context;
    }

    public Completable handleQuestionClicked(@NonNull Question question) {
        return Completable.create(emitter -> {
            Log.d(TAG, "handleQuestionClicked: ");
            Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(question.getLink()));
            mContext.startActivity(intent);
            emitter.onComplete();
        });
    }
}
