package in.ureport;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

/**
 * Created by ilhasoft on 7/9/15.
 */
public class UreportApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}
