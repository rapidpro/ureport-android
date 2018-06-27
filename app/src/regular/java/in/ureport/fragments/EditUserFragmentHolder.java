package in.ureport.fragments;

import android.content.Context;

import com.firebase.client.Firebase;

import in.ureport.models.User;
import in.ureport.models.geonames.CountryInfo;
import in.ureport.network.UserServices;
import in.ureport.tasks.SaveContactTask;
import io.rapidpro.sdk.core.models.base.ContactBase;

public class EditUserFragmentHolder {

    private static Firebase.CompletionListener firebaseCompletionListener;
    private static SaveContactTask.Listener saveContactTaskListener;

    public static void registerFirebaseCompletionListener(Firebase.CompletionListener listener) {
        firebaseCompletionListener = listener;
    }

    public static void registerSaveContactTaskListener(SaveContactTask.Listener listener) {
        saveContactTaskListener = listener;
    }

    public static void editUser(User user) {
        UserServices userServices = new UserServices();
        userServices.editUser(user, (firebaseError, firebase) ->
                firebaseCompletionListener.onComplete(firebaseError, firebase)
        );
    }

    public static void updateContactToRapidpro(Context context, User user, CountryInfo countryInfo) {
        new SaveContactTask(context, user, false, countryInfo) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                saveContactTaskListener.onStart();
            }

            @Override
            protected void onPostExecute(ContactBase contact) {
                super.onPostExecute(contact);
                saveContactTaskListener.onFinished(contact, user);
            }
        }.execute();
    }

}
