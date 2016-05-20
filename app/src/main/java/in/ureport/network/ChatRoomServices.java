package in.ureport.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import in.ureport.helpers.ChildEventListenerAdapter;
import in.ureport.listener.ChatRoomInterface;
import in.ureport.managers.GcmTopicManager;
import in.ureport.managers.UserManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.ChatMessage;
import in.ureport.models.ChatRoom;
import in.ureport.models.GroupChatRoom;
import in.ureport.models.IndividualChatRoom;
import in.ureport.models.User;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.models.holders.ChatRoomHolder;

/**
 * Created by johncordeiro on 16/08/15.
 */
public class ChatRoomServices extends ProgramServices {

    private static final String TAG = "ChatRoomServices";

    public static final String chatRoomPath = "chat_room";
    public static final String membersPath = "chat_members";
    public static final String messagesPath = "chat_messages";

    public void addChildEventListenerForPublicGroups(ChildEventListener listener) {
        getDefaultRoot().child(chatRoomPath).orderByChild("privateAccess")
                .equalTo(false).addChildEventListener(listener);
    }

    public void removeChildEventListenerForPublicGroups(ChildEventListener listener) {
        getDefaultRoot().child(chatRoomPath).orderByChild("privateAccess")
                .equalTo(false).removeEventListener(listener);
    }

    public void addChildEventListenerForChatMessages(String key, ChildEventListener listener) {
        getDefaultRoot().child(messagesPath).child(key).orderByChild("date").addChildEventListener(listener);
    }

    public void removeEventListenerForChatMessages(String key, ChildEventListener listener) {
        getDefaultRoot().child(messagesPath).child(key).orderByChild("date").removeEventListener(listener);
    }

    public void addValueListenForChatRoom(ChatRoom chatRoom, ValueEventListener listener) {
        getDefaultRoot().child(chatRoomPath).child(chatRoom.getKey()).addValueEventListener(listener);
    }

    public void removeValueListenForChatRoom(ChatRoom chatRoom, ValueEventListener listener) {
        getDefaultRoot().child(chatRoomPath).child(chatRoom.getKey()).removeEventListener(listener);
    }

    public void blockChatRoom(ChatRoom chatRoom) {
        getDefaultRoot().child(chatRoomPath).child(chatRoom.getKey()).child("blocked").setValue(UserManager.getUserId());
    }

    public void unblockChatRoom(ChatRoom chatRoom) {
        getDefaultRoot().child(chatRoomPath).child(chatRoom.getKey()).child("blocked").removeValue();
    }

    public void closeChatRoom(Context context, ChatRoom chatRoom, ChatMembers chatMembers) {
        for (User user : chatMembers.getUsers()) {
            removeChatMember(context, user, chatRoom.getKey());
        }
        getDefaultRoot().child(membersPath).child(chatRoom.getKey()).removeValue();
        getDefaultRoot().child(chatRoomPath).child(chatRoom.getKey()).removeValue();
    }

    public void removeChatMessage(ChatRoom chatRoom, ChatMessage chatMessage, Firebase.CompletionListener listener) {
        getDefaultRoot().child(messagesPath).child(chatRoom.getKey())
                .child(chatMessage.getKey()).removeValue(listener);
    }

    public void saveChatMessage(ChatRoom chatRoom, ChatMessage chatMessage) {
        setUserIfNeeded(chatMessage);
        getDefaultRoot().child(messagesPath).child(chatRoom.getKey())
                .push().setValue(chatMessage);
    }

    private void setUserIfNeeded(ChatMessage chatMessage) {
        if(chatMessage.getUser() != null && chatMessage.getUser().getKey() != null) {
            User user = new User();
            user.setKey(chatMessage.getUser().getKey());
            chatMessage.setUser(user);
        }
    }

