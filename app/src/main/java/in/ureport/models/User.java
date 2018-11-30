package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.StringRes;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.Expose;

import java.util.Date;
import java.util.HashMap;

import in.ureport.R;

/**
 * Created by johncordeiro on 7/9/15.
 */
public class User implements Parcelable {

    @Expose
    private String key;

    private String email;

    @Expose
    private String nickname;

    private Long birthday;

    private String country;

    private String countryProgram;

    private String state;

    private String district;

    @Expose
    private String picture;

    private String gender;

    private Type type;

    private Integer points;

    private Integer stories;

    private String pushIdentity;

    private HashMap<String, Boolean> chatRooms;

    private Boolean publicProfile = true;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryProgram() {
        return countryProgram;
    }

    public void setCountryProgram(String countryProgram) {
        this.countryProgram = countryProgram;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Exclude
    public Gender getGenderAsEnum() {
        try {
            return Gender.valueOf(this.gender);
        } catch (Exception exception) {
            return Gender.Male;
        }
    }

    public void setGenderAsEnum(Gender gender) {
        this.gender = gender.name();
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getStories() {
        return stories;
    }

    public void setStories(Integer stories) {
        this.stories = stories;
    }

    public String getPushIdentity() {
        return pushIdentity;
    }

    public void setPushIdentity(String pushIdentity) {
        this.pushIdentity = pushIdentity;
    }

    public HashMap<String, Boolean> getChatRooms() {
        return chatRooms;
    }

    public void setChatRooms(HashMap<String, Boolean> chatRooms) {
        this.chatRooms = chatRooms;
    }

    public Boolean getPublicProfile() {
        return publicProfile;
    }

    public void setPublicProfile(Boolean publicProfile) {
        this.publicProfile = publicProfile;
    }

    @Override
    public String toString() {
        return "User{" +
                "key='" + key + '\'' +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", birthday=" + birthday +
                ", country='" + country + '\'' +
                ", picture='" + picture + '\'' +
                ", type=" + type +
                ", gender=" + gender +
                '}';
    }

    public enum Gender {
        Male(R.string.user_gender_male),
        Female(R.string.user_gender_female);

        private int value;

        Gender(@StringRes int value) {
            this.value = value;
        }

        public int getStringResource() {
            return value;
        }
    }

    public enum Type {
        ureport,
        facebook,
        twitter,
        google
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;
        return key.equals(user.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.email);
        dest.writeString(this.nickname);
        dest.writeLong(birthday);
        dest.writeString(this.country);
        dest.writeString(this.countryProgram);
        dest.writeString(this.state);
        dest.writeString(this.district);
        dest.writeString(this.picture);
        dest.writeString(this.gender);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeValue(this.points);
        dest.writeValue(this.stories);
        dest.writeString(this.pushIdentity);
        dest.writeSerializable(this.chatRooms);
        dest.writeValue(this.publicProfile);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.key = in.readString();
        this.email = in.readString();
        this.nickname = in.readString();
        this.birthday = in.readLong();
        this.country = in.readString();
        this.countryProgram = in.readString();
        this.state = in.readString();
        this.district = in.readString();
        this.picture = in.readString();
        this.gender = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : User.Type.values()[tmpType];
        this.points = (Integer) in.readValue(Integer.class.getClassLoader());
        this.stories = (Integer) in.readValue(Integer.class.getClassLoader());
        this.pushIdentity = in.readString();
        this.chatRooms = (HashMap<String, Boolean>) in.readSerializable();
        this.publicProfile = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
