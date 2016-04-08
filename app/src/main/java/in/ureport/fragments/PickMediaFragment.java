package in.ureport.fragments;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;

import br.com.ilhasoft.support.tool.ButtonTinter;
import br.com.ilhasoft.support.tool.IOManager;
import in.ureport.R;
import in.ureport.helpers.MediaSelector;
import in.ureport.helpers.YoutubePicker;
import in.ureport.helpers.YoutubeThumbnailHandler;
import in.ureport.models.LocalMedia;
import in.ureport.models.Media;
import in.ureport.models.VideoMedia;
import in.ureport.tasks.CompressVideoTask;

/**
 * Created by john-mac on 2/5/16.
 */
public class PickMediaFragment extends Fragment
        implements MediaSelector.OnLoadLocalMediaListener, YoutubePicker.OnPickYoutubeVideoListener {

    private static final String TAG = "PickMediaFragment";

    private ImageView background;

    private MediaSelector mediaSelector;
    private YoutubeThumbnailHandler youtubeThumbnailHandler;

    private ProgressDialog progressDialog;

    private OnPickMediaListener onPickMediaListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pick_media, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObjects();
        setupView(view);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mediaSelector.onActivityResult(this, this, requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mediaSelector.onRequestPermissionResult(this, requestCode, grantResults);
    }

    private void setupObjects() {
        mediaSelector = new MediaSelector(getContext());
        youtubeThumbnailHandler = new YoutubeThumbnailHandler();
    }

    private void setupView(View view) {
        background = (ImageView) view.findViewById(R.id.background);
        background.setOnClickListener(onBackgroundClickListener);
        showBackground(background);

        ViewGroup cameraGroup = (ViewGroup) view.findViewById(R.id.camera);
        setupValuesForGroup(cameraGroup, onCameraClickListener, R.color.primary_color
                , R.drawable.ic_camera_white_24dp, R.string.title_camera);

        ViewGroup galleryGroup = (ViewGroup) view.findViewById(R.id.gallery);
        setupValuesForGroup(galleryGroup, onGalleryClickListener, R.color.yellow
                , R.drawable.ic_photo_white_24dp, R.string.title_gallery);

        ViewGroup videoGroup = (ViewGroup) view.findViewById(R.id.video);
        setupValuesForGroup(videoGroup, onVideoClickListener, R.color.purple
                , R.drawable.ic_videocam_white_24dp, R.string.title_video);

        ViewGroup fileGroup = (ViewGroup) view.findViewById(R.id.file);
        setupValuesForGroup(fileGroup, onFileClickListener, R.color.orange
                , R.drawable.ic_folder_white_24dp, R.string.title_file);

        ViewGroup audioGroup = (ViewGroup) view.findViewById(R.id.audio);
        setupValuesForGroup(audioGroup, onAudioClickListener, R.color.light_green_highlight
                , R.drawable.ic_music_note_white_24dp, R.string.title_record);

        ViewGroup youtubeGroup = (ViewGroup) view.findViewById(R.id.youtube);
        setupValuesForGroup(youtubeGroup, onYoutubeClickListener, R.color.red
                , R.drawable.ic_play_arrow_white_24dp, R.string.title_youtube);
    }

    private void showBackground(ImageView background) {
        animateBackground(background, 0, 1, 300, 400);
    }

    private void hideBackground(ImageView background) {
        animateBackground(background, 1, 0, 100, 0);
    }

    private void animateBackground(ImageView background, int alphaPre, int alphaPos, int duration, int startDelay) {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(background, "alpha", alphaPre, alphaPos);
            objectAnimator.setDuration(duration);
            objectAnimator.setStartDelay(startDelay);
            objectAnimator.start();
        }
    }

    private void setupValuesForGroup(ViewGroup cameraGroup, View.OnClickListener clickListener, int colorRes, int mediaRes, int titleRes) {
        ImageButton button = (ImageButton) cameraGroup.findViewById(R.id.mediaButton);
        button.setImageResource(mediaRes);
        button.setOnClickListener(clickListener);
        ButtonTinter.setImageButtonTint(button, getResources().getColorStateList(colorRes));

        TextView title = (TextView) cameraGroup.findViewById(R.id.mediaTitle);
        title.setText(titleRes);
    }

    private View.OnClickListener onBackgroundClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
        }
    };

    public void dismiss() {
        hideBackground(background);
        getFragmentManager().popBackStack();
    }

    private View.OnClickListener onCameraClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mediaSelector.pickFromCamera(PickMediaFragment.this);
        }
    };

    private View.OnClickListener onGalleryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mediaSelector.pickFromGallery(PickMediaFragment.this);
        }
    };

    private View.OnClickListener onVideoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mediaSelector.pickVideoFromCamera(PickMediaFragment.this);
        }
    };

    private View.OnClickListener onFileClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mediaSelector.pickFile(PickMediaFragment.this);
        }
    };

    private View.OnClickListener onAudioClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mediaSelector.pickAudio(PickMediaFragment.this);
        }
    };

    private View.OnClickListener onYoutubeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mediaSelector.pickFromYoutube(PickMediaFragment.this);
        }
    };

    @Override
    public void onLoadLocalImage(Uri uri) {
        addLocalMedia(uri, Media.Type.Picture, null);
    }

    @Override
    public void onLoadLocalVideo(Uri uri) {
        new CompressVideoTask(getContext()) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(getActivity(), null
                        , getString(R.string.message_compressing_video), true, false);
            }

            @Override
            protected void onPostExecute(Uri uri) {
                progressDialog.cancel();
                if(uri != null) {
                    addLocalMedia(uri, Media.Type.VideoPhone, null);
                } else {
                    Toast.makeText(getContext(), R.string.error_compressing_video, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(uri);
    }

    @Override
    public void onLoadFile(Uri uri) {
        HashMap<String, Object> metadata = new HashMap<>();
        metadata.put(Media.KEY_FILENAME, getFilenameForUri(uri));

        addLocalMedia(uri, Media.Type.File, metadata);
    }

    @Override
    public void onLoadAudio(Uri uri, int duration) {
        HashMap<String, Object> metadata = new HashMap<>();
        metadata.put(Media.KEY_DURATION, duration);

        addLocalMedia(uri, Media.Type.Audio, metadata);
    }

    private void addLocalMedia(Uri pictureUri, Media.Type type, HashMap<String, Object> metadata) {
        LocalMedia media = new LocalMedia();
        media.setType(type);
        media.setPath(pictureUri);
        media.setMetadata(metadata);
        onPickMediaListener.onPickMedia(media);
    }

    private String getFilenameForUri(Uri uri) {
        try {
            IOManager ioManager = new IOManager(getContext());
            File file = new File(ioManager.getFilePathForUri(uri));
            return file.getName();
        } catch(Exception exception) {
            Log.e(TAG, "bindImage: ", exception);
        }
        return null;
    }

    @Override
    public void onPickYoutubeVideo(String videoId, String videoUrl) {
        addYoutubeVideoMedia(videoId, videoUrl);
    }

    private void addYoutubeVideoMedia(String videoId, String videoUrl) {
        VideoMedia videoMedia = new VideoMedia();
        videoMedia.setId(videoId);
        videoMedia.setPath(videoUrl);
        videoMedia.setUrl(youtubeThumbnailHandler.getThumbnailUrlFromVideo(videoId
                , YoutubeThumbnailHandler.ThumbnailSizeClass.HighQuality));
        onPickMediaListener.onPickMedia(videoMedia);
    }

    public void setOnPickMediaListener(OnPickMediaListener onPickMediaListener) {
        this.onPickMediaListener = onPickMediaListener;
    }

    public interface OnPickMediaListener {
        void onPickMedia(Media media);
    }
}
