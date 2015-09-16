package in.ureport.network;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.FirebaseManager;
import in.ureport.managers.UserManager;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 14/08/15.
 */
public class UserServices extends ProgramServices {

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

    public void addChildEventListenerForChatRooms(String key, ChildEventListener childEventListener) {
        FirebaseManager.getReference().child(userPath).child(key)
                .child("chatRooms").addChildEventListener(childEventListener);
    }

    public void getUser(String key, ValueEventListener valueEventListener) {
        FirebaseManager.getReference().child(userPath).child(key).addListenerForSingleValueEvent(valueEventListener);
    }

    public void loadByName(String nickname, final OnLoadAllUsersListener onLoadAllUsersListener) {
        FirebaseManager.getReference().child(userPath).orderByChild("nickname").equalTo(nickname)
                .addListenerForSingleValueEvent(new ValueEventListenerAdapter() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        super.onDataChange(dataSnapshot);
                        handleDataResponse(dataSnapshot, onLoadAllUsersListener);
                    }
                });
    }

    private void handleDataResponse(DataSnapshot dataSnapshot, OnLoadAllUsersListener onLoadAllUsersListener) {
        String currentUserKey = UserManager.getUserId();

        List<User> users = new ArrayList<User>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            User user = snapshot.getValue(User.class);
            if(!snapshot.getKey().equals(currentUserKey)) {
                users.add(user);
            }
        }

        if(onLoadAllUsersListener != null) onLoadAllUsersListener.onLoadAllUsers(users);
    }

    public void loadAll(final OnLoadAllUsersListener onLoadAllUsersListener) {
        String countryCode = CountryProgramManager.getCurrentCountryProgram().getCode();

        Query query = FirebaseManager.getReference().child(userPath).orderByChild("countryProgram").equalTo(countryCode);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                handleDataResponse(dataSnapshot, onLoadAllUsersListener);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void editUser(User user, Firebase.CompletionListener listener) {
        Firebase userReference = FirebaseManager.getReference().child(userPath).child(user.getKey());
        userReference.child("nickname").setValue(user.getNickname());
        userReference.child("birthday").setValue(user.getBirthday());
        userReference.child("state").setValue(user.getState());
        userReference.child("gender").setValue(user.getGender(), listener);
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
