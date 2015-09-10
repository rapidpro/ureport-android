package in.ureport.helpers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by johncordeiro on 08/09/15.
 */
public class KeyboardHandler {

    public void showSoftwareKeyboard(@NonNull Activity activity, boolean showKeyboard){
        final InputMethodManager inputManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), showKeyboard ? InputMethodManager.SHOW_FORCED : InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
