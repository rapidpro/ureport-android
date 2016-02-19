package in.ureport.fragments;

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
import br.com.ilhasoft.support.tool.bitmap.IOManager;
import in.ureport.R;
import in.ureport.helpers.MediaSelector;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.helpers.YoutubePicker;
import in.ureport.helpers.YoutubeThumbnailHandler;
import in.ureport.listener.OnPickMediaListener;
import in.ureport.managers.GcmTopicManager;
import in.ureport.managers.TransferManager;
import in.ureport.managers.UserManager;
import in.ureport.models.LocalMedia;
import in.ureport.models.Marker;
import in.ureport.models.Media;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.models.VideoMedia;
import in.ureport.network.StoryServices;
import in.ureport.helpers.SpaceItemDecoration;
import in.ureport.network.UserServices;
import in.ureport.tasks.CompressVideoTask;
import in.ureport.views.adapters.MediaAdapter;

/**
 * Created by johncordeiro on 7/14/15.
 */
public class CreateStoryFragment extends Fragment implements MediaAdapter.MediaListener
        , YoutubePicker.OnPickYoutubeVideoListener, OnPickMediaListener {

    private static final String TAG = "CreateStoryFragment";
    public static final int MEDIA_GAP = 5;

    private List<Marker> selectedMarkers;
    private List<Media> mediaList;

    private MediaAdapter mediaAdapter;

    private EditText markers;
    private EditText title;
    private EditText content;

    private StoryCreationListener storyCreationListener;

    private MediaSelector mediaSelector;
    private YoutubeThumbnailHandler youtubeThumbnailHandler;
    private IOManager ioManager;

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
        mediaSelector.onActivityResult(this, onLoadLocalMediaListener, requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mediaSelector.onRequestPermissionResult(this, requestCode, grantResults);
    }

    private void addMedia(Media media) {
        mediaList.add(media);
        mediaAdapter.updateMediaList(mediaList);
    }

    private void setupObjects() {
        mediaList = new ArrayList<>();
        mediaSelector = new MediaSelector(getContext());
        youtubeThumbnailHandler = new YoutubeThumbnailHandler();
        ioManager = new IOManager(getContext());
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
            List<Media> mediasToUpload = getMediasToUpload();

            if(mediasToUpload.size() > 0) {
                uploadMediasAndCreateStory();
            } else {
                createStoryWithMediasAndSave(mediaList);
            }
        }
    }

    @NonNull
    private List<Media> getMediasToUpload() {
        List<Media> mediasToUpload = new ArrayList<>();
        for (Media media : mediaList) {
            if(media instanceof LocalMedia) {
                mediasToUpload.add(media);
            }
        }
        return mediasToUpload;
    }

    private void uploadMediasAndCreateStory() {
        try {
            final ProgressDialog progressUpload = ProgressDialog.show(getActivity(), null
                    , getString(R.string.load_message_uploading_image), true, true);

            TransferManager transferManager = new TransferManager(getActivity());
            transferManager.transferMedias(mediaList, "story", medias -> {
                progressUpload.dismiss();
                createStoryWithMediasAndSave(medias);
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
        final ProgressDialog progressCreation = ProgressDialog.show(getActivity(), null
                , getString(R.string.load_message_wait), true, true);

        final Story story = new Story();
        story.setTitle(title.getText().toString());
        story.setContributions(0);
        story.setContent(content.getText().toString());
        story.setCreatedDate(new Date());
        story.setMedias(medias.size() > 0 ? medias : null);
        story.setCover(getCoverFromMediasUploaded(medias));

        String markersText = markers.getText().toString();
        story.setMarkers(markersText.length() == 0 ? "" : markersText);

        StoryServices storyServices = new StoryServices();
        storyServices.saveStory(story, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                progressCreation.dismiss();
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
        PickMediaFragment pickMediaFragment = new PickMediaFragment();
        pickMediaFragment.setOnPickMediaListener(this);
        getFragmentManager().beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_bottom
                        , R.anim.slide_in_top, R.anim.slide_out_bottom)
                .add(R.id.details, pickMediaFragment)
                .commit();
    }

    private View.OnClickListener onMarkerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (storyCreationListener != null)
                storyCreationListener.onAddMarkers(selectedMarkers);
        }
    };

    private MediaSelector.OnLoadLocalMediaListener onLoadLocalMediaListener = new MediaSelector.OnLoadLocalMediaListener() {
        @Override
        public void onLoadLocalImage(Uri uri) {
            addLocalMedia(uri, Media.Type.Picture, null);
        }

        @Override
        public void onLoadLocalVideo(Uri uri) {
            new CompressVideoTask(getContext()) {
                public ProgressDialog progressDialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog = ProgressDialog.show(getContext(), null
                            , getString(R.string.message_compressing_video), true, false);
                }

                @Override
                protected void onPostExecute(Uri uri) {
                    progressDialog.dismiss();
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
            addLocalMedia(uri, Media.Type.File, getFilenameForUri(uri));
        }
    };

    private void addLocalMedia(Uri pictureUri, Media.Type type, String name) {
        LocalMedia media = new LocalMedia();
        media.setType(type);
        media.setPath(pictureUri);
        media.setName(name);
        addMedia(media);
    }

    private String getFilenameForUri(Uri uri) {
        try {
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

        addMedia(videoMedia);
    }

    @Override
    public void onPickFromCamera() {
        mediaSelector.pickFromCamera(this);
    }

    @Override
    public void onPickFromGallery() {
        mediaSelector.pickFromGallery(this);
    }

    @Override
    public void onPickVideo() {
        mediaSelector.pickVideoFromCamera(this);
    }

    @Override
    public void onPickFile() {
        mediaSelector.pickFile(this);
    }

    @Override
    public void onPickAudioRecord() {

    }

    @Override
    public void onPickYoutubeLink() {
        mediaSelector.pickFromYoutube(this);
    }

    public interface StoryCreationListener {
        void onAddMarkers(List<Marker> markers);
        void onStoryCreated(Story story);
    }
}
