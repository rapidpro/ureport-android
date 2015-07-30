package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.ilhasoft.support.tool.UnitConverter;
import in.ureport.R;
import in.ureport.UreportApplication;
import in.ureport.managers.PrototypeManager;
import in.ureport.managers.UserViewManager;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.util.SpaceItemDecoration;
import in.ureport.util.WrapLinearLayoutManager;
import in.ureport.views.adapters.ContributionAdapter;
import in.ureport.views.adapters.MediaAdapter;

/**
 * Created by johncordeiro on 7/16/15.
 */
public class StoryViewFragment extends Fragment {

    private static final String EXTRA_STORY = "story";
    private static final String EXTRA_USER = "user";

    private Story story;
    private User user;

    private ContributionAdapter contributionAdapter;

    public static StoryViewFragment newInstance(Story story, User user) {
        StoryViewFragment storyViewFragment = new StoryViewFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_STORY, story);
        args.putParcelable(EXTRA_USER, user);
        storyViewFragment.setArguments(args);

        return storyViewFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null && getArguments().containsKey(EXTRA_STORY)
        && getArguments().containsKey(EXTRA_USER)) {
            story = getArguments().getParcelable(EXTRA_STORY);
            user = getArguments().getParcelable(EXTRA_USER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_story_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
    }

    private void setupView(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(story.getTitle());

        TextView content = (TextView) view.findViewById(R.id.content);
        content.setText(story.getContent());

        TextView markers = (TextView) view.findViewById(R.id.markers);
        markers.setText(story.getMarkers());

        TextView author = (TextView) view.findViewById(R.id.author);
        author.setText("@"+story.getUser().getUsername());

        ImageView picture = (ImageView) view.findViewById(R.id.picture);
        picture.setImageResource(UserViewManager.getUserImage(getActivity(), story.getUser()));

        TextView contributors = (TextView) view.findViewById(R.id.contributors);
        contributors.setText(String.format(getString(R.string.stories_list_item_contributions), story.getContributions()));

        Button contribute = (Button) view.findViewById(R.id.contribute);
        contribute.setOnClickListener(onContributeClickListener);

        UnitConverter converter = new UnitConverter(getActivity());

        RecyclerView contributionList = (RecyclerView) view.findViewById(R.id.contributionList);
        contributionList.setLayoutManager(new WrapLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        contributionAdapter = new ContributionAdapter(user);
        contributionList.setAdapter(contributionAdapter);

        RecyclerView mediaList = (RecyclerView) view.findViewById(R.id.mediaList);
        mediaList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        SpaceItemDecoration mediaItemDecoration = new SpaceItemDecoration();
        mediaItemDecoration.setHorizontalSpaceWidth((int) converter.convertDpToPx(10));
        mediaList.addItemDecoration(mediaItemDecoration);

        MediaAdapter adapter = new MediaAdapter(getMediaList(), false);
        mediaList.setAdapter(adapter);

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.share);
        floatingActionButton.setOnClickListener(onShareClickListener);
    }

    @NonNull
    private List<String> getMediaList() {
        List<String> mediaObjectsList = new ArrayList<>();
        mediaObjectsList.add(MediaAdapter.MEDIA_PICTURE);
        mediaObjectsList.add(MediaAdapter.MEDIA_VIDEO);
        mediaObjectsList.add(MediaAdapter.MEDIA_PICTURE);
        return mediaObjectsList;
    }

    private View.OnClickListener onContributeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(UreportApplication.validateUserLogin(getActivity())) {
                view.setVisibility(View.GONE);
                contributionAdapter.startContribution();
            }
        }
    };

    private View.OnClickListener onShareClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PrototypeManager.showPrototypeAlert(getActivity());
        }
    };
}
