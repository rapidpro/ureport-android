package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.StringRes;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMarshalling;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.Date;

import in.ureport.R;
import in.ureport.models.converters.EnumTypeConverter;

/**
 * Created by johncordeiro on 7/9/15.
 */
@DynamoDBTable(tableName = "User")
@Table(name = "User")
public class User extends Model implements Parcelable {

    @Column(name = "identityId")
    private String identityId;

    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String nickname;

    @Column(name = "password")
    private String password;

    @Column(name = "birthday")
    private Date birthday;

    @Column(name = "country")
    private String country;

    @Column(name = "picture")
    private String picture;

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

    @DynamoDBHashKey(attributeName = "id")
    public String getIdentityId() {
        return identityId;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    @DynamoDBIndexHashKey(attributeName="email", globalSecondaryIndexName="email-index")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDBIndexHashKey(attributeName="nickname", globalSecondaryIndexName="nickname-index")
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @DynamoDBAttribute(attributeName = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @DynamoDBAttribute(attributeName = "birthday")
    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @DynamoDBAttribute(attributeName = "country")
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @DynamoDBAttribute(attributeName = "picture")
    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @DynamoDBAttribute(attributeName = "gender")
    @DynamoDBMarshalling(marshallerClass = EnumTypeConverter.class)
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @DynamoDBAttribute(attributeName = "type")
    @DynamoDBMarshalling(marshallerClass = EnumTypeConverter.class)
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @DynamoDBAttribute(attributeName = "points")
    public Integer getPoints() {
        return points != null ? points : 0;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    @DynamoDBAttribute(attributeName = "stories")
    public Integer getStories() {
        return stories != null ? stories : 0;
    }

    public void setStories(Integer stories) {
        this.stories = stories;
    }

    @DynamoDBAttribute(attributeName = "polls")
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
                ", nickname='" + nickname + '\'' +
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

        User user = (User) o;
        return identityId.equals(user.identityId);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + identityId.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.identityId);
        dest.writeString(this.email);
        dest.writeString(this.nickname);
        dest.writeString(this.password);
        dest.writeLong(birthday != null ? birthday.getTime() : -1);
        dest.writeString(this.country);
        dest.writeString(this.picture);
        dest.writeInt(this.gender == null ? -1 : this.gender.ordinal());
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeValue(this.points);
        dest.writeValue(this.stories);
        dest.writeValue(this.polls);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.identityId = in.readString();
        this.email = in.readString();
        this.nickname = in.readString();
        this.password = in.readString();
        long tmpBirthday = in.readLong();
        this.birthday = tmpBirthday == -1 ? null : new Date(tmpBirthday);
        this.country = in.readString();
        this.picture = in.readString();
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
