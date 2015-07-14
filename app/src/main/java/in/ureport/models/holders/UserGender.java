package in.ureport.models.holders;

import android.content.Context;

import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/9/15.
 */
public class UserGender {

    private Context context;
    private User.Gender gender;

    public UserGender(Context context, User.Gender gender) {
        this.context = context;
        this.gender = gender;
    }

    public User.Gender getGender() {
        return gender;
    }

    @Override
    public String toString() {
        return context.getString(gender.getStringResource());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserGender that = (UserGender) o;
        return gender == that.gender;

    }

    @Override
    public int hashCode() {
        return gender.hashCode();
    }
}
