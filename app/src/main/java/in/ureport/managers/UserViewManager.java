package in.ureport.managers;

import android.content.Context;
import android.support.annotation.DrawableRes;

import in.ureport.R;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/22/15.
 */
public class UserViewManager {

    @DrawableRes
    public static int getUserImage(Context context, User user) {
        if(user != null && user.getPicture() != null) {
            return context.getResources().getIdentifier(user.getPicture(), "drawable", context.getPackageName());
        }
        return R.drawable.face;
    }

}
