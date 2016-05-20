package in.ureport.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import in.ureport.R;
import in.ureport.fragments.RecordAudioFragment;
import in.ureport.listener.OnMediaSelectedListener;

/**
 * Created by johncordeiro on 25/09/15.
 */
public class MediaSelector {

    private static final String TAG = "MediaSelector";

    public static final int POSITION_GALLERY = 0;
    public static final int POSITION_CAMERA = 1;
    public static final int POSITION_YOUTUBE = 2;
    public static final int REQUEST_CODE_WRITE_EXTERNAL_IMAGE_PERMISSION = 201;
    public static final int REQUEST_CODE_WRITE_EXTERNAL_VIDEO_PERMISSION = 202;
    public static final int REQUEST_CODE_AUDIO_PERMISSION = 203;
    public static final int REQUEST_CODE_READ_EXTERNAL_VIDEO_PERMISSION = 204;

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

    public void selectMedia(Fragment fragment, YoutubePicker.OnPickYoutubeVideoListener listener) {
        OnMediaSelectedFragmentListener onMediaSelectedFragmentListener = new OnMediaSelectedFragmentListener(fragment, listener);
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
        OnMediaSelectedFragmentListener onMediaSelectedFragmentListener = new OnMediaSelectedFragmentListener(fragment, null);
        selectImage(onMediaSelectedFragmentListener);
    }

    public void onActivityResult(Fragment fragment, OnLoadLocalMediaListener onLoadLocalMediaListener
            , int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case MediaPicker.REQUEST_VIDEO_FROM_CAMERA:
                    saveChoosenVideo(data, onLoadLocalMediaListener);
                    break;
                case MediaPicker.REQUEST_PICK_FROM_GALLERY:
                    saveChoosenPicture(data, onLoadLocalMediaListener);
                    break;
                case MediaPicker.REQUEST_IMAGE_CAPTURE:
                    saveTakenPicture(fragment, onLoadLocalMediaListener);
                    break;
                case MediaPicker.REQUEST_FILE:
                    saveFile(fragment, data, onLoadLocalMediaListener);
            }
        }
    }

    private void saveFile(Fragment fragment, Intent data, OnLoadLocalMediaListener onLoadLocalMediaListener) {
        Uri dataUri = data.getData();
        if(dataUri != null)
            onLoadLocalMediaListener.onLoadFile(dataUri);
        else
            Toast.makeText(fragment.getContext(), R.string.error_get_file, Toast.LENGTH_SHORT).show();
    }

    public void onRequestPermissionResult(Fragment fragment, int requestCode, int [] grantResults) {
        if (grantResults.length > 0 && allPermissionsGranted(grantResults)) {
            switch (requestCode) {
                case REQUEST_CODE_WRITE_EXTERNAL_VIDEO_PERMISSION:
                    pickVideoFromCamera(fragment); break;
                case REQUEST_CODE_WRITE_EXTERNAL_IMAGE_PERMISSION:
                    pickFromCamera(fragment); break;
                case REQUEST_CODE_AUDIO_PERMISSION:
                    pickAudio(fragment); break;
                case REQUEST_CODE_READ_EXTERNAL_VIDEO_PERMISSION:
                    pickFile(fragment);
            }
        } else {
            Toast.makeText(fragment.getContext(), R.string.error_message_permission_external
                    , Toast.LENGTH_SHORT).show();
        }
    }

    private boolean allPermissionsGranted(int[] grantResults) {
        boolean granted = true;
        for (int grantResult : grantResults) {
            if(grantResult != PackageManager.PERMISSION_GRANTED) {
                granted = false;
                break;
            }
        }
        return granted;
    }

    public void pickFile(Fragment fragment) {
        if (ContextCompat.checkSelfPermission(fragment.getActivity()
                , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            fragment.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                    , REQUEST_CODE_READ_EXTERNAL_VIDEO_PERMISSION);
        } else {
            MediaPicker mediaPicker = new MediaPicker();
            mediaPicker.pickFile(fragment);
        }
    }

    public void pickVideoFromCamera(Fragment fragment) {
        if (ContextCompat.checkSelfPermission(fragment.getActivity()
                , Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            fragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                    , REQUEST_CODE_WRITE_EXTERNAL_VIDEO_PERMISSION);
        } else {
            MediaPicker mediaPicker = new MediaPicker();
            mediaPicker.pickVideoFromCamera(fragment);
        }
    }

    public void pickFromCamera(Fragment fragment) {
        try {
            if (ContextCompat.checkSelfPermission(fragment.getActivity()
            , Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                fragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        , REQUEST_CODE_WRITE_EXTERNAL_IMAGE_PERMISSION);
            } else {
                MediaPicker mediaPicker = new MediaPicker();
                imageFromCamera = mediaPicker.pickImageFromCamera(fragment);
            }
        } catch(Exception exception) {
            showTakenPictureError(fragment);
            Log.e(TAG, "onClick ", exception);
        }
    }

    public void pickFromGallery(Fragment fragment) {
        MediaPicker mediaPicker = new MediaPicker();
        mediaPicker.pickImageFromGallery(fragment);
    }

    public void pickFromYoutube(YoutubePicker.OnPickYoutubeVideoListener listener) {
        YoutubePicker youtubePicker = new YoutubePicker(context);
        youtubePicker.pickVideoFromInput(listener);
    }

    public void pickAudio(Fragment fragment) {
        pickAudio(fragment, (OnLoadLocalMediaListener) fragment);
    }
    public void pickAudio(Fragment fragment, OnLoadLocalMediaListener onLoadLocalMediaListener) {
        if (ContextCompat.checkSelfPermission(fragment.getActivity()
                , Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            fragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO}
                    , REQUEST_CODE_AUDIO_PERMISSION);
        } else {
            FragmentTransaction transaction = fragment.getActivity().getSupportFragmentManager().beginTransaction();

            RecordAudioFragment recordAudioFragment = new RecordAudioFragment();
            recordAudioFragment.setOnLoadLocalMediaListener(onLoadLocalMediaListener);
            recordAudioFragment.show(transaction, "recordAudioFragment");
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
        private YoutubePicker.OnPickYoutubeVideoListener onPickYoutubeVideoListener;

        public OnMediaSelectedFragmentListener(Fragment fragment, YoutubePicker.OnPickYoutubeVideoListener onPickYoutubeVideoListener) {
            this.fragment = fragment;
            this.onPickYoutubeVideoListener = onPickYoutubeVideoListener;
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
                    pickFromYoutube(onPickYoutubeVideoListener);
            }
        }
    }

    public interface OnLoadLocalMediaListener {
        void onLoadLocalImage(Uri uri);
        void onLoadLocalVideo(Uri uri);
        void onLoadFile(Uri uri);
        void onLoadAudio(Uri uri, int duration);
    }
}
