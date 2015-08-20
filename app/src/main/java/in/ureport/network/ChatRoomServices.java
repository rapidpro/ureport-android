package in.ureport.network;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import in.ureport.listener.OnChatLastMessageLoadedListener;
import in.ureport.listener.OnChatMembersLoadedListener;
import in.ureport.listener.OnChatRoomCreatedListener;
import in.ureport.listener.OnChatRoomLoadedListener;
import in.ureport.managers.FirebaseManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.ChatMessage;
import in.ureport.models.ChatRoom;
import in.ureport.models.GroupChatRoom;
import in.ureport.models.IndividualChatRoom;
import in.ureport.models.User;
import in.ureport.helpers.ValueEventListenerAdapter;

/**
 * Created by johncordeiro on 16/08/15.
 */
public class ChatRoomServices {

    public static final String chatRoomPath = "chat_room";
    public static final String membersPath = "chat_members";
    public static final String messagesPath = "chat_messages";

    public void addChildEventListenerForChatMessages(String key, ChildEventListener listener) {
        FirebaseManager.getReference().child(messagesPath).child(key).orderByKey().addChildEventListener(listener);
    }

    public void saveChatMessage(ChatRoom chatRoom, ChatMessage chatMessage) {
        User user = new User();
        user.setKey(chatMessage.getUser().getKey());
        chatMessage.setUser(user);

        FirebaseManager.getReference().child(messagesPath).child(chatRoom.getKey())
                .push().setValue(chatMessage);
    }

    public void getChatRoom(final String key, final OnChatRoomLoadedListener listener) {
        FirebaseManager.getReference().child(chatRoomPath).child(key).addListenerForSingleValueEvent(new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) return;

                final ChatRoom chatRoom = getChatRoomInstance(dataSnapshot);
                chatRoom.setKey(key);
                loadExtraDataForChatRoom(chatRoom, key, listener);
            }
        });
    }

    private void loadExtraDataForChatRoom(final ChatRoom chatRoom, final String key, final OnChatRoomLoadedListener listener) {
        loadChatRoomMembers(key, new OnChatMembersLoadedListener() {
            @Override
            public void onChatMembersLoaded(final ChatMembers chatMembers) {
                loadLastChatMessage(key, chatMembers, new OnChatLastMessageLoadedListener() {
                    @Override
                    public void onChatLastMessageLoaded(ChatMessage chatMessage) {
                        listener.onChatRoomLoaded(chatRoom, chatMembers, chatMessage);
                    }
                });
            }
        });
    }

    private ChatRoom getChatRoomInstance(DataSnapshot dataSnapshot) {
        Map<String, String> value = (Map<String, String>) dataSnapshot.getValue();

        final ChatRoom chatRoom;
        if (ChatRoom.Type.valueOf(value.get("type")) == ChatRoom.Type.Group) {
            chatRoom = dataSnapshot.getValue(GroupChatRoom.class);
        } else {
            chatRoom = dataSnapshot.getValue(IndividualChatRoom.class);
        }
        return chatRoom;
    }

    private void loadLastChatMessage(String key, final ChatMembers chatMembers
            , final OnChatLastMessageLoadedListener onChatLastMessageLoadedListener) {
        FirebaseManager.getReference().child(messagesPath).child(key).limitToLast(1)
        .addListenerForSingleValueEvent(new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);

                ChatMessage lastChatMessage = null;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    lastChatMessage = snapshot.getValue(ChatMessage.class);
                    if (lastChatMessage != null) {
                        int indexOfUser = chatMembers.getUsers().indexOf(lastChatMessage.getUser());
                        if (indexOfUser >= 0)
                            lastChatMessage.setUser(chatMembers.getUsers().get(indexOfUser));
                    }
                    break;
                }

                onChatLastMessageLoadedListener.onChatLastMessageLoaded(lastChatMessage);
            }
        });
    }

    private void loadChatRoomMembers(String key, final OnChatMembersLoadedListener listener) {
        FirebaseManager.getReference().child(membersPath).child(key).addListenerForSingleValueEvent(new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> users = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = new User();
                    user.setKey(snapshot.getKey());

                    users.add(user);
                }

                ChatMembers chatMembers = new ChatMembers();
                chatMembers.setUsers(users);

                loadUsersFromChatRooms(chatMembers, listener);
            }
        });
    }

    private void loadUsersFromChatRooms(final ChatMembers chatMembers, final OnChatMembersLoadedListener listener) {
        for (int position = 0; position < chatMembers.getUsers().size(); position++) {
            User user = chatMembers.getUsers().get(position);
            UserServices userServices = new UserServices();

            final int index = position;
            userServices.getUser(user.getKey(), new ValueEventListenerAdapter() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User userLoaded = dataSnapshot.getValue(User.class);
                    chatMembers.getUsers().set(index, userLoaded);

                    if(index == chatMembers.getUsers().size()-1) listener.onChatMembersLoaded(chatMembers);
                }
            });
        }
    }

    public void saveGroupChatRoom(final GroupChatRoom groupChatRoom, final List<User> members, final OnChatRoomCreatedListener onChatRoomCreatedListener) {
        final User me = new User();
        me.setKey(FirebaseManager.getReference().getAuth().getUid());
        groupChatRoom.setAdministrator(me);

        FirebaseManager.getReference().child(chatRoomPath).push().setValue(groupChatRoom, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {
                    addChatMember(me, firebase);
                    for (User member : members) {
                        addChatMember(member, firebase);
                    }

                    groupChatRoom.setKey(firebase.getKey());
                    onChatRoomCreatedListener.onChatRoomCreated(groupChatRoom);
                }
            }
        });
    }

    public void saveIndividualChatRoom(final User friend, final OnChatRoomCreatedListener onChatRoomCreatedListener) {
        final IndividualChatRoom chatRoom = new IndividualChatRoom();
        chatRoom.setCreatedDate(new Date());

        FirebaseManager.getReference().child(chatRoomPath).push().setValue(chatRoom, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {
                    User me = new User();
                    me.setKey(FirebaseManager.getReference().getAuth().getUid());

                    addChatMember(me, firebase);
                    addChatMember(friend, firebase);

                    chatRoom.setKey(firebase.getKey());
                    onChatRoomCreatedListener.onChatRoomCreated(chatRoom);
                }
            }
        });
    }

    private void addChatMember(User user, Firebase firebase) {
        UserServices userServices = new UserServices();
        userServices.addUserChatRoom(user.getKey(), firebase.getKey());

        FirebaseManager.getReference().child(membersPath)
                .child(firebase.getKey())
                .child(user.getKey())
                .setValue(true);
    }
}
