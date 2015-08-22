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
import in.ureport.models.GroupChatRoom;
import in.ureport.models.User;
import in.ureport.network.ChatRoomServices;
import in.ureport.pref.SystemPreferences;

/**
 * Created by johncordeiro on 21/07/15.
 */
public class UserManager {

    private static final String TAG = "UserManager";

    public static boolean userLoggedIn = false;
    public static String countryCode = "";

    public static boolean validateKeyAction(Context context) {
        if(!UserManager.userLoggedIn) {
            showLoginAlertValidation(context);
            return false;
        } else if(!isUserCountryProgram()) {
            showCountryProgramAlert(context);
            return false;
        }
        return true;
    }

    public static void leaveFromGroup(final Activity activity, final ChatRoom chatRoom) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setMessage(R.string.chat_group_leave)
                .setNegativeButton(R.string.cancel_dialog_button, null)
                .setPositiveButton(R.string.confirm_neutral_dialog_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        User user = new User();
                        user.setKey(FirebaseManager.getAuthUserKey());

                        ChatRoomServices chatRoomServices = new ChatRoomServices();
                        chatRoomServices.removeChatMember(activity, user, chatRoom.getKey());

                        activity.finish();
                    }
                }).create();
        alertDialog.show();
    }

    public static void logout(Context context) {
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

    @NonNull
    public static List<User> getFakeUsers(Context context) {
        User user1 = new User();
        user1.setNickname("esther_aiken");
        user1.setEmail("estheraiken@gmail.com");
        user1.setGender(User.Gender.Female);
        user1.setType(User.Type.twitter);
        user1.setBirthday(new Date());
        user1.setCountry("Uganda");
        user1.setPicture(context.getResources().getResourceEntryName(R.drawable.user1));
        user1.setPolls(20);
        user1.setStories(10);
        user1.setPoints(user1.getPolls() + user1.getStories());

        User user2 = new User();
        user2.setNickname("domingos_hailey123");
        user2.setEmail("domingoshailey@gmail.com");
        user2.setGender(User.Gender.Male);
        user2.setType(User.Type.twitter);
        user2.setBirthday(new Date());
        user2.setCountry("Spain");
        user2.setPicture(context.getResources().getResourceEntryName(R.drawable.user2));
        user2.setPolls(25);
        user2.setStories(15);
        user2.setPoints(user2.getPolls() + user2.getStories());

        User user3 = new User();
        user3.setNickname("pauleenk12");
        user3.setEmail("pauleenk12@gmail.com");
        user3.setGender(User.Gender.Male);
        user3.setType(User.Type.facebook);
        user3.setBirthday(new Date());
        user3.setCountry("Uganda");
        user3.setPicture(context.getResources().getResourceEntryName(R.drawable.user3));
        user3.setPolls(30);
        user3.setStories(20);
        user3.setPoints(user3.getPolls() + user3.getStories());

        User user4 = new User();
        user4.setNickname("robertap");
        user4.setEmail("robertap@gmail.com");
        user4.setGender(User.Gender.Female);
        user4.setType(User.Type.facebook);
        user4.setBirthday(new Date());
        user4.setCountry("Brazil");
        user4.setPicture(context.getResources().getResourceEntryName(R.drawable.user4));
        user4.setPolls(1);
        user4.setStories(2);
        user4.setPoints(user4.getPolls() + user4.getStories());

        User user5 = new User();
        user5.setNickname("phil89");
        user5.setEmail("phil89@gmail.com");
        user5.setGender(User.Gender.Male);
        user5.setType(User.Type.facebook);
        user5.setBirthday(new Date());
        user5.setCountry("United States");
        user5.setPicture(context.getResources().getResourceEntryName(R.drawable.user5));
        user5.setPolls(10);
        user5.setStories(28);
        user5.setPoints(user5.getPolls() + user5.getStories());

        User user6 = new User();
        user6.setNickname("joshy");
        user6.setEmail("joshy@gmail.com");
        user6.setGender(User.Gender.Male);
        user6.setType(User.Type.ureport);
        user6.setBirthday(new Date());
        user6.setCountry("United States");
        user6.setPicture(context.getResources().getResourceEntryName(R.drawable.user6));
        user6.setPolls(5);
        user6.setStories(30);
        user6.setPoints(user6.getPolls() + user6.getStories());

        User user7 = new User();
        user7.setNickname("tommy20");
        user7.setEmail("tommy20@gmail.com");
        user7.setGender(User.Gender.Male);
        user7.setType(User.Type.google);
        user7.setBirthday(new Date());
        user7.setCountry("Nigeria");
        user7.setPicture(context.getResources().getResourceEntryName(R.drawable.user7));
        user7.setPolls(8);
        user7.setStories(16);
        user7.setPoints(user7.getPolls() + user7.getStories());

        User user8 = new User();
        user8.setNickname("maria_gloria");
        user8.setEmail("maria_gloria@gmail.com");
        user8.setGender(User.Gender.Female);
        user8.setType(User.Type.ureport);
        user8.setBirthday(new Date());
        user8.setCountry("Brazil");
        user8.setPicture(context.getResources().getResourceEntryName(R.drawable.user8));
        user8.setPolls(40);
        user8.setStories(100);
        user8.setPoints(user8.getPolls() + user8.getStories());

        User user9 = new User();
        user9.setNickname("easi");
        user9.setEmail("easi@gmail.com");
        user9.setGender(User.Gender.Male);
        user9.setType(User.Type.facebook);
        user9.setBirthday(new Date());
        user9.setCountry("Nigeria");
        user9.setPicture(context.getResources().getResourceEntryName(R.drawable.user9));
        user9.setPolls(10);
        user9.setStories(1);
        user9.setPoints(user9.getPolls() + user9.getStories());

        User user10 = new User();
        user10.setNickname("gonzalez1000");
        user10.setEmail("gonzalez1000@gmail.com");
        user10.setGender(User.Gender.Male);
        user10.setType(User.Type.google);
        user10.setBirthday(new Date());
        user10.setCountry("Mexico");
        user10.setPicture(context.getResources().getResourceEntryName(R.drawable.user10));
        user10.setPolls(10);
        user10.setStories(90);
        user10.setPoints(user10.getPolls() + user10.getStories());

        List<User> newUsers = new ArrayList<>();
        newUsers.add(user1);
        newUsers.add(user2);
        newUsers.add(user3);
        newUsers.add(user4);
        newUsers.add(user5);
        newUsers.add(user6);
        newUsers.add(user7);
        newUsers.add(user8);
        newUsers.add(user9);
        newUsers.add(user10);
        return newUsers;
    }

}
