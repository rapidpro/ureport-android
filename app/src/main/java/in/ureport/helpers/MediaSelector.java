package in.ureport.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import in.ureport.R;
import in.ureport.listener.OnMediaSelectedListener;

/**
 * Created by johncordeiro on 25/09/15.
 */
public class MediaSelector {

    private static final String TAG = "MediaSelector";

    public static final int POSITION_GALLERY = 0;
    public static final int POSITION_CAMERA = 1;
    public static final int POSITION_YOUTUBE = 2;

    private Context context;

    private File imageFromCamera;

    public MediaSelector(Context context) {
        this.context = context;
    }

    public void selectMedia(final OnMediaSelectedListener listener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.title_media_source)
                .setItems(R.array.media_sources, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onMediaSelected(which);
                    }
                })
                .create();
        alertDialog.show();
    }

    public void selectMedia(Fragment fragment) {
        OnMediaSelectedFragmentListener onMediaSelectedFragmentListener = new OnMediaSelectedFragmentListener(fragment);
        selectMedia(onMediaSelectedFragmentListener);
    }

    public void selectImage(final OnMediaSelectedListener listener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.title_media_source)
                .setItems(R.array.image_sources, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onMediaSelected(which);
                    }
                })
                .create();
        alertDialog.show();
    }

    public void selectImage(Fragment fragment) {
        OnMediaSelectedFragmentListener onMediaSelectedFragmentListener = new OnMediaSelectedFragmentListener(fragment);
        selectImage(onMediaSelectedFragmentListener);
    }

    public void onActivityResult(Fragment fragment, OnLoadLocalMediaListener onLoadLocalMediaListener
            , int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case ImagePicker.REQUEST_PICK_FROM_GALLERY:
                    saveChoosenPicture(data, onLoadLocalMediaListener);
                    break;
                case ImagePicker.REQUEST_IMAGE_CAPTURE:
                    saveTakenPicture(fragment, onLoadLocalMediaListener);
            }
        }
    }

    private void saveTakenPicture(Fragment fragment, OnLoadLocalMediaListener onLoadLocalMediaListener) {
        if(imageFromCamera != null) {
            onLoadLocalMediaListener.onLoadLocalMedia(Uri.fromFile(imageFromCamera));
        } else {
            showTakenPictureError(fragment);
        }
    }

    private void saveChoosenPicture(Intent data, OnLoadLocalMediaListener onLoadLocalMediaListener) {
        Uri pictureUri = data.getData();
        if(pictureUri != null)
            onLoadLocalMediaListener.onLoadLocalMedia(pictureUri);
    }

    private void pickFromCamera(Fragment fragment) {
        try {
            ImagePicker imagePicker = new ImagePicker();
            imageFromCamera = imagePicker.pickImageFromCamera(fragment);
        } catch(Exception exception) {
            showTakenPictureError(fragment);
            Log.e(TAG, "onClick ", exception);
        }
    }

    private void pickFromGallery(Fragment fragment) {
        ImagePicker imagePicker = new ImagePicker();
        imagePicker.pickImageFromGallery(fragment);
    }

    private void pickFromYoutube(Fragment fragment) {
        if(fragment instanceof YoutubePicker.OnPickVideoListener) {
            YoutubePicker youtubePicker = new YoutubePicker(context);
            youtubePicker.pickVideoFromInput((YoutubePicker.OnPickVideoListener)fragment);
        }
    }

    private void showTakenPictureError(Fragment fragment) {
        Toast.makeText(fragment.getContext(), R.string.error_take_picture, Toast.LENGTH_SHORT).show();
    }

    private class OnMediaSelectedFragmentListener implements OnMediaSelectedListener {
        private Fragment fragment;

        public OnMediaSelectedFragmentListener(Fragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void onMediaSelected(int position) {
            switch (position) {
                case MediaSelector.POSITION_GALLERY:
                    pickFromGallery(fragment);
                    break;
                case MediaSelector.POSITION_CAMERA:
                    pickFromCamera(fragment);
                    break;
                case MediaSelector.POSITION_YOUTUBE:
                    pickFromYoutube(fragment);
            }
        }
    }

    public interface OnLoadLocalMediaListener {
        void onLoadLocalMedia(Uri uri);
    }
}