    public void getChatRoom(final String key, final ChatRoomInterface.OnChatRoomLoadedListener listener) {
        getDefaultRoot().child(chatRoomPath).child(key).addListenerForSingleValueEvent(
                new ValueEventListenerAdapter() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            listener.onChatRoomLoadFailed();
                            return;
                        }

                        final ChatRoom chatRoom = getChatRoomFromSnapshot(dataSnapshot);
                        chatRoom.setKey(key);
                        loadExtraDataForChatRoom(chatRoom, key, listener);
                    }
                });
    }

    private void loadExtraDataForChatRoom(final ChatRoom chatRoom, final String key, final ChatRoomInterface.OnChatRoomLoadedListener listener) {
        loadChatRoomMembersWithData(key, new ChatRoomInterface.OnChatMembersLoadedListener() {
            @Override
            public void onChatMembersLoaded(final ChatMembers chatMembers) {
                listener.onChatRoomLoaded(chatRoom, chatMembers);
            }
        });
    }

    public ChatRoom getChatRoomFromSnapshot(DataSnapshot dataSnapshot) {
        Map<String, String> value = (Map<String, String>) dataSnapshot.getValue();

        final ChatRoom chatRoom;
        if (ChatRoom.Type.valueOf(value.get("type")) == ChatRoom.Type.Group) {
            chatRoom = dataSnapshot.getValue(GroupChatRoom.class);
        } else {
            chatRoom = dataSnapshot.getValue(IndividualChatRoom.class);
        }
        chatRoom.setKey(dataSnapshot.getKey());
        return chatRoom;
    }

    public ValueEventListener loadLastChatMessage(final ChatRoomHolder holder
            , final ChatRoomInterface.OnChatLastMessageLoadedListener onChatLastMessageLoadedListener) {
        Query query = getDefaultRoot().child(messagesPath).child(holder.chatRoom.getKey()).orderByKey().limitToLast(1);
        return query.addValueEventListener(new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);

                Log.d(TAG, "onDataChange() called with: " + "dataSnapshot = [" + dataSnapshot + "]");
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    ChatMessage lastChatMessage = dataSnapshot.getChildren().iterator().next().getValue(ChatMessage.class);
                    if (lastChatMessage != null) {
                        lastChatMessage.setKey(dataSnapshot.getKey());
                        int indexOfUser = holder.members.getUsers().indexOf(lastChatMessage.getUser());
                        if (indexOfUser >= 0) {
                            lastChatMessage.setUser(holder.members.getUsers().get(indexOfUser));
                        }
                        holder.lastMessage = lastChatMessage;
                    }
                    onChatLastMessageLoadedListener.onChatLastMessageLoaded(lastChatMessage);
                } else {
                    onChatLastMessageLoadedListener.onChatLastMessageLoadFailed();
                }
            }
        });
    }

    public void loadChatRoomMembers(String key, final ChatRoomInterface.OnChatMembersLoadedListener listener) {
        getDefaultRoot().child(membersPath).child(key)
                .addListenerForSingleValueEvent(new ValueEventListenerAdapter() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ChatMembers chatMembers = getChatMembersFromSnapshot(dataSnapshot);
                        listener.onChatMembersLoaded(chatMembers);
                    }
                });
    }

    public void loadChatRoomMembersWithData(String key, final ChatRoomInterface.OnChatMembersLoadedListener listener) {
        getDefaultRoot().child(membersPath).child(key)
                .addListenerForSingleValueEvent(new ValueEventListenerAdapter() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ChatMembers chatMembers = getChatMembersFromSnapshot(dataSnapshot);
                        loadUsersFromChatRooms(chatMembers, listener);
                    }
                });
    }

    @NonNull
    private ChatMembers getChatMembersFromSnapshot(DataSnapshot dataSnapshot) {
        List<User> users = new ArrayList<>();

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            User user = new User();
            user.setKey(snapshot.getKey());

            users.add(user);
        }

        ChatMembers chatMembers = new ChatMembers();
        chatMembers.setUsers(users);
        return chatMembers;
    }

    private void loadUsersFromChatRooms(final ChatMembers chatMembers, final ChatRoomInterface.OnChatMembersLoadedListener listener) {
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

    public void updateGroupChatRoom(final GroupChatRoom groupChatRoom) {
        getDefaultRoot().child(chatRoomPath).child(groupChatRoom.getKey()).setValue(groupChatRoom);
    }

    public void saveGroupChatRoom(final Context context, final User administrator, final GroupChatRoom groupChatRoom
            , final List<User> members, final ChatRoomInterface.OnChatRoomSavedListener onChatRoomSavedListener) {
        User administratorWithKey = new User();
        administratorWithKey.setKey(administrator.getKey());
        groupChatRoom.setAdministrator(administrator);

        getDefaultRoot().child(chatRoomPath).push().setValue(groupChatRoom
                , new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Log.i(TAG, "onComplete firebaseError: " + firebaseError);
                if (firebaseError == null) {
                    members.add(administrator);
                    for (User member : members) {
                        addChatMember(context, member, firebase.getKey());
                    }

                    groupChatRoom.setKey(firebase.getKey());
                    groupChatRoom.setAdministrator(administrator);

                    ChatMembers chatMembers = new ChatMembers();
                    chatMembers.setUsers(members);

                    onChatRoomSavedListener.onChatRoomSaved(groupChatRoom, chatMembers);
                }
            }
        });
    }

    public void saveIndividualChatRoom(final Context context, final User me, final User friend
            , final ChatRoomInterface.OnChatRoomSavedListener onChatRoomSavedListener) {
        final IndividualChatRoom chatRoom = new IndividualChatRoom();
        chatRoom.setCreatedDate(new Date());

        getDefaultRoot().child(chatRoomPath).push().setValue(chatRoom
                , new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {
                    addChatMember(context, me, firebase.getKey());
                    addChatMember(context, friend, firebase.getKey());

                    chatRoom.setKey(firebase.getKey());

                    ChatMembers chatMembers = createChatMembers(me, friend);
                    onChatRoomSavedListener.onChatRoomSaved(chatRoom, chatMembers);
                }
            }
        });
    }

    @NonNull
    private ChatMembers createChatMembers(User me, User friend) {
        ChatMembers chatMembers = new ChatMembers();
        List<User> users = new ArrayList<>();
        users.add(me);
        users.add(friend);
        chatMembers.setUsers(users);
        return chatMembers;
    }

    public void removeChatMember(Context context, User user, String chatRoomKey) {
        UserServices userServices = new UserServices();
        userServices.removeUserChatRoom(user.getKey(), chatRoomKey);

        GcmTopicManager gcmTopicManager = new GcmTopicManager(context);
        gcmTopicManager.unregisterToChatRoomTopic(user, chatRoomKey);

        getRootByCode(user.getCountryProgram()).child(membersPath)
                .child(chatRoomKey)
                .child(user.getKey())
                .removeValue();
    }

    public void addChatMember(Context context, User user, String chatRoomKey) {
        UserServices userServices = new UserServices();
        userServices.addUserChatRoom(user.getKey(), chatRoomKey);

        GcmTopicManager gcmTopicManager = new GcmTopicManager(context);
        gcmTopicManager.registerToChatRoomTopic(user, chatRoomKey);

        getRootByCode(user.getCountryProgram()).child(membersPath)
                .child(chatRoomKey)
                .child(user.getKey())
                .setValue(true);
    }
}
