package in.ureport.managers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.ureport.R;
import in.ureport.activities.LoginActivity;
import in.ureport.activities.MainActivity;
import in.ureport.models.ChatRoom;
import in.ureport.models.User;
import in.ureport.network.ChatRoomServices;
import in.ureport.network.UserServices;
import in.ureport.pref.SystemPreferences;

/**
 * Created by johncordeiro on 21/07/15.
 */
public class UserManager {

    private static final String TAG = "UserManager";

    private static String userId = null;
    private static String countryCode = null;

    private static Context context;

    public static void init(Context context) {
        UserManager.context = context;

        SystemPreferences systemPreferences = new SystemPreferences(context);
        userId = systemPreferences.getUserLoggedId();
        countryCode = systemPreferences.getCountryCode();

        CountryProgramManager.switchCountryProgram(UserManager.getCountryCode());
    }

    public static boolean isCountryProgramEnabled() {
        return getCountryCode() != null
            && getCountryCode().equals(CountryProgramManager.getCurrentCountryProgram().getCode());
    }

    public static String getUserId() {
        return userId;
    }

    public static void updateUserInfo(User user) {
        UserServices userServices = new UserServices();
        userServices.keepUserOffline(user);

        UserManager.userId = user.getKey();
        UserManager.countryCode = user.getCountryProgram();

        CountryProgramManager.switchCountryProgram(countryCode);

        SystemPreferences systemPreferences = new SystemPreferences(context);
        systemPreferences.setCountryCode(countryCode);
        systemPreferences.setUserLoggedId(userId);
    }

    public static String getCountryCode() {
        return countryCode;
    }

    public static boolean validateKeyAction(Context context) {
        if(!isUserLoggedIn()) {
            showLoginAlertValidation(context);
            return false;
        } else if(!isUserCountryProgram()) {
            showCountryProgramAlert(context);
            return false;
        }
        return true;
    }

    public static boolean isUserLoggedIn() {
        return userId != null && !userId.equals(SystemPreferences.USER_NO_LOGGED_ID);
    }

    public static void leaveFromGroup(final Activity activity, final ChatRoom chatRoom) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setMessage(R.string.chat_group_leave)
                .setNegativeButton(R.string.cancel_dialog_button, null)
                .setPositiveButton(R.string.confirm_neutral_dialog_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        User user = new User();
                        user.setKey(userId);

                        ChatRoomServices chatRoomServices = new ChatRoomServices();
                        chatRoomServices.removeChatMember(activity, user, chatRoom.getKey());

                        activity.finish();
                    }
                }).create();
        alertDialog.show();
    }

    public static void logout(Context context) {
        UserManager.userId = null;
        UserManager.countryCode = null;

        FirebaseManager.logout();

        SystemPreferences systemPreferences = new SystemPreferences(context);
        systemPreferences.setUserLoggedId(SystemPreferences.USER_NO_LOGGED_ID);
        systemPreferences.setCountryCode("");

        startLoginFlow(context);
    }

    public static void startLoginFlow(Context context) {
        Intent backIntent = new Intent(context, MainActivity.class);
        backIntent.putExtra(MainActivity.EXTRA_FORCED_LOGIN, true);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(backIntent);
    }

    private static boolean isUserCountryProgram() {
        return countryCode == null || countryCode.length() == 0
        || CountryProgramManager.getCurrentCountryProgram().equals(CountryProgramManager.getCountryProgramForCode(countryCode));
    }

    private static void showCountryProgramAlert(Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(context.getString(R.string.country_program_validation_error))
                .setPositiveButton(R.string.confirm_neutral_dialog_button, null)
                .create();
        alertDialog.show();
    }

    private static void showLoginAlertValidation(final Context context) {
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
