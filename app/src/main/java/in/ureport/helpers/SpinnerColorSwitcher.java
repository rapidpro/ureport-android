package in.ureport.helpers;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.widget.Spinner;

/**
 * Created by johncordeiro on 22/07/15.
 */
public class SpinnerColorSwitcher {

    private Context context;

    public SpinnerColorSwitcher(Context context) {
        this.context = context;
    }

    public void switchToColor(Spinner spinner, @ColorRes int color) {
        Drawable spinnerDrawable = spinner.getBackground().getConstantState().newDrawable();
        spinnerDrawable.setColorFilter(context.getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            spinner.setBackground(spinnerDrawable);
        } else {
            spinner.setBackgroundDrawable(spinnerDrawable);
        }
    }

}
