package in.ureport.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.ilhasoft.support.tool.EditTextValidator;
import br.com.ilhasoft.support.tool.UnitConverter;
import in.ureport.R;
import in.ureport.helpers.ImagePicker;
import in.ureport.helpers.MediaSelector;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.listener.OnMediaSelectedListener;
import in.ureport.managers.GcmTopicManager;
import in.ureport.managers.TransferManager;
import in.ureport.managers.UserManager;
import in.ureport.models.LocalMedia;
import in.ureport.models.Marker;
import in.ureport.models.Media;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.network.StoryServices;
import in.ureport.helpers.SpaceItemDecoration;
import in.ureport.network.UserServices;
import in.ureport.views.adapters.MediaAdapter;

/**
 * Created by johncordeiro on 7/14/15.
 */
public class CreateStoryFragment extends Fragment implements MediaAdapter.MediaListener {

    private static final String TAG = "CreateStoryFragment";
    public static final int MEDIA_GAP = 5;

    private List<Marker> selectedMarkers;
    private List<Media> mediaList;

    private MediaAdapter mediaAdapter;

    private EditText markers;
    private EditText title;
    private EditText content;

    private StoryCreationListener storyCreationListener;

    private ImagePicker imagePicker;
    private File imageFromCamera;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_story, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObjects();
        setupView(view);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case ImagePicker.REQUEST_PICK_FROM_GALLERY:
                    saveChoosenPicture(data);
                    break;
                case ImagePicker.REQUEST_IMAGE_CAPTURE:
                    saveTakenPicture();
            }
        }
    }

    private void saveTakenPicture() {
        if(imageFromCamera != null) {
            addLocalMedia(Uri.fromFile(imageFromCamera));
        } else {
            showTakenPictureError();
        }
    }

    private void saveChoosenPicture(Intent data) {
        Uri pictureUri = data.getData();
        if(pictureUri != null)
            addLocalMedia(pictureUri);
    }

    private void addLocalMedia(Uri pictureUri) {
        LocalMedia media = new LocalMedia();
        media.setPath(pictureUri);
        mediaList.add(media);
        mediaAdapter.updateMediaList(mediaList);
    }

    private void setupObjects() {
        mediaList = new ArrayList<>();
        imagePicker = new ImagePicker();
    }

    private void setupView(View view) {
        setHasOptionsMenu(true);

        title = (EditText) view.findViewById(R.id.title);
        content = (EditText) view.findViewById(R.id.content);

        markers = (EditText) view.findViewById(R.id.markers);
        markers.setOnClickListener(onMarkerClickListener);

        mediaAdapter = new MediaAdapter(mediaList, true);
        mediaAdapter.setHasStableIds(true);
        mediaAdapter.setMediaListener(this);

        RecyclerView mediaAddList = (RecyclerView) view.findViewById(R.id.mediaAddList);
        mediaAddList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        UnitConverter converter = new UnitConverter(getActivity());
        SpaceItemDecoration mediaItemDecoration = new SpaceItemDecoration();
        mediaItemDecoration.setHorizontalSpaceWidth((int) converter.convertDpToPx(MEDIA_GAP));
        mediaAddList.addItemDecoration(mediaItemDecoration);
        mediaAddList.setAdapter(mediaAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof StoryCreationListener) {
            this.storyCreationListener = (StoryCreationListener) context;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_create_story, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.publish:
                publishStory();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        setCloseIconToNavigation();
    }

    private void setCloseIconToNavigation() {
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
    }

    private void publishStory() {
        if(isFieldsValid()) {
            if(mediaList.size() > 0) {
                uploadMediasAndCreateStory();
            } else {
                createStoryWithMediasAndSave(null);
            }
        }
    }

    private void uploadMediasAndCreateStory() {
        try {
            final ProgressDialog progressUpload = ProgressDialog.show(getActivity(), null
                    , getString(R.string.load_message_uploading_image), true, true);

            TransferManager transferManager = new TransferManager(getActivity());
            transferManager.transferMedias(mediaList, "story", new TransferManager.OnTransferMediasListener() {
                @Override
                public void onTransferMedias(List<Media> medias) {
                    progressUpload.dismiss();
                    createStoryWithMediasAndSave(medias);
                }
            });
        } catch(Exception exception) {
            showErrorImageUpload();
            Log.e(TAG, "uploadMediasAndCreateStory ", exception);
        }
    }

    private void showErrorImageUpload() {
        Toast.makeText(getActivity(), R.string.error_image_upload, Toast.LENGTH_SHORT).show();
    }

    private void createStoryWithMediasAndSave(List<Media> medias) {
        final Story story = new Story();
        story.setTitle(title.getText().toString());
        story.setContributions(0);
        story.setContent(content.getText().toString());
        story.setCreatedDate(new Date());
        story.setMedias(medias);
        story.setCover(getCoverFromMediasUploaded(medias));

        String markersText = markers.getText().toString();
        story.setMarkers(markersText.length() == 0 ? "" : markersText);

        StoryServices storyServices = new StoryServices();
        storyServices.saveStory(story, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null && storyCreationListener != null) {
                    story.setKey(firebase.getKey());

                    incrementStoryCount();
                    storyCreationListener.onStoryCreated(story);
                    registerAuthorToGcm(story);
                }
            }
        });
    }

    private void registerAuthorToGcm(final Story story) {
        UserServices userServices = new UserServices();
        userServices.getUser(UserManager.getUserId(), new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                User user = dataSnapshot.getValue(User.class);

                GcmTopicManager gcmTopicManager = new GcmTopicManager(getActivity());
                gcmTopicManager.registerToStoryTopic(user, story);
            }
        });
    }

    private void incrementStoryCount() {
        UserServices userServices = new UserServices();
        userServices.incrementStoryCount();
    }

    @Nullable
    private Media getCoverFromMediasUploaded(List<Media> medias) {
        Media cover = null;
        int indexOfCover = mediaList.indexOf(mediaAdapter.getSelectedMedia());
        if(indexOfCover >= 0) {
            cover = medias.get(indexOfCover);
        }
        return cover;
    }

    private boolean isFieldsValid() {
        EditTextValidator validator = new EditTextValidator();
        String errorMessage = getString(R.string.error_required_field);

        return validator.validateEmpty(title, errorMessage) && validator.validateEmpty(content, errorMessage);
    }

    public void setSelectedMarkers(List<Marker> selectedMarkers) {
        setCloseIconToNavigation();
        this.selectedMarkers = selectedMarkers;
        markers.setText(getMarkerTexts(selectedMarkers));
    }

    @NonNull
    private String getMarkerTexts(List<Marker> selectedMarkers) {
        StringBuilder markersText = new StringBuilder();
        for (int i = 0; i < selectedMarkers.size(); i++) {
            Marker selectedMarker = selectedMarkers.get(i);
            markersText.append(selectedMarker.getName());

            if(i < selectedMarkers.size()-1)
                markersText.append(", ");
        }
        return markersText.toString();
    }

    @Override
    public void onMediaRemoveListener(int position) {
        mediaList.remove(position);
        mediaAdapter.updateMediaList(mediaList);
    }

    @Override
    public void onMediaAddListener() {
        MediaSelector mediaSelector = new MediaSelector(getActivity());
        mediaSelector.selectMedia(onMediaSelectedListener);
    }

    private View.OnClickListener onMarkerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (storyCreationListener != null)
                storyCreationListener.onAddMarkers(selectedMarkers);
        }
    };

    private OnMediaSelectedListener onMediaSelectedListener = new OnMediaSelectedListener() {
        @Override
        public void onMediaSelected(int position) {
            switch (position) {
                case MediaSelector.POSITION_GALLERY:
                    imagePicker.pickImageFromGallery(CreateStoryFragment.this);
                    break;
                case MediaSelector.POSITION_CAMERA:
                    pickFromCamera();
                    break;
            }
        }
    };

    private void pickFromCamera() {
        try {
            imageFromCamera = imagePicker.pickImageFromCamera(CreateStoryFragment.this);
        } catch(Exception exception) {
            showTakenPictureError();
            Log.e(TAG, "onClick ", exception);
        }
    }

    private void showTakenPictureError() {
        Toast.makeText(getActivity(), R.string.error_take_picture, Toast.LENGTH_SHORT).show();
    }

    public interface StoryCreationListener {
        void onAddMarkers(List<Marker> markers);
        void onStoryCreated(Story story);
    }
}
