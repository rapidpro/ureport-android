package in.ureport.managers;

import android.support.annotation.DrawableRes;

import in.ureport.R;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/22/15.
 */
public class UserDataManager {

    @DrawableRes
    public static int getUserImage(User user) {
        if(user != null && user.getPicture() != null)
            return user.getPicture();

        return R.drawable.face;
    }

}
