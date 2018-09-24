package pl.dzielins42.stackoverflow.view;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
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

public class QuestionAdapter extends PagedListAdapter<Question, RecyclerView.ViewHolder> {

    @NonNull
    private final Context mContext;

    private boolean mLoadingPage = false;

    QuestionAdapter(@NonNull Context context) {
        super(new QuestionDiffUtilItemCallback());

        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == R.layout.item_question) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_question, parent, false
            );
            return new QuestionViewHolder(view);
        } else {
            final View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_status, parent, false
            );
            return new StatusItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == R.layout.item_question) {
            ((QuestionViewHolder) holder).bind(getItem(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mLoadingPage && position == getItemCount() - 1) {
            return R.layout.item_status;
        } else {
            return R.layout.item_question;
        }
    }

    void setLoading(boolean isLoadingPage) {
        if (isLoadingPage == mLoadingPage) {
            return;
        }
        mLoadingPage = isLoadingPage;
        if (!mLoadingPage) {
            notifyItemRemoved(super.getItemCount());
        } else {
            notifyItemInserted(super.getItemCount());
        }
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

        private Question mQuestion;

        QuestionViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse(mQuestion.getLink()));
                mContext.startActivity(intent);
            });
        }

        void bind(Question question) {
            mQuestion = question;

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
            mTitle.setText(Html.fromHtml(question.getTitle()));
            mAnswerCount.setText(String.valueOf(question.getAnswerCount()));
        }

        private Spanned getFormattedAuthorHeader(@NonNull String authorDisplayName) {
            return Html.fromHtml(mContext.getString(R.string.author_header, authorDisplayName));
        }
    }

    public class StatusItemViewHolder extends RecyclerView.ViewHolder {

        StatusItemViewHolder(@NonNull View itemView) {
            super(itemView);
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
