package in.ureport.models.holders;

import android.content.Context;

import in.ureport.models.User;

/**
 * Created by ilhasoft on 7/9/15.
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
}
