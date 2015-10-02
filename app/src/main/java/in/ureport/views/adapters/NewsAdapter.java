package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.ureport.R;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.PrototypeManager;
import in.ureport.models.News;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/17/15.
 */
public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<News> newsList;
    private User user;

    private NewsListener newsListener;

    public NewsAdapter(List<News> newsList, User user) {
        this.newsList = newsList;
        this.user = user;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch(viewType) {
            case TYPE_HEADER:
                return new HeaderViewHolder(inflater.inflate(R.layout.item_header_news, parent, false));
            default:
                return new ItemViewHolder(inflater.inflate(R.layout.item_news, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch(getItemViewType(position)) {
            case TYPE_HEADER:
                ((HeaderViewHolder)holder).bindView();
                break;
            case TYPE_ITEM:
                ((ItemViewHolder)holder).bindView(newsList.get(position-1));
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView countryProgram;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            countryProgram = (TextView) itemView.findViewById(R.id.countryProgram);
            itemView.setOnClickListener(onStatisticsClickListener);
        }

        private View.OnClickListener onStatisticsClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newsListener != null)
                    newsListener.onAddStatistics();
            }
        };

        private void bindView() {
            if(user != null) {
                countryProgram.setText(CountryProgramManager.getCurrentCountryProgram().getName());
            }
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView description;
        private final TextView category;
        private TextView title;
        private TextView author;
        private ImageView cover;

        public ItemViewHolder(View itemView) {
            super(itemView);

            category = (TextView) itemView.findViewById(R.id.category);
            cover = (ImageView) itemView.findViewById(R.id.cover);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            author = (TextView) itemView.findViewById(R.id.author);

            itemView.setOnClickListener(onNewsClickListener);

            Button share = (Button) itemView.findViewById(R.id.share);
            share.setOnClickListener(onShareClickListener);
        }

        private View.OnClickListener onNewsClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newsListener != null)
                    newsListener.onReadNews(newsList.get(getLayoutPosition() - 1));
            }
        };

        private void bindView(News news) {
            category.setText(news.getCategory().getName());
            title.setText(news.getTitle());
            description.setText(news.getSummary());
            author.setText(itemView.getContext().getString(R.string.stories_list_item_author, news.getTags()));
        }

        private View.OnClickListener onShareClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrototypeManager.showPrototypeAlert(itemView.getContext());
            }
        };
    }

    public void setUser(User user) {
        this.user = user;
        notifyItemChanged(0);
    }

    public void setNewsListener(NewsListener newsListener) {
        this.newsListener = newsListener;
    }

    public static interface NewsListener {
        void onAddStatistics();
        void onReadNews(News news);
    }
}
