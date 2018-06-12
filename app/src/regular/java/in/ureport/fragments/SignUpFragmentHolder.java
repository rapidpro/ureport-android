package in.ureport.fragments;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

import in.ureport.managers.FirebaseManager;
import in.ureport.models.User;
import in.ureport.models.holders.Login;
import in.ureport.network.UserServices;

public class SignUpFragmentHolder {

    private static ValueResultHandlerWrapper firebaseValueResultHandler;
    private static AuthResultHandlerWrapper firebaseAuthResultHandler;
    private static Firebase.CompletionListener firebaseCompletionListener;

    public static void registerFirebaseValueResultHandler(ValueResultHandlerWrapper listener) {
        firebaseValueResultHandler = listener;
    }

    public static void registerFirebaseAuthResultHandler(AuthResultHandlerWrapper listener) {
        firebaseAuthResultHandler = listener;
    }

    public static void registerFirebaseCompletionListener(Firebase.CompletionListener listener) {
       firebaseCompletionListener = listener;
    }

    public static void createUserAndAuthenticate(Login login, User user) {
        FirebaseManager.getReference().createUser(login.getEmail(), login.getPassword(), new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                firebaseValueResultHandler.onSuccess(result, login, user);
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                firebaseValueResultHandler.onError(firebaseError);
            }
        });
    }

    public static void authWithPassword(Login login, User user) {
        FirebaseManager.getReference().authWithPassword(login.getEmail(), login.getPassword(), new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                firebaseAuthResultHandler.onAuthenticated(user);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                firebaseAuthResultHandler.onAuthenticationError(firebaseError);
            }
        });
    }

    public static void saveUser(User user) {
        UserServices userServices = new UserServices();
        userServices.saveUser(user, (firebaseError, firebase) ->
                firebaseCompletionListener.onComplete(firebaseError, firebase)
        );
    }

    interface ValueResultHandlerWrapper {
        void onSuccess(Map<String, Object> result, Login login, User user);
        void onError(FirebaseError firebaseError);
    }

    interface AuthResultHandlerWrapper {
        void onAuthenticated(User user);
        void onAuthenticationError(FirebaseError firebaseError);
    }

}