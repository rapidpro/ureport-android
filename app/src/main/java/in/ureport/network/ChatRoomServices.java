package in.ureport.network;

import android.content.Context;
import android.support.annotation.NonNull;

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
import in.ureport.listener.OnChatRoomSavedListener;
import in.ureport.listener.OnChatRoomLoadedListener;
import in.ureport.managers.GcmTopicManager;
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
public class ChatRoomServices extends ProgramServices {

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

    public void closeChatRoom(Context context, ChatRoom chatRoom, ChatMembers chatMembers) {
        for (User user : chatMembers.getUsers()) {
            removeChatMember(context, user, chatRoom.getKey());
        }
        getDefaultRoot().child(membersPath).child(chatRoom.getKey()).removeValue();
        getDefaultRoot().child(chatRoomPath).child(chatRoom.getKey()).removeValue();
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
            user.setNickname(chatMessage.getUser().getNickname());
            chatMessage.setUser(user);
        }
    }

    public void getChatRoom(final String key, final OnChatRoomLoadedListener listener) {
        getDefaultRoot().child(chatRoomPath).child(key).addListenerForSingleValueEvent(
                new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) return;

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
        getDefaultRoot().child(messagesPath).child(key).orderByKey().limitToLast(1)
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

    public void loadChatRoomMembers(String key, final OnChatMembersLoadedListener listener) {
        getDefaultRoot().child(membersPath).child(key)
                .addListenerForSingleValueEvent(new ValueEventListenerAdapter() {
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

    public void updateGroupChatRoom(final GroupChatRoom groupChatRoom) {
        getDefaultRoot().child(chatRoomPath).child(groupChatRoom.getKey()).setValue(groupChatRoom);
    }

    public void saveGroupChatRoom(final Context context, final User administrator, final GroupChatRoom groupChatRoom
            , final List<User> members, final OnChatRoomSavedListener onChatRoomSavedListener) {
        User administratorWithKey = new User();
        administratorWithKey.setKey(administrator.getKey());
        groupChatRoom.setAdministrator(administrator);

        getDefaultRoot().child(chatRoomPath).push().setValue(groupChatRoom
                , new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
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
            , final OnChatRoomSavedListener onChatRoomSavedListener) {
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
        gcmTopicManager.unregisterUserTopic(user, chatRoomKey);

        getDefaultRoot().child(membersPath)
                .child(chatRoomKey)
                .child(user.getKey())
                .removeValue();
    }

    public void addChatMember(Context context, User user, String chatRoomKey) {
        UserServices userServices = new UserServices();
        userServices.addUserChatRoom(user.getKey(), chatRoomKey);

        GcmTopicManager gcmTopicManager = new GcmTopicManager(context);
        gcmTopicManager.registerUserTopic(user, chatRoomKey);

        getDefaultRoot().child(membersPath)
                .child(chatRoomKey)
                .child(user.getKey())
                .setValue(true);
    }
}
