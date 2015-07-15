package in.ureport.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import br.com.ilhasoft.support.tool.UnitConverter;
import in.ureport.R;
import in.ureport.models.User;
import in.ureport.util.SpaceItemDecoration;
import in.ureport.views.adapters.MediaAdapter;

/**
 * Created by johncordeiro on 7/14/15.
 */
public class CreateStoryFragment extends Fragment implements MediaAdapter.MediaListener {

    public static final String MEDIA_PICTURE = "picture";
    public static final String MEDIA_VIDEO = "video";

    private static final String TAG = "CreateStoryFragment";

    private List<User> selectedCoauthors;
    private List<String> mediaList;

    private MediaAdapter mediaAdapter;

    private ImageView cover;
    private View insertCoverInfo;
    private EditText coauthors;

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

        cover = (ImageView) view.findViewById(R.id.cover);
        insertCoverInfo = view.findViewById(R.id.insertCoverInfo);
        coauthors = (EditText) view.findViewById(R.id.coauthors);
        coauthors.setOnClickListener(onCoauthorsClickListener);

        FloatingActionButton addCover = (FloatingActionButton) view.findViewById(R.id.addCover);
        addCover.setOnClickListener(onAddCoverClickListener);

        mediaAdapter = new MediaAdapter(mediaList);
        mediaAdapter.setHasStableIds(true);
        mediaAdapter.setMediaListener(this);

        RecyclerView mediaAddList = (RecyclerView) view.findViewById(R.id.mediaAddList);
        mediaAddList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        UnitConverter converter = new UnitConverter(getActivity());
        mediaAddList.addItemDecoration(new SpaceItemDecoration((int)converter.convertDpToPx(10)));
        mediaAddList.setAdapter(mediaAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_create_story, menu);
    }

    public void setSelectedCoauthors(List<User> selectedCoauthors) {
        this.selectedCoauthors = selectedCoauthors;

        String coauthorsTemplate = getResources().getQuantityString(R.plurals.stories_list_item_coauthors, selectedCoauthors.size());
        coauthors.setText(String.format(coauthorsTemplate, selectedCoauthors.size()));
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

    private View.OnClickListener onCoauthorsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick selected coauthors: " + (selectedCoauthors != null ? selectedCoauthors.size() : 0));

            if (storyCreationListener != null)
                storyCreationListener.addCoauthors(selectedCoauthors);
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

    private View.OnClickListener onAddCoverClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.create_story_title_cover_image)
                    .setItems(R.array.create_story_cover_sources, onCoverSourceSelectedListener)
                    .create();
            alertDialog.show();
        }
    };

    private DialogInterface.OnClickListener onCoverSourceSelectedListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int position) {
            cover.setImageResource(R.drawable.cover_example);
            insertCoverInfo.setVisibility(View.GONE);
        }
    };

    public interface StoryCreationListener {
        void addCoauthors(List<User> coauthors);
    }
}
