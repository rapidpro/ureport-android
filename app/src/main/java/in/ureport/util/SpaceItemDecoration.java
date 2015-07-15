package in.ureport.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by johncordeiro on 7/15/15.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int horizontalSpaceWidth;

    public SpaceItemDecoration(int horizontalSpaceWidth) {
        this.horizontalSpaceWidth = horizontalSpaceWidth;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.right = horizontalSpaceWidth;
    }
}
