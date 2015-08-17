package in.ureport.managers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.firebase.client.AuthData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import in.ureport.models.User;

/**
 * Created by johncordeiro on 12/08/15.
 */
public class UserSocialAuthBuilder {

    public User build(AuthData authData) {
        String provider = authData.getProvider();
        switch(User.Type.valueOf(provider)) {
            case twitter:
                return buildUserFromTwitter(authData);
            case facebook:
                return buildUserFromFacebook(authData);
            case google:
                return buildUserFromGoogle(authData);
        }
        return null;
    }

    @NonNull
    public User buildUserFromTwitter(AuthData authData) {
        Map<String, Object> data = authData.getProviderData();

        User user = new User();
        user.setKey(authData.getUid());
        user.setType(User.Type.twitter);
        user.setNickname(getStringValue(data, "username"));
        user.setPicture(getBiggerTwitterProfilePicture(getStringValue(data, "profileImageURL")));
        return user;
    }

    private String getBiggerTwitterProfilePicture(String profileImageUrl) {
        return profileImageUrl.replace("_normal", "_bigger");
    }

    public User buildUserFromGoogle(AuthData authData) {
        Map<String, Object> data = authData.getProviderData();
        Map<String, Object> cachedUserProfile = (Map<String, Object>) data.get("cachedUserProfile");

        User user = new User();
        user.setKey(authData.getUid());
        user.setType(User.Type.google);
        user.setNickname(getFormattedNickname(getStringValue(data, "displayName")));
        user.setPicture(getStringValue(data, "profileImageURL"));
        user.setGender(getUserGender(getStringValue(cachedUserProfile, "gender")));

        return user;
    }

    @NonNull
    public User buildUserFromFacebook(AuthData authData) {
        Map<String, Object> data = authData.getProviderData();
        Map<String, Object> cachedUserProfile = (Map<String, Object>) authData.getProviderData().get("cachedUserProfile");

        User user = new User();
        user.setKey(authData.getUid());
        user.setType(User.Type.facebook);
        user.setEmail(getStringValue(data, "email"));
        user.setNickname(getFormattedNickname(getStringValue(data, "displayName")));
        user.setPicture(getStringValue(data, "profileImageURL"));
        user.setBirthday(getFormattedDate(getStringValue(cachedUserProfile, "birthday"), "MM/dd/yyyy"));
        user.setGender(getUserGender(getStringValue(cachedUserProfile, "gender")));

        return user;
    }

    @Nullable
    private User.Gender getUserGender(String gender) {
        if(gender != null && !gender.isEmpty())
            return gender.equals("male") ? User.Gender.Male : User.Gender.Female;
        return null;
    }

    private String getFormattedNickname(String name) {
        return name.replace(" ", "");
    }

    @Nullable
    private Date getFormattedDate(String birthdayDate, String format) {
        try {
            DateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
            return dateFormat.parse(birthdayDate);
        } catch (Exception exception) {
            return null;
        }
    }

    @Nullable
    private String getStringValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }

}
