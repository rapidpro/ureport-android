package in.ureport.loader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import in.ureport.managers.DynamoDBManager;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.network.StoryServices;
import in.ureport.network.UserServices;
import in.ureport.util.StoryComparator;

/**
 * Created by johncordeiro on 7/14/15.
 */
public class StoriesLoader extends AsyncTaskLoader<List<Story>> {

    private static final String TAG = "StoriesLoader";

    private User user;
    private boolean publicType = true;

    public StoriesLoader(Context context) {
        super(context);
    }

    public StoriesLoader(Context context, User user) {
        super(context);
        this.user = user;
        publicType = false;
    }

    @Override
    public List<Story> loadInBackground() {
        try {
            StoryServices storyServices = new StoryServices();
            List<Story> stories = storyServices.loadAll();

            storyServices.loadUsersForStories(stories);
            Collections.sort(stories, new StoryComparator());

            return stories;
        } catch(Exception exception) {
            Log.e(TAG, "loadInBackground ", exception);
        }
        return new ArrayList<>();
    }

}
