package in.ureport.helpers;

import android.support.annotation.NonNull;

import com.firebase.client.FirebaseError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import in.ureport.AnalyticsTracker;

/**
 * Created by johncordeiro on 29/10/15.
 */
public class AnalyticsHelper {

    public static void sendException(Exception exception) {
        Tracker tracker = AnalyticsTracker.get(AnalyticsTracker.Target.APP);
        tracker.send(new HitBuilders.ExceptionBuilder()
                .setDescription(getExceptionDescription(exception))
                .setFatal(false)
                .build());
    }

    public static void sendFirebaseError(FirebaseError firebaseError) {
        Tracker tracker = AnalyticsTracker.get(AnalyticsTracker.Target.APP);
        tracker.send(new HitBuilders.ExceptionBuilder()
                .setDescription(getFirebaseErrorDescription(firebaseError))
                .setFatal(false)
                .build());
    }

    private static String getExceptionDescription(Exception exception) {
        return exception != null ? exception.getLocalizedMessage()
                + "\n" + exception.getMessage() + "\ncause: " + exception.getCause() : "No description";
    }

    @NonNull
    private static String getFirebaseErrorDescription(FirebaseError firebaseError) {
        return firebaseError.getCode() + " - " + firebaseError.getMessage() + " \n" + firebaseError.getDetails();
    }

}
