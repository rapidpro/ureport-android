package in.ureport.fragments;

import android.content.Context;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.List;
import java.util.Map;

import in.ureport.managers.TransferManager;
import in.ureport.models.LocalMedia;
import in.ureport.models.Media;
import in.ureport.models.Story;
import in.ureport.network.StoryServices;

public class CreateStoryFragmentHolder {

    private static TransferManager.OnTransferMediasListener mediasTransferListener;
    private static FirebaseStorySavingCompletionListener firebaseStorySavingCompletionListener;

    public static void registerMediasTransferListener(TransferManager.OnTransferMediasListener listener) {
        mediasTransferListener = listener;
    }

    public static void registerFirebaseStorySavingCompletionListener(FirebaseStorySavingCompletionListener completionListener) {
        firebaseStorySavingCompletionListener = completionListener;
    }

    public static void transferMedias(Context context, List<Media> mediaList) {
        try {
            TransferManager transferManager = new TransferManager(context);
            transferManager.transferMedias(mediaList, "story", new TransferManager.OnTransferMediasListener() {
                @Override
                public void onTransferMedias(Map<LocalMedia, Media> medias) {
                    mediasTransferListener.onTransferMedias(medias);
                }

                @Override
                public void onWaitingConnection() {
                    mediasTransferListener.onWaitingConnection();
                }

                @Override
                public void onFailed() {
                    mediasTransferListener.onFailed();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            mediasTransferListener.onFailed();
        }
    }

    public static void cancelTransfer(Context context) {
        try {
            TransferManager transferManager = new TransferManager(context);
            transferManager.cancelTransfer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveStory(Story story) {
        StoryServices storyServices = new StoryServices();
        storyServices.saveStory(story, (firebaseError, firebase) ->
                firebaseStorySavingCompletionListener.onComplete(firebaseError, firebase, story)
        );
    }

    interface FirebaseStorySavingCompletionListener {
        void onComplete(FirebaseError firebaseError, Firebase firebase, Story story);
    }

}
