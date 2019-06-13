package in.ureport.services;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.managers.FcmTopicManager;
import in.ureport.managers.UserManager;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.network.StoryServices;
import in.ureport.network.UserServices;
import io.fcmchannel.sdk.core.models.v2.Contact;
import io.fcmchannel.sdk.services.FcmClientRegistrationIntentService;

/**
 * Created by John Cordeiro on 5/15/17.
 * Copyright © 2017 ureport-android, Inc. All rights reserved.
 */

public class UreportFcmRegistrationService extends FcmClientRegistrationIntentService {

    private static final String TAG = "UreportFcmRegistration";

    @Override
    public void onFcmRegistered(String pushIdentity, Contact contact) {
        try {
            String userKey = UserManager.getUserId();

            if (userKey != null) {
                registerTopics(pushIdentity, userKey);
            }
        } catch(Exception exception) {
            Log.e(TAG, "onHandleIntent ", exception);
        }
    }

    private void registerTopics(final String pushIdentity, String userKey) {
        UserServices userServices = new UserServices();
        userServices.updatePushIdentity(userKey, pushIdentity);
        userServices.getUser(userKey, new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                final User user = dataSnapshot.getValue(User.class);
                user.setKey(dataSnapshot.getKey());

                registerToChatTopics(user, pushIdentity);
                registerToStoryTopics(user);
            }
        });
    }

    private void registerToStoryTopics(final User user) {
        StoryServices storyServices = new StoryServices();
        storyServices.loadStoriesForUser(user, new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Story story = snapshot.getValue(Story.class);

                        FcmTopicManager fcmTopicManager = new FcmTopicManager(getApplicationContext());
                        fcmTopicManager.registerToStoryTopic(user, story);
                    }
                }
            }
        });
    }

    private void registerToChatTopics(final User user, final String pushIdentity) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                FcmTopicManager fcmTopicManager = new FcmTopicManager(UreportFcmRegistrationService.this);
                fcmTopicManager.registerToChatRoomTopics(pushIdentity, user);
                return null;
            }
        }.execute();
    }
}
