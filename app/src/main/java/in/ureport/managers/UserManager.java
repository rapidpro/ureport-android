package in.ureport.managers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.firebase.client.DataSnapshot;

import in.ureport.R;
import in.ureport.activities.LoginActivity;
import in.ureport.activities.MainActivity;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.listener.OnUserLoadedListener;
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
    private static String userRapidUuid = null;
    private static String userLanguage = null;
    private static String countryCode = null;
    private static String countryToken = null;
    private static Boolean master = false;
    private static Boolean moderator = false;

    private static Context context;

    public static void init(Context context) {
        UserManager.context = context;

        SystemPreferences systemPreferences = new SystemPreferences(context);
        userId = systemPreferences.getUserLoggedId();
        userRapidUuid = systemPreferences.getUserLoggedRapidUuid();
        userLanguage = systemPreferences.getUserLanguage();
        countryCode = systemPreferences.getCountryCode();
        countryToken = systemPreferences.getCountryToken();
        master = systemPreferences.isMaster();
        moderator = systemPreferences.isModerator();

        CountryProgramManager.switchCountryProgram(UserManager.getCountryCode());
    }

    public static boolean isUserCountryProgramEnabled() {
        Log.i(TAG, "isUserCountryProgramEnabled getCountryCode: " + getCountryCode());
        return getCountryCode() != null
            && getCountryCode().equals(CountryProgramManager.getCurrentCountryProgram().getCode());
    }

    public static void updateUserInfo(User user, final OnUserLoadedListener listener) {
        final UserServices userServices = new UserServices();
        userServices.keepUserOffline(user);

        UserManager.countryCode = user.getCountryProgram();

        CountryProgramManager.switchCountryProgram(countryCode);

        SystemPreferences systemPreferences = new SystemPreferences(context);
        systemPreferences.setCountryCode(countryCode);

        checkUserModeratorPermission(user, listener);
    }

    private static void checkUserModeratorPermission(final User user, final OnUserLoadedListener listener) {
        final UserServices userServices = new UserServices();
        final SystemPreferences systemPreferences = new SystemPreferences(context);

        userServices.isUserMaster(user, new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);

                UserManager.master = dataSnapshot.exists();
                systemPreferences.setMaster(UserManager.master);

                if (UserManager.master) {
                    listener.onUserLoaded();
                } else {
                    checkUserCountryModerator(user, listener);
                }
            }
        });
    }

    private static void checkUserCountryModerator(final User user, final OnUserLoadedListener listener) {
        final UserServices userServices = new UserServices();
        final SystemPreferences systemPreferences = new SystemPreferences(context);

        userServices.isUserCountryModerator(user, new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);

                UserManager.moderator = dataSnapshot.exists();
                systemPreferences.setModerator(UserManager.moderator);

                listener.onUserLoaded();
            }
        });
    }

    public static String getUserId() {
        return FirebaseManager.getReference().getAuth() != null
            && FirebaseManager.getReference().getAuth().getUid() != null ? FirebaseManager.getReference().getAuth().getUid() : null;
    }

    public static void updateUserRapidUuid(String userRapidUuid) {
        UserManager.userRapidUuid = userRapidUuid;

        SystemPreferences systemPreferences = new SystemPreferences(context);
        systemPreferences.setUserLoggedRapidUuid(userRapidUuid);
    }

    public static String getUserLanguage() {
        return userLanguage != null && !userLanguage.equals(SystemPreferences.USER_LANGUAGE_NOT_DEFINED)
                ? userLanguage : null;
    }

    public static void updateUserLanguage(String userLanguage) {
        UserManager.userLanguage = userLanguage;

        SystemPreferences systemPreferences = new SystemPreferences(context);
        systemPreferences.setUserLanguage(userLanguage);
    }

    public static String getUserRapidUuid() {
        return userRapidUuid;
    }

    public static void updateCountryToken(String countryToken) {
        UserManager.countryToken = countryToken;

        SystemPreferences systemPreferences = new SystemPreferences(context);
        systemPreferences.setCountryToken(countryToken != null ? countryToken : "");
    }

    public static String getCountryToken() {
        return countryToken;
    }

    public static boolean isCountryTokenValid() {
        return countryToken != null && !countryToken.isEmpty();
    }

    public static String getCountryCode() {
        return countryCode;
    }

    public static Boolean canModerate() {
        return isMaster() || (isModerator() && isUserCountryProgramEnabled());
    }

    public static Boolean isMaster() {
        return master;
    }

    private static Boolean isModerator() {
        return moderator;
    }

    public static boolean validateKeyAction(Context context) {
        if(!isUserLoggedIn()) {
            showLoginAlertValidation(context);
            return false;
        } else if(!isUserCountryProgram() && !isMaster()) {
            showCountryProgramAlert(context);
            return false;
        }
        return true;
    }

    public static boolean hasOldVersion() {
        Log.i(TAG, "hasOldVersion userId: " + userId);
        return userId != null && !userId.equals(SystemPreferences.USER_NO_LOGGED_ID);
    }

    public static void removeOldVersionFlag(Context context) {
        UserManager.userId = null;

        SystemPreferences systemPreferences = new SystemPreferences(context);
        systemPreferences.setUserLoggedId(SystemPreferences.USER_NO_LOGGED_ID);
    }

    public static boolean isUserLoggedIn() {
        return getUserId() != null;
    }

    public static boolean isUserRapidUuidValid() {
        return userRapidUuid != null && !userRapidUuid.equals(SystemPreferences.USER_NO_LOGGED_ID);
    }

    public static boolean isCountryCodeValid() {
        return countryCode != null && !countryCode.isEmpty();
    }

    public static void leaveFromGroup(final Activity activity, final ChatRoom chatRoom) {
        leaveFromGroup(activity, chatRoom, activity::finish);
    }

    public static void leaveFromGroup(final Activity activity, final ChatRoom chatRoom, OnRemoveChatListener onRemoveChatListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setMessage(R.string.chat_group_leave)
                .setNegativeButton(R.string.cancel_dialog_button, null)
                .setPositiveButton(R.string.confirm_neutral_dialog_button, (dialogInterface, i) -> {
                    User user = new User();
                    user.setKey(getUserId());
                    user.setCountryProgram(UserManager.getCountryCode());

                    ChatRoomServices chatRoomServices = new ChatRoomServices();
                    chatRoomServices.removeChatMember(activity, user, chatRoom.getKey());

                    onRemoveChatListener.onRemoveChat();
                }).create();
        alertDialog.show();
    }

    public static void logout(Context context) {
        UserManager.userId = null;
        UserManager.userRapidUuid = null;
        UserManager.countryCode = null;
        UserManager.countryToken = null;

        FirebaseManager.logout();

        SystemPreferences systemPreferences = new SystemPreferences(context);
        systemPreferences.setUserLoggedId(SystemPreferences.USER_NO_LOGGED_ID);
        systemPreferences.setUserLoggedRapidUuid(SystemPreferences.USER_NO_LOGGED_ID);
        systemPreferences.setCountryCode("");
        systemPreferences.setCountryToken("");
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

    public interface OnRemoveChatListener {
        void onRemoveChat();
    }
}
