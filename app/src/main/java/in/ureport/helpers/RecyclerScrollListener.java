package in.ureport.helpers;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import in.ureport.listener.FloatingActionButtonListener;

/**
 * Created by johncordeiro on 7/22/15.
 */
public class RecyclerScrollListener extends RecyclerView.OnScrollListener {

    private static final float MINIMUM = 25;

    private int scrolled = 0;
    private boolean visible = false;

    private FloatingActionButtonListener floatingActionButtonListener;

    public RecyclerScrollListener(@NonNull FloatingActionButtonListener floatingActionButtonListener) {
        this.floatingActionButtonListener = floatingActionButtonListener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if(floatingActionButtonListener == null) return;

        if (visible && scrolled < -MINIMUM) {
            floatingActionButtonListener.hideFloatingButton();
            scrolled = 0;
            visible = false;
        } else if (!visible && scrolled > MINIMUM) {
            floatingActionButtonListener.showFloatingButton();
            scrolled = 0;
            visible = true;
        }

        if ((visible && dy < 0) || (!visible && dy > 0)) {
            scrolled += dy;
        }
    }

}
