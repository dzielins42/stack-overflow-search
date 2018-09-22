package pl.dzielins42.stackoverflow.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.dzielins42.stackoverflow.R;
import pl.dzielins42.stackoverflow.database.model.Question;

public class QuestionAdapter extends ListAdapter<Question, QuestionAdapter.QuestionViewHolder> {

    private final Context mContext;

    QuestionAdapter(@NonNull Context context) {
        super(new QuestionDiffUtilItemCallback());

        mContext = context;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_question, parent, false
        );
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.profile_image)
        AppCompatImageView mProfileImage;
        @BindView(R.id.author_header)
        AppCompatTextView mAuthorHeader;
        @BindView(R.id.title)
        AppCompatTextView mTitle;
        @BindView(R.id.answer_count)
        AppCompatTextView mAnswerCount;

        QuestionViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bind(Question question) {
            if (!TextUtils.isEmpty(question.getAuthorProfileImageUrl())) {
                Picasso.get()
                        .load(question.getAuthorProfileImageUrl())
                        .placeholder(R.drawable.ic_person_placeholder)
                        .error(R.drawable.ic_person_error)
                        .into(mProfileImage);
            } else {
                mProfileImage.setImageResource(R.drawable.ic_person_placeholder);
            }
            mAuthorHeader.setText(getFormattedAuthorHeader(question.getAuthorDisplayName()));
            mTitle.setText(question.getTitle());
            mAnswerCount.setText(String.valueOf(question.getAnswerCount()));
        }

        private Spanned getFormattedAuthorHeader(@NonNull String authorDisplayName) {
            return Html.fromHtml(mContext.getString(R.string.author_header, authorDisplayName));
        }
    }

    private static class QuestionDiffUtilItemCallback extends DiffUtil.ItemCallback<Question> {

        @Override
        public boolean areItemsTheSame(@NonNull Question question1, @NonNull Question question2) {
            return question1.getId() == question2.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Question question1, @NonNull Question
                question2) {
            return question1.equals(question2);
        }
    }
}
