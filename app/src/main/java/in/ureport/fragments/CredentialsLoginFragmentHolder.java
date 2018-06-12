package in.ureport.fragments;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import in.ureport.managers.FirebaseManager;

public class CredentialsLoginFragmentHolder {

    private static Firebase.AuthResultHandler firebaseAuthResultHandler;

    public static void registerFirebaseAuthResultHandler(Firebase.AuthResultHandler listener) {
        firebaseAuthResultHandler = listener;
    }

    public static void authWithPassword(String email, String password) {
        FirebaseManager.getReference().authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                firebaseAuthResultHandler.onAuthenticated(authData);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                firebaseAuthResultHandler.onAuthenticationError(firebaseError);
            }
        });
    }

}