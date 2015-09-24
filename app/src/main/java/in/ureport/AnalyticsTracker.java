package in.ureport;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;
import java.util.Map;

public final class AnalyticsTracker {

    public enum Target {
        APP,
    }

    private static AnalyticsTracker sInstance;

    private static final Map<Target, Tracker> mTrackers = new HashMap<>();
    private static Context mContext;

    public static synchronized void initialize(Context context) {
        if (sInstance != null) {
            throw new IllegalStateException("Extra call to initialize analytics trackers");
        }
        sInstance = new AnalyticsTracker(context);
        get(Target.APP);
    }

    public static synchronized AnalyticsTracker getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("Call initialize() before getInstance()");
        }
        return sInstance;
    }

    private AnalyticsTracker(Context context) {
        mContext = context.getApplicationContext();
    }

    public static synchronized Tracker get(Target target) {
        if (!mTrackers.containsKey(target)) {
            GoogleAnalytics googleAnalytics = GoogleAnalytics.getInstance(mContext);
            googleAnalytics.setDryRun(BuildConfig.DEBUG);

            Tracker tracker;
            switch (target) {
                case APP:
                    tracker = googleAnalytics.newTracker(R.xml.app_tracker);
                    break;
                default:
                    throw new IllegalArgumentException("Unhandled analytics target " + target);
            }
            mTrackers.put(target, tracker);
        }
        return mTrackers.get(target);
    }
}
