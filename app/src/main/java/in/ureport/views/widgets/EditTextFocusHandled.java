package in.ureport.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by johncordeiro on 11/09/15.
 */
public class EditTextFocusHandled extends EditText {

    public EditTextFocusHandled(Context context) {
        super(context);
    }

    public EditTextFocusHandled(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextFocusHandled(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            clearFocus();
        }
        return super.onKeyPreIme(keyCode, event);
    }

}
