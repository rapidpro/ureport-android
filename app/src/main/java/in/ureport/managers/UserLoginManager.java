package in.ureport.managers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import in.ureport.R;
import in.ureport.activities.LoginActivity;

/**
 * Created by johncordeiro on 21/07/15.
 */
public class UserLoginManager {

    public static boolean userLoggedIn = false;

    public static boolean validateUserLogin(Context context) {
        if(!UserLoginManager.userLoggedIn) {
            showAlertValidation(context);
        }
        return UserLoginManager.userLoggedIn;
    }

    private static void showAlertValidation(final Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(R.string.login_validation_error_message)
                .setNegativeButton(R.string.cancel_dialog_button, null)
                .setPositiveButton(R.string.confirm_neutral_dialog_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent loginIntent = new Intent(context, LoginActivity.class);
                        context.startActivity(loginIntent);
                    }
                }).create();
        alertDialog.show();
    }

}
