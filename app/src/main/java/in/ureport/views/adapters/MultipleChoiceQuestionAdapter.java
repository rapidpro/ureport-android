package in.ureport.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import in.ureport.R;
import in.ureport.listener.PollQuestionAnswerListener;
import in.ureport.models.MultipleChoiceQuestion;

/**
 * Created by johncordeiro on 7/17/15.
 */
public class MultipleChoiceQuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_RESPOND = 1;

    private static final int NO_CHOSEN_YET = -1;

    private int chosen = NO_CHOSEN_YET;

    private PollQuestionAnswerListener pollQuestionAnswerListener;
    private MultipleChoiceQuestion multipleChoiceQuestion;

    public MultipleChoiceQuestionAdapter(MultipleChoiceQuestion multipleChoiceQuestion) {
        this.multipleChoiceQuestion = multipleChoiceQuestion;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch(viewType) {
            case TYPE_ITEM:
                return new ItemViewHolder(inflater.inflate(R.layout.item_choice, parent, false));
            case TYPE_RESPOND:
                return new RespondViewHolder(inflater.inflate(R.layout.item_respond_poll_question, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch(getItemViewType(position)) {
            case TYPE_ITEM:
                ((ItemViewHolder)holder).bindView(multipleChoiceQuestion.getChoices().get(position));
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == multipleChoiceQuestion.getChoices().size()) {
            return TYPE_RESPOND;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return multipleChoiceQuestion.getChoices().size() + 1;
    }

    public void setPollQuestionAnswerListener(PollQuestionAnswerListener pollQuestionAnswerListener) {
        this.pollQuestionAnswerListener = pollQuestionAnswerListener;
    }

    private class RespondViewHolder extends RecyclerView.ViewHolder {

        public RespondViewHolder(View itemView) {
            super(itemView);

            Button respond = (Button) itemView.findViewById(R.id.respond);
            respond.setOnClickListener(onRespondClickListener);
        }

        private View.OnClickListener onRespondClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chosen == NO_CHOSEN_YET) {
                    Context context = view.getContext();
                    Toast.makeText(context, context.getString(R.string.answer_poll_choose_error), Toast.LENGTH_LONG)
                            .show();
                } else if(pollQuestionAnswerListener != null) {
                    multipleChoiceQuestion.setAnswer(multipleChoiceQuestion.getChoices().get(chosen));
                    pollQuestionAnswerListener.onQuestionAnswered(multipleChoiceQuestion);
                }
            }
        };
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private RadioButton check;

        public ItemViewHolder(View itemView) {
            super(itemView);

            check = (RadioButton) itemView.findViewById(R.id.check);
            check.setOnClickListener(onCheckClickListener);
        }

        public void bindView(String choice) {
            check.setChecked(chosen == getLayoutPosition());
            check.setText(choice);
        }

        private View.OnClickListener onCheckClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int lastChosen = chosen;

                chosen = getLayoutPosition();
                notifyItemChanged(chosen);

                if (lastChosen != NO_CHOSEN_YET)
                    notifyItemChanged(lastChosen);
            }
        };
    }
}
