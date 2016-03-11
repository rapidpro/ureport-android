package in.ureport.fragments;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.ilhasoft.support.tool.ButtonTinter;
import in.ureport.R;
import in.ureport.listener.OnPickMediaListener;

/**
 * Created by john-mac on 2/5/16.
 */
public class PickMediaFragment extends Fragment {

    private OnPickMediaListener onPickMediaListener;

    private ImageView background;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pick_media, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
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
        animateBackground(background, 0, 1);
    }

    private void hideBackground(ImageView background) {
        animateBackground(background, 1, 0);
    }

    private void animateBackground(ImageView background, int alphaPre, int alphaPos) {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(background, "alpha", alphaPre, alphaPos);
            objectAnimator.setStartDelay(400);
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
            onPickMediaListener.onPickFromCamera();
            dismiss();
        }
    };

    private View.OnClickListener onGalleryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onPickMediaListener.onPickFromGallery();
            dismiss();
        }
    };

    private View.OnClickListener onVideoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onPickMediaListener.onPickVideo();
            dismiss();
        }
    };

    private View.OnClickListener onFileClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onPickMediaListener.onPickFile();
            dismiss();
        }
    };

    private View.OnClickListener onAudioClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onPickMediaListener.onPickAudioRecord();
            dismiss();
        }
    };

    private View.OnClickListener onYoutubeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onPickMediaListener.onPickYoutubeLink();
            dismiss();
        }
    };

    public void setOnPickMediaListener(OnPickMediaListener onPickMediaListener) {
        this.onPickMediaListener = onPickMediaListener;
    }

}
