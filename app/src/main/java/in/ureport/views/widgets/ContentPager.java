package in.ureport.views.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by johndalton on 24/01/15.
 */
public class ContentPager extends ViewPager {

    private boolean pagingEnabled = true;

    public ContentPager(Context context) {
        super(context);
    }

    public ContentPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.pagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.pagingEnabled && super.onInterceptTouchEvent(event);
    }

    public void setPagingEnabled(boolean pagingEnabled) {
        this.pagingEnabled = pagingEnabled;
    }

}
