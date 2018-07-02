package in.ureport.fragments;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import in.ureport.managers.FirebaseManager;

public class ForgotPasswordFragmentHolder {

    private static Firebase.ResultHandler firebasePasswordResultHandler;

    public static void registerFirebasePasswordResultHandler(Firebase.ResultHandler resultHandler) {
        firebasePasswordResultHandler = resultHandler;
    }

    public static void resetPassword(String email) {
        FirebaseManager.getReference().resetPassword(email, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                firebasePasswordResultHandler.onSuccess();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                firebasePasswordResultHandler.onError(firebaseError);
            }
        });
    }

}
