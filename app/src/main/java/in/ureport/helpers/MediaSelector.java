package in.ureport.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
    public static final int REQUEST_CODE_WRITE_EXTERNAL_PERMISSION = 201;

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
                case ImagePicker.REQUEST_VIDEO_FROM_GALLERY:
                    saveChoosenVideo(data, onLoadLocalMediaListener);
                    break;
                case ImagePicker.REQUEST_PICK_FROM_GALLERY:
                    saveChoosenPicture(data, onLoadLocalMediaListener);
                    break;
                case ImagePicker.REQUEST_IMAGE_CAPTURE:
                    saveTakenPicture(fragment, onLoadLocalMediaListener);
            }
        }
    }

    public void onRequestPermissionResult(Fragment fragment, int requestCode, int [] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_WRITE_EXTERNAL_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromCamera(fragment);
                } else {
                    Toast.makeText(fragment.getContext(), R.string.error_message_permission_external
                            , Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void pickVideoFromCamera(Fragment fragment) {
        ImagePicker imagePicker = new ImagePicker();
        imagePicker.pickVideoFromCamera(fragment);
    }

    public void pickFromCamera(Fragment fragment) {
        try {
            if (ContextCompat.checkSelfPermission(fragment.getActivity()
            , Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                fragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        , REQUEST_CODE_WRITE_EXTERNAL_PERMISSION);
            } else {
                ImagePicker imagePicker = new ImagePicker();
                imageFromCamera = imagePicker.pickImageFromCamera(fragment);
            }
        } catch(Exception exception) {
            showTakenPictureError(fragment);
            Log.e(TAG, "onClick ", exception);
        }
    }

    public void pickFromGallery(Fragment fragment) {
        ImagePicker imagePicker = new ImagePicker();
        imagePicker.pickImageFromGallery(fragment);
    }

    public void pickFromYoutube(Fragment fragment) {
        if(fragment instanceof YoutubePicker.OnPickVideoListener) {
            YoutubePicker youtubePicker = new YoutubePicker(context);
            youtubePicker.pickVideoFromInput((YoutubePicker.OnPickVideoListener)fragment);
        }
    }

    private void saveTakenPicture(Fragment fragment, OnLoadLocalMediaListener onLoadLocalMediaListener) {
        if(imageFromCamera != null) {
            onLoadLocalMediaListener.onLoadLocalImage(Uri.fromFile(imageFromCamera));
        } else {
            showTakenPictureError(fragment);
        }
    }

    private void saveChoosenPicture(Intent data, OnLoadLocalMediaListener onLoadLocalMediaListener) {
        Uri pictureUri = data.getData();
        if(pictureUri != null)
            onLoadLocalMediaListener.onLoadLocalImage(pictureUri);
    }

    private void saveChoosenVideo(Intent data, OnLoadLocalMediaListener onLoadLocalMediaListener) {
        Uri videoUri = data.getData();
        if(videoUri != null)
            onLoadLocalMediaListener.onLoadLocalVideo(videoUri);
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
        void onLoadLocalImage(Uri uri);
        void onLoadLocalVideo(Uri uri);
    }
}
