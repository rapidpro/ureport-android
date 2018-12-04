package in.ureport.managers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;

import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

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

    public User build(AuthResult authResult) {
        String provider = authResult.getAdditionalUserInfo().getProviderId().replace(".com", "");
        switch (User.Type.valueOf(provider)) {
            case twitter:
                return buildUserFromTwitter(authResult);
            case facebook:
                return buildUserFromFacebook(authResult);
//            case google:
//                return buildUserFromGoogle(authData);
        }
        return null;
    }

    @NonNull
    public User buildUserFromTwitter(AuthResult authResult) {
        User user = new User();
        user.setKey(authResult.getUser().getUid());
        user.setType(User.Type.twitter);
        user.setNickname(authResult.getAdditionalUserInfo().getUsername());
//        user.setPicture(getBiggerTwitterProfilePicture(getStringValue(data, "profileImageURL")));
        return user;
    }

//    private String getBiggerTwitterProfilePicture(String profileImageUrl) {
//        return profileImageUrl.replace("_normal", "_bigger");
//    }
//
//    public User buildUserFromGoogle(AuthData authData) {
//        Map<String, Object> data = authData.getProviderData();
//        Map<String, Object> cachedUserProfile = (Map<String, Object>) data.get("cachedUserProfile");
//
//        User user = new User();
//        user.setKey(authData.getUid());
//        user.setType(User.Type.google);
//        user.setNickname(getFormattedNickname(getStringValue(data, "displayName")));
//        user.setPicture(getStringValue(data, "profileImageURL"));
//        user.setGenderAsEnum(getUserGender(getStringValue(cachedUserProfile, "gender")));
//
//        return user;
//    }
//

    @NonNull
    public User buildUserFromFacebook(AuthResult authResult) {
        final FirebaseUser userInfo = authResult.getUser();
        final AdditionalUserInfo additionalUserInfo = authResult.getAdditionalUserInfo();
        final User user = new User();

        String pictureUrl;
        try {
            final Map<String, Object> pictureData = ((ArrayMap<String, Object>)
                    ((ArrayMap<String, Object>) additionalUserInfo.getProfile().get("picture")).get("data"));
            pictureUrl = (String) pictureData.get("url");
        } catch (Exception e) {
            pictureUrl = "";
        }

        user.setKey(userInfo.getUid());
        user.setType(User.Type.facebook);
        user.setEmail(getStringValue(additionalUserInfo.getProfile(), "email"));
        user.setPicture(pictureUrl);

        final String nickname = getStringValue(additionalUserInfo.getProfile(), "name");
        final Date birthday = getFormattedDate(getStringValue(authResult.getAdditionalUserInfo()
                .getProfile(), "birthday"), "MM/dd/yyyy");

        user.setNickname(nickname == null ? "" : getFormattedNickname(nickname));
        user.setBirthday(birthday == null ? 0 : birthday.getTime());
        user.setGenderAsEnum(getUserGender(getStringValue(additionalUserInfo.getProfile(), "gender")));
        user.setPicture(getStringValue(additionalUserInfo.getProfile(), "profileImageURL"));

        return user;
    }

    private User.Gender getUserGender(String gender) {
        if (gender != null && gender.equals("female"))
            return User.Gender.Female;
        return User.Gender.Male;
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
