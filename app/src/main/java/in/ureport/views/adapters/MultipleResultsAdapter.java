package in.ureport.views.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.ilhasoft.support.tool.UnitConverter;
import in.ureport.R;
import in.ureport.models.ItemChoice;

/**
 * Created by johncordeiro on 15/09/15.
 */
public class MultipleResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ItemChoice> choicesResults;
    private String [] colors;

    public MultipleResultsAdapter(List<ItemChoice> choicesResults, String[] colors) {
        this.choicesResults = choicesResults;
        this.colors = colors;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_choice_result, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).bindView(choicesResults.get(position));
    }

    @Override
    public int getItemCount() {
        return choicesResults.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private static final int SCREEN_WIDTH_OFFSET = 80;

        private final TextView title;
        private final View bar;
        private final TextView value;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            bar = itemView.findViewById(R.id.bar);
            value = (TextView) itemView.findViewById(R.id.value);
        }

        private void bindView(ItemChoice itemChoice) {
            title.setText(itemChoice.getTitle());

            String resultValue = itemView.getContext().getString(R.string.choice_result_value, itemChoice.getValue());
            value.setText(resultValue);
            value.setTextColor(getColor());

            bindBar(itemChoice);
        }

        private void bindBar(ItemChoice itemChoice) {
            UnitConverter unitConverter = new UnitConverter(itemView.getContext());

            int widthPixels = itemView.getContext().getResources().getDisplayMetrics().widthPixels
                    - (int)unitConverter.convertDpToPx(SCREEN_WIDTH_OFFSET);
            int barWidth = (widthPixels * itemChoice.getValue())/100;

            ViewGroup.LayoutParams params = bar.getLayoutParams();
            params.width = barWidth;

            bar.setLayoutParams(params);
            bar.setBackgroundColor(getColor());
        }

        private int getColor() {
            return Color.parseColor(colors[getLayoutPosition() % colors.length]);
        }
    }
}
