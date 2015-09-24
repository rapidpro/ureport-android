package in.ureport.models.rapidpro;

import java.util.Date;

/**
 * Created by johncordeiro on 18/08/15.
 */
public class ContactFields {

    private String lga;

    private String nickname;

    private String gender;

    private Date registration_date;

    private String state;

    private String born;

    private String token;

    private Date birthday;

    private String ward;

    private String settlement;

    private String email;

    private String occupation;

    public String getLga() {
        return lga;
    }

    public void setLga(String lga) {
        this.lga = lga;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getRegistration_date() {
        return registration_date;
    }

    public void setRegistration_date(Date registration_date) {
        this.registration_date = registration_date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getBorn() {
        return born;
    }

    public void setBorn(String born) {
        this.born = born;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getSettlement() {
        return settlement;
    }

    public void setSettlement(String settlement) {
        this.settlement = settlement;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    @Override
    public String toString() {
        return "ContactFields{" +
                "lga='" + lga + '\'' +
                ", nickname='" + nickname + '\'' +
                ", gender='" + gender + '\'' +
                ", registration_date=" + registration_date +
                ", state='" + state + '\'' +
                ", born='" + born + '\'' +
                ", token='" + token + '\'' +
                ", birthday=" + birthday +
                ", ward='" + ward + '\'' +
                ", settlement='" + settlement + '\'' +
                ", email='" + email + '\'' +
                ", occupation='" + occupation + '\'' +
                '}';
    }
}
