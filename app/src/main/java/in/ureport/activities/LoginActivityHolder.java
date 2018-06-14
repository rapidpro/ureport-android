package in.ureport.activities;

import android.content.Context;

import in.ureport.models.User;
import in.ureport.tasks.SaveContactTask;
import io.rapidpro.sdk.core.models.base.ContactBase;

public class LoginActivityHolder {

    private static RapidProContactSavingListener rapidProContactSavingListener;

    public static void registerContactSavingRapidProListener(RapidProContactSavingListener listener) {
        rapidProContactSavingListener = listener;
    }

    public static void saveContactOnRapidPro(Context context, User user, boolean newUser) {
        new SaveContactTask(context, newUser) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                rapidProContactSavingListener.onStart();
            }
            @Override
            protected void onPostExecute(ContactBase contact) {
                super.onPostExecute(contact);
                rapidProContactSavingListener.onFinished(contact, user);
            }
        }.execute(user);
    }

    interface RapidProContactSavingListener {
        void onStart();
        void onFinished(ContactBase contact, User user);
    }

}