package in.ureport.views.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.marcorei.infinitefire.InfiniteFireArray;

/**
 * Created by john-mac on 8/22/16.
 */
public class InfiniteFireLinearRecyclerView extends RecyclerView {

    private InfiniteFireArray infiniteFireArray;

    public InfiniteFireLinearRecyclerView(Context context) {
        super(context);
        init();
    }

    public InfiniteFireLinearRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InfiniteFireLinearRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        addOnScrollListener(onScrollListener);
    }

    private OnScrollListener onScrollListener = new OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy >= 0) {
                return;
            }
            if (((LinearLayoutManager)getLayoutManager()).findLastVisibleItemPosition() < infiniteFireArray.getCount() - 20) {
                return;
            }
            infiniteFireArray.more();
        }
    };

    public void setInfiniteFireArray(InfiniteFireArray infiniteFireArray) {
        this.infiniteFireArray = infiniteFireArray;
    }
}