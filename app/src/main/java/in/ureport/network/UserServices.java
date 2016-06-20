package in.ureport.network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.helpers.ValueIncrementerTransaction;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.FirebaseManager;
import in.ureport.managers.GameficationManager;
import in.ureport.managers.UserManager;
import in.ureport.models.Media;
import in.ureport.models.Story;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 14/08/15.
 */
public class UserServices extends ProgramServices {

    private static final String TAG = "UserServices";

    private static final String userPath = "user";
    private static final String userModeratorPath = "user_moderator";

    public void isUserCountryModerator(User user, ValueEventListener listener) {
        getDefaultRoot().child(userModeratorPath).child(user.getKey())
                .addListenerForSingleValueEvent(listener);
    }

    public void isUserMaster(User user, ValueEventListener listener) {
        FirebaseManager.getReference().child(userModeratorPath).child(user.getKey())
                .addListenerForSingleValueEvent(listener);
    }

    public void updatePushIdentity(String userKey, String identityId) {
        FirebaseManager.getReference().child(userPath).child(userKey)
                .child("pushIdentity").setValue(identityId);
    }

    public void addUserChatRoom(String userKey, String chatRoomKey) {
        FirebaseManager.getReference().child(userPath)
                .child(userKey).child("chatRooms").child(chatRoomKey).setValue(true);
    }

    public void removeUserChatRoom(String userKey, String chatRoomKey) {
        FirebaseManager.getReference().child(userPath)
                .child(userKey).child("chatRooms").child(chatRoomKey).removeValue();
    }

    public Firebase getUserChatRoomsReference(String key) {
        return FirebaseManager.getReference().child(userPath).child(key)
                .child("chatRooms");
    }

    public void addValueEventListenerForChatRooms(String key, ValueEventListener listener) {
        FirebaseManager.getReference().child(userPath).child(key)
                .child("chatRooms").addValueEventListener(listener);
    }

    public void loadChatRooms(String key, ValueEventListener valueEventListener) {
        FirebaseManager.getReference().child(userPath).child(key)
                .child("chatRooms").addListenerForSingleValueEvent(valueEventListener);
    }

    public void getUser(String key, ValueEventListener valueEventListener) {
        FirebaseManager.getReference().child(userPath).child(key).addListenerForSingleValueEvent(valueEventListener);
    }

    private void handleDataResponse(DataSnapshot dataSnapshot, OnLoadAllUsersListener onLoadAllUsersListener) {
        String currentUserKey = UserManager.getUserId();

        List<User> users = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            try {
                User user = snapshot.getValue(User.class);
                if (!snapshot.getKey().equals(currentUserKey) && (user.getPublicProfile() == null || user.getPublicProfile())) {
                    users.add(user);
                }
            } catch(Exception exception) {
                Log.e(TAG, "handleDataResponse: ", exception);
            }
        }

