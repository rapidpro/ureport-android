package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import in.ureport.R;
import in.ureport.models.Story;
import in.ureport.views.adapters.StoriesAdapter;

/**
 * Created by johncordeiro on 16/09/15.
 */
public class StoriesModerationFragment extends StoriesListFragment implements StoriesAdapter.StoryModerationListener {

    private static DatabaseReference.CompletionListener updateFinishedListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        publicType = false;
        setupContextDependencies();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        storiesAdapter.enableModerationMode(this);
        hideFloatingButton();
        setLoadingMessage(getString(R.string.load_message_wait));
    }

    @Override
    public Query loadData() {
        return storyServices.getStoriesModerationQuery();
    }

    @Override
    public void onApprove(Story story) {
        showLoading();
        storyServices.approveStory(story, (firebaseError, firebase) ->
                updateFinishedListener.onComplete(firebaseError, firebase));
    }

    @Override
    public void onDisapprove(Story story) {
        showLoading();
        storyServices.disapprovedStory(story, (firebaseError, firebase) ->
                updateFinishedListener.onComplete(firebaseError, firebase));
    }

    private void setupContextDependencies() {
        updateFinishedListener = (error, reference) -> {
            dismissLoading();
            if (error == null)
                displayToast(R.string.success_story_update_message);
            else
                displayToast(R.string.error_update_user);
        };
    }

    @Override
    protected boolean hasCreateStoryButton() {
        return false;
    }

    private void displayToast(@StringRes int messageId) {
        Toast.makeText(getContext(), messageId, Toast.LENGTH_SHORT).show();
    }

}
