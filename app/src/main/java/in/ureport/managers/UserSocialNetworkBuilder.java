package in.ureport.managers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.plus.model.people.Person;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterSession;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import in.ureport.models.User;

/**
 * Created by johncordeiro on 12/08/15.
 */
public class UserSocialNetworkBuilder {

    @NonNull
    public User buildUserFromTwitter(Result<com.twitter.sdk.android.core.models.User> result, TwitterSession session) {
        User user = new User();
        user.setType(User.Type.Twitter);
        user.setNickname(session.getUserName());
        user.setEmail(result.data.email);
        user.setPicture(result.data.profileImageUrl);
        return user;
    }

    public User buildUserFromGoogle(Person currentPerson, String email) {
        User user = new User();
        user.setType(User.Type.Google);

        user.setNickname(getFormattedNickname(currentPerson.getDisplayName()));

        Person.Image image = currentPerson.getImage();
        user.setPicture(image != null ? image.getUrl() : null);

        user.setEmail(email);

        String birthday = currentPerson.getBirthday();
        user.setBirthday(getFormattedDate(birthday, "yyyy-MM-dd"));

        user.setGender(userUserGenderByGooglePerson(currentPerson));

        return user;
    }

    @NonNull
    private User.Gender userUserGenderByGooglePerson(Person currentPerson) {
        User.Gender gender = User.Gender.Male;
        if(currentPerson.hasGender()) {
            gender = currentPerson.getGender() == Person.Gender.MALE ? User.Gender.Male : User.Gender.Female;
        }
        return gender;
    }

    @NonNull
    public User buildUserFromFacebook(JSONObject jsonObject) {
        User user = new User();
        user.setType(User.Type.Facebook);
        user.setEmail(jsonObject.optString("email"));

        String name = jsonObject.optString("name");
        user.setNickname(getFormattedNickname(name));

        String birthday = jsonObject.optString("birthday");
        user.setBirthday(getFormattedDate(birthday, "MM/dd/yyyy"));

        String gender = jsonObject.optString("gender");
        user.setGender(getUserGender(gender));

        JSONObject pictureData = jsonObject.optJSONObject("picture");
        user.setPicture(getPictureFromFacebook(pictureData));

        return user;
    }

    @Nullable
    private String getPictureFromFacebook(JSONObject pictureData) {
        return pictureData != null && pictureData.has("data")
                ? pictureData.optJSONObject("data").optString("url") : null;
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

}