        if(onLoadAllUsersListener != null) onLoadAllUsersListener.onLoadAllUsers(users);
    }

    public void incrementContributionPoint() {
        FirebaseManager.getReference().child(userPath).child(UserManager.getUserId()).child("points")
                .runTransaction(new ValueIncrementerTransaction(GameficationManager.CONTRIBUTION_POINTS));
    }

    public void incrementStoryCount(Story story) {
        FirebaseManager.getReference().child(userPath).child(UserManager.getUserId()).child("stories")
                .runTransaction(new ValueIncrementerTransaction(1));

        FirebaseManager.getReference().child(userPath).child(UserManager.getUserId()).child("points")
                .runTransaction(new ValueIncrementerTransaction(GameficationManager.getPointsForStory(story)));
    }

    public void loadRanking(final OnLoadAllUsersListener onLoadAllUsersListener) {
        FirebaseManager.getReference().child(userPath).orderByChild("points")
                .addListenerForSingleValueEvent(new ValueEventListenerAdapter() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        super.onDataChange(dataSnapshot);

                        List<User> users = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            users.add(user);
                        }

                        onLoadAllUsersListener.onLoadAllUsers(users);
                    }
                });
    }

    public ValueEventListener loadAll(final OnLoadAllUsersListener onLoadAllUsersListener) {
        Query query = FirebaseManager.getReference().child(userPath);
        return loadUsers(onLoadAllUsersListener, query);
    }

    public void addCountryModerator(User user, Firebase.CompletionListener listener) {
        getDefaultRoot().child(userModeratorPath).child(user.getKey()).setValue(true, listener);
    }

    public void removeCountryModerator(User user, Firebase.CompletionListener listener) {
        getDefaultRoot().child(userModeratorPath).child(user.getKey()).removeValue(listener);
    }

    public void loadMasterModerators(final OnLoadAllUsersListener onLoadAllUsersListener) {
        FirebaseManager.getReference().child(userModeratorPath).addListenerForSingleValueEvent(new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                List<User> users = getUsersWithKey(dataSnapshot);
                onLoadAllUsersListener.onLoadAllUsers(users);
            }
        });
    }

    public void loadCountryModerators(final OnLoadAllUsersListener onLoadAllUsersListener) {
        getDefaultRoot().child(userModeratorPath).addListenerForSingleValueEvent(new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                List<User> users = getUsersWithKey(dataSnapshot);
                onLoadAllUsersListener.onLoadAllUsers(users);
            }
        });
    }

    @NonNull
    private List<User> getUsersWithKey(DataSnapshot dataSnapshot) {
        List<User> users = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            User user = new User();
            user.setKey(snapshot.getKey());
            users.add(user);
        }
        return users;
    }

    public void removeCountryCodeListener(ValueEventListener listener) {
        Query query = getUserCountryProgramQuery();
        query.removeEventListener(listener);
    }

    public ValueEventListener loadByCountryCode(final OnLoadAllUsersListener onLoadAllUsersListener) {
        Query query = getUserCountryProgramQuery();
        return loadUsers(onLoadAllUsersListener, query);
    }

    private Query getUserCountryProgramQuery() {
        String countryCode = CountryProgramManager.getCurrentCountryProgram().getCode();
        return FirebaseManager.getReference().child(userPath).orderByChild("countryProgram").equalTo(countryCode);
    }

    private ValueEventListener loadUsers(final OnLoadAllUsersListener onLoadAllUsersListener, Query query) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                handleDataResponse(dataSnapshot, onLoadAllUsersListener);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        };
        query.addValueEventListener(listener);
        return listener;
    }

    public void changePublicProfile(User user, Boolean publicProfile, Firebase.CompletionListener listener) {
        Firebase userReference = FirebaseManager.getReference().child(userPath).child(user.getKey());
        userReference.child("publicProfile").setValue(publicProfile, listener);
    }

    public void editUserPicture(User user, Firebase.CompletionListener listener) {
        Firebase userReference = FirebaseManager.getReference().child(userPath).child(user.getKey());
        userReference.child("picture").setValue(user.getPicture(), listener);
    }

    public void editUser(User user, Firebase.CompletionListener listener) {
        Firebase userReference = FirebaseManager.getReference().child(userPath).child(user.getKey());

        Map<String, Object> children = new HashMap<>();
        children.put("nickname", user.getNickname());
        children.put("birthday", user.getBirthday().getTime());
        children.put("state", user.getState());
        children.put("gender", user.getGender().toString());

        userReference.updateChildren(children, listener);
    }

    public void saveUser(User user, Firebase.CompletionListener listener) {
        FirebaseManager.getReference().child(userPath).child(user.getKey()).setValue(user, listener);
    }

    public void keepUserOffline(User user) {
        FirebaseManager.getReference().child(userPath).child(user.getKey()).keepSynced(true);
    }

    public interface OnLoadAllUsersListener {
        void onLoadAllUsers(List<User> users);
    }
}
