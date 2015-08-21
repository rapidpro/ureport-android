package in.ureport.managers;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;

/**
 * Created by johncordeiro on 20/08/15.
 */
public class ImagePicker {

    public static final int REQUEST_CODE = 1024;

    public void pickImageFromGallery(Fragment fragment) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        } else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }

        intent.setType("image/*");
        fragment.startActivityForResult(intent, REQUEST_CODE);
    }

}
