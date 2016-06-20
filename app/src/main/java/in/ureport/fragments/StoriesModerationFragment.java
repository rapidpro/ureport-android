package in.ureport.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import in.ureport.R;
import in.ureport.models.Story;
import in.ureport.views.adapters.StoriesAdapter;

/**
 * Created by johncordeiro on 16/09/15.
 */
public class StoriesModerationFragment extends StoriesListFragment implements StoriesAdapter.StoryModerationListener {

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        publicType = false;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        storiesAdapter.enableModerationMode(this);
        hideFloatingButton();
    }

    public void loadData() {
        storyServices.addStoryModerateChildEventListener(childEventListener);
    }

    @Override
    public void onApprove(Story story) {
        progressDialog = ProgressDialog.show(getActivity(), null
                , getString(R.string.load_message_wait), true, false);
        storyServices.approveStory(story, onUpdateFinishedListener);
    }

    @Override
    public void onDisapprove(Story story) {
        progressDialog = ProgressDialog.show(getActivity(), null
                , getString(R.string.load_message_wait), true, false);
        storyServices.disapprovedStory(story, onUpdateFinishedListener);
    }

    private Firebase.CompletionListener onUpdateFinishedListener = new Firebase.CompletionListener() {
        @Override
        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
            progressDialog.dismiss();
            if (firebaseError == null) {
                displayToast(R.string.success_story_update_message);
            } else {
                displayToast(R.string.error_update_user);
            }
        }
    };

    private void displayToast(@StringRes int messageId) {
        Toast.makeText(getActivity(), messageId, Toast.LENGTH_SHORT).show();
    }
}
