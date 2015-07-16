package in.ureport.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.ilhasoft.support.tool.EditTextValidator;
import br.com.ilhasoft.support.tool.UnitConverter;
import in.ureport.R;
import in.ureport.db.business.UserBusiness;
import in.ureport.db.repository.UserRepository;
import in.ureport.models.Marker;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.pref.SystemPreferences;
import in.ureport.tasks.PublishStoryTask;
import in.ureport.util.SpaceItemDecoration;
import in.ureport.views.adapters.MediaAdapter;

/**
 * Created by johncordeiro on 7/14/15.
 */
public class CreateStoryFragment extends Fragment implements MediaAdapter.MediaListener {

    public static final String MEDIA_PICTURE = "picture";
    public static final String MEDIA_VIDEO = "video";

    private List<Marker> selectedMarkers;
    private List<String> mediaList;

    private MediaAdapter mediaAdapter;

    private EditText markers;
    private EditText title;
    private EditText content;

    private StoryCreationListener storyCreationListener;

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

    private void setupObjects() {
        mediaList = new ArrayList<>();
    }

    private void setupView(View view) {
        setHasOptionsMenu(true);

        title = (EditText) view.findViewById(R.id.title);
        content = (EditText) view.findViewById(R.id.content);

        markers = (EditText) view.findViewById(R.id.markers);
        markers.setOnClickListener(onMarkerClickListener);

        mediaAdapter = new MediaAdapter(mediaList);
        mediaAdapter.setHasStableIds(true);
        mediaAdapter.setMediaListener(this);

        RecyclerView mediaAddList = (RecyclerView) view.findViewById(R.id.mediaAddList);
        mediaAddList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        UnitConverter converter = new UnitConverter(getActivity());
        mediaAddList.addItemDecoration(new SpaceItemDecoration((int) converter.convertDpToPx(10)));
        mediaAddList.setAdapter(mediaAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_create_story, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_publish:
                publishStory();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void publishStory() {
        if(isFieldsValid()) {
            final Story story = new Story();
            story.setTitle(title.getText().toString());
            story.setContributions(0);
            story.setContent(content.getText().toString());
            story.setCreatedDate(new Date());

            String markersText = markers.getText().toString();
            story.setMarkers(markersText.length() == 0 ? "" : markersText);

            PublishStoryTask publishStoryTask = new PublishStoryTask(getActivity()) {
                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if(storyCreationListener != null)
                        storyCreationListener.storyCreated(story);
                }
            };
            publishStoryTask.execute(story);
        }
    }

    private boolean isFieldsValid() {
        EditTextValidator validator = new EditTextValidator();
        String errorMessage = getString(R.string.error_required_field);

        return validator.validateEmpty(title, errorMessage) && validator.validateEmpty(content, errorMessage);
    }

    public void setSelectedMarkers(List<Marker> selectedMarkers) {
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

    public void setStoryCreationListener(StoryCreationListener storyCreationListener) {
        this.storyCreationListener = storyCreationListener;
    }

    @Override
    public void onMediaRemoveListener(int position) {
        mediaList.remove(position);
        mediaAdapter.updateMediaList(mediaList);
    }

    @Override
    public void onMediaAddListener() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.create_story_title_media_source)
                .setItems(R.array.create_story_media_sources, onMediaSelectedListener)
                .create();
        alertDialog.show();
    }

    private View.OnClickListener onMarkerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (storyCreationListener != null)
                storyCreationListener.addMarkers(selectedMarkers);
        }
    };

    private DialogInterface.OnClickListener onMediaSelectedListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int position) {
            switch (position) {
                case 2:
                    mediaList.add(MEDIA_VIDEO);
                    break;
                default:
                    mediaList.add(MEDIA_PICTURE);
            }
            mediaAdapter.updateMediaList(mediaList);
        }
    };

    public interface StoryCreationListener {
        void addMarkers(List<Marker> markers);
        void storyCreated(Story story);
    }
}
