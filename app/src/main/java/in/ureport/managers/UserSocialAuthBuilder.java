package in.ureport.managers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;

import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

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
            case google:
                return buildUserFromGoogle(authResult);
        }
        return null;
    }

    @NonNull
    private User buildUserFromTwitter(AuthResult authResult) {
        final String profileImageUrl = (String) authResult.getAdditionalUserInfo()
                .getProfile().get("profile_image_url");
        final User user = new User();

        user.setKey(authResult.getUser().getUid());
        user.setType(User.Type.twitter);
        user.setNickname(authResult.getAdditionalUserInfo().getUsername());
        if (profileImageUrl != null) {
            user.setPicture(getBiggerTwitterProfilePicture(profileImageUrl));
        }
        return user;
    }

    private String getBiggerTwitterProfilePicture(String profileImageUrl) {
        return profileImageUrl.replace("_normal", "_bigger");
    }

    @NonNull
    private User buildUserFromFacebook(AuthResult authResult) {
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
        user.setNickname(nickname == null ? "" : getFormattedNickname(nickname));
        user.setGenderAsEnum(getUserGender(getStringValue(additionalUserInfo.getProfile(), "gender")));
        user.setPicture(getStringValue(additionalUserInfo.getProfile(), "profileImageURL"));

        return user;
    }

    private User buildUserFromGoogle(AuthResult authResult) {
        final String displayName = authResult.getUser().getDisplayName();
        final String pictureUrl = (String) authResult.getAdditionalUserInfo().getProfile().get("picture");
        final User user = new User();

        user.setKey(authResult.getUser().getUid());
        user.setType(User.Type.google);
        if (displayName != null) {
            user.setNickname(getFormattedNickname(displayName));
        }
        user.setPicture(pictureUrl);

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
    private String getStringValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }

}
