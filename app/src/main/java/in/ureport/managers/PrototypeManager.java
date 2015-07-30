package in.ureport.managers;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import in.ureport.R;

/**
 * Created by johncordeiro on 7/29/15.
 */
public class PrototypeManager {

    public static void showPrototypeAlert(Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage("This function is not implemented yet in this prototype. Wait for the next version.")
                .setNeutralButton(R.string.confirm_neutral_dialog_button, null).create();
        alertDialog.show();
    }

}
