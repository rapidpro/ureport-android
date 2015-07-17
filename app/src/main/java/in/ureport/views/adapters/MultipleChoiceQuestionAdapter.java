package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import java.util.List;

import in.ureport.R;

/**
 * Created by johncordeiro on 7/17/15.
 */
public class MultipleChoiceQuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_RESPOND = 1;

    private static final int NO_CHOSEN_YET = -1;

    private List<String> choices;
    private int chosen = NO_CHOSEN_YET;

    public MultipleChoiceQuestionAdapter(List<String> choices) {
        this.choices = choices;
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
                ((ItemViewHolder)holder).bindView(choices.get(position));
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == choices.size()) {
            return TYPE_RESPOND;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return choices.size() + 1;
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

            }
        };
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private CheckBox check;

        public ItemViewHolder(View itemView) {
            super(itemView);

            check = (CheckBox) itemView.findViewById(R.id.check);
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
