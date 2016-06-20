package in.ureport.helpers;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by john-mac on 6/4/16.
 */
public class PermissionHelper {

    public static boolean allPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if(grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean isPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

}
