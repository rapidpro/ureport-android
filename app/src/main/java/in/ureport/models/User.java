package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.StringRes;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

import in.ureport.R;

/**
 * Created by johncordeiro on 7/9/15.
 */
@Table(name = "User")
public class User extends Model implements Parcelable {

    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "birthday")
    private Date birthday;

    @Column(name = "country")
    private String country;

    @Column(name = "picture")
    private Integer picture;

    @Column(name = "gender")
    private Gender gender;

    @Column(name = "type")
    private Type type;

    @Column(name = "points")
    private Integer points;

    @Column(name = "stories")
    private Integer stories;

    @Column(name = "polls")
    private Integer polls;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getPicture() {
        return picture;
    }

    public void setPicture(Integer picture) {
        this.picture = picture;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Integer getPoints() {
        return points != null ? points : 0;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getStories() {
        return stories != null ? stories : 0;
    }

    public void setStories(Integer stories) {
        this.stories = stories;
    }

    public Integer getPolls() {
        return polls != null ? polls : 0;
    }

    public void setPolls(Integer polls) {
        this.polls = polls;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", birthday=" + birthday +
                ", country='" + country + '\'' +
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
        Ureport,
        Facebook,
        Twitter,
        Google
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        User user = (User) o;

        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + username.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.email);
        dest.writeString(this.username);
        dest.writeString(this.password);
        dest.writeLong(birthday != null ? birthday.getTime() : -1);
        dest.writeString(this.country);
        dest.writeValue(this.picture);
        dest.writeInt(this.gender == null ? -1 : this.gender.ordinal());
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeValue(this.points);
        dest.writeValue(this.stories);
        dest.writeValue(this.polls);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.email = in.readString();
        this.username = in.readString();
        this.password = in.readString();
        long tmpBirthday = in.readLong();
        this.birthday = tmpBirthday == -1 ? null : new Date(tmpBirthday);
        this.country = in.readString();
        this.picture = (Integer) in.readValue(Integer.class.getClassLoader());
        int tmpGender = in.readInt();
        this.gender = tmpGender == -1 ? null : User.Gender.values()[tmpGender];
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : User.Type.values()[tmpType];
        this.points = (Integer) in.readValue(Integer.class.getClassLoader());
        this.stories = (Integer) in.readValue(Integer.class.getClassLoader());
        this.polls = (Integer) in.readValue(Integer.class.getClassLoader());
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
