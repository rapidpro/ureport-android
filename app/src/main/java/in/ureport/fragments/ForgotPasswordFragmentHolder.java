package in.ureport.fragments;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import in.ureport.managers.FirebaseManager;

public class ForgotPasswordFragmentHolder {

    private static Firebase.ResultHandler firebaseForgotPasswordResultHandler;

    public static void registerFirebaseForgotPasswordResultHandler(Firebase.ResultHandler resultHandler) {
        firebaseForgotPasswordResultHandler = resultHandler;
    }

    public static void resetPassword(String email) {
        FirebaseManager.getReference().resetPassword(email, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                firebaseForgotPasswordResultHandler.onSuccess();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                firebaseForgotPasswordResultHandler.onError(firebaseError);
            }
        });
    }

}
