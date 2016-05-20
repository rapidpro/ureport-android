package in.ureport.helpers;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import java.io.File;
import java.io.IOException;

import br.com.ilhasoft.support.tool.bitmap.IOManager;
import in.ureport.R;

/**
 * Created by johncordeiro on 20/08/15.
 */
public class MediaPicker {

    private static final String TAG = "MediaPicker";

    public static final int REQUEST_PICK_FROM_GALLERY = 1024;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_VIDEO_FROM_CAMERA = 2048;
    public static final int REQUEST_FILE = 3072;

    public static final int VIDEO_QUALITY = 1;
    public static final int VIDEO_DURATION_LIMIT = 20;

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
        fragment.startActivityForResult(intent, REQUEST_PICK_FROM_GALLERY);
    }

    public void pickVideoFromCamera(Fragment fragment) {
        pickVideoFromCamera(fragment, VIDEO_QUALITY, VIDEO_DURATION_LIMIT);
    }

    public void pickFile(Fragment fragment) {
        Intent documentIntent = new Intent(Intent.ACTION_GET_CONTENT);
        if (Build.VERSION.SDK_INT >= 19) {
            documentIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            documentIntent.addCategory(Intent.CATEGORY_OPENABLE);
            documentIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        } else {
            documentIntent.setAction(Intent.ACTION_GET_CONTENT);
        }

        documentIntent.setType("*/*");
        fragment.startActivityForResult(Intent.createChooser(documentIntent
                , fragment.getString(R.string.prompt_file_upload)), REQUEST_FILE);
    }

    public void pickVideoFromCamera(Fragment fragment, int videoQuality, int durationLimit) {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, videoQuality);
        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, durationLimit);
        if (videoIntent.resolveActivity(fragment.getActivity().getPackageManager()) != null) {
            fragment.startActivityForResult(videoIntent, REQUEST_VIDEO_FROM_CAMERA);
        }
    }

    public File pickImageFromCamera(Fragment fragment) throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(fragment.getActivity().getPackageManager()) != null) {
            IOManager ioManager = new IOManager(fragment.getActivity());
            File pictureFile = ioManager.createImageFilePath();

            if(pictureFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(pictureFile));
                fragment.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                return pictureFile;
            }
        }
        return null;
    }

}
