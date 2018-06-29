package in.ureport.fragments;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import in.ureport.managers.FirebaseManager;
import in.ureport.models.User;

public class ChangePasswordFragmentHolder {


    private static Firebase.ResultHandler passwordResultHandler;

    public static void registerPasswordResultHandler(Firebase.ResultHandler resultHandler) {
        passwordResultHandler = resultHandler;
    }

    public static void changePassword(User user, String oldPassword, String newPassword) {
        FirebaseManager.changePassword(user, oldPassword, newPassword, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                passwordResultHandler.onSuccess();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                passwordResultHandler.onError(firebaseError);
            }
        });
    }

}
