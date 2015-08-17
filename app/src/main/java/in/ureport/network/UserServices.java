package in.ureport.network;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import in.ureport.managers.FirebaseManager;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 14/08/15.
 */
public class UserServices {

    public static final String path = "user";

    public void addUserChatRoom(String userKey, String chatRoomKey) {
        FirebaseManager.getReference().child(path)
                .child(userKey).child("chatRooms").child(chatRoomKey).setValue(true);
    }

    public void addChildEventListenerForChatRooms(String key, ChildEventListener childEventListener) {
        FirebaseManager.getReference().child(path).child(key)
                .child("chatRooms").addChildEventListener(childEventListener);
    }

    public void getUser(String key, ValueEventListener valueEventListener) {
        FirebaseManager.getReference().child(path).child(key).addListenerForSingleValueEvent(valueEventListener);
    }

    public void loadAll(final OnLoadAllUsersListener onLoadAllUsersListener) {
        FirebaseManager.getReference().child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currentUserKey = FirebaseManager.getReference().getAuth().getUid();

                List<User> users = new ArrayList<User>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if(!snapshot.getKey().equals(currentUserKey)) {
                        users.add(user);
                    }
                }

                if(onLoadAllUsersListener != null) onLoadAllUsersListener.onLoadAllUsers(users);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    public void saveUser(User user, Firebase.CompletionListener listener) {
        FirebaseManager.getReference().child(path).child(user.getKey()).setValue(user, listener);
    }

    public void keepUserOffline(User user) {
        FirebaseManager.getReference().child(path).child(user.getKey()).keepSynced(true);
    }

    public interface OnLoadAllUsersListener {
        void onLoadAllUsers(List<User> users);
    }
}
