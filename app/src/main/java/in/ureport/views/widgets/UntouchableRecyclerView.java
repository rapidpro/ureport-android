package in.ureport.views.widgets;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by johndalton on 14/02/15.
 */
public class UntouchableRecyclerView extends RecyclerView {
    public UntouchableRecyclerView(Context context) {
        super(context);
    }

    public UntouchableRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UntouchableRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        onTouchEvent(event);
        return false;
    }
}
