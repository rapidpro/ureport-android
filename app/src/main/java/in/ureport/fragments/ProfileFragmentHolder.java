package in.ureport.fragments;

import android.content.Context;

import com.firebase.client.DataSnapshot;

import in.ureport.helpers.TransferListenerAdapter;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.managers.TransferManager;
import in.ureport.managers.UserManager;
import in.ureport.models.LocalMedia;
import in.ureport.models.Media;
import in.ureport.network.UserServices;

public class ProfileFragmentHolder {

    private static ValueEventListenerAdapter firebaseValueEventListenerAdapter;
    private static TransferListenerAdapter imageTransferListenerAdapter;

    public static void registerFirebaseValueEventListenerAdapter(ValueEventListenerAdapter listenerAdapter) {
        firebaseValueEventListenerAdapter = listenerAdapter;
    }

    public static void registerImageTransferListenerAdapter(TransferListenerAdapter listenerAdapter) {
        imageTransferListenerAdapter = listenerAdapter;
    }

    public static void loadUser() {
        if (firebaseValueEventListenerAdapter == null)
            return;

        UserServices userServices = new UserServices();
        userServices.getUser(UserManager.getUserId(), new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseValueEventListenerAdapter.onDataChange(dataSnapshot);
            }
        });
    }

    public static void transferMedia(Context context, LocalMedia localMedia) {
        if (imageTransferListenerAdapter == null)
            return;

        imageTransferListenerAdapter.onStart();
        try {
            TransferManager transferManager = new TransferManager(context);
            transferManager.transferMedia(localMedia, "user", new TransferListenerAdapter(context, localMedia) {
                @Override
                public void onTransferFinished(Media media) {
                    super.onTransferFinished(media);
                    imageTransferListenerAdapter.onTransferFinished(media);
                }

                @Override
                public void onError(int id, Exception ex) {
                    super.onError(id, ex);
                    imageTransferListenerAdapter.onError(id, ex);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            imageTransferListenerAdapter.onTransferFailed();
        }
    }

}
