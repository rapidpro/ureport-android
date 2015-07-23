package in.ureport;

import android.app.Application;
import android.content.Context;

import com.activeandroid.ActiveAndroid;

import in.ureport.managers.UserManager;

/**
 * Created by johncordeiro on 7/9/15.
 */
public class UreportApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }

    public static boolean validateUserLogin(Context context) {
        return UserManager.validateKeyAction(context);
    }
}
