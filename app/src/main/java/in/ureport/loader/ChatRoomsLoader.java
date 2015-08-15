package in.ureport.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.ureport.R;
import in.ureport.db.business.UserBusiness;
import in.ureport.db.repository.UserRepository;
import in.ureport.managers.UserManager;
import in.ureport.models.ChatGroup;
import in.ureport.models.ChatRoom;
import in.ureport.models.GroupChatRoom;
import in.ureport.models.IndividualChatRoom;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class ChatRoomsLoader extends AsyncTaskLoader<List<ChatRoom>> {

    public ChatRoomsLoader(Context context) {
        super(context);
    }

    @Override
    public List<ChatRoom> loadInBackground() {
        List<ChatRoom> chatRooms = new ArrayList<>();
        List<User> users = UserManager.getFakeUsers(getContext());

        Calendar date1 = Calendar.getInstance();
        date1.roll(Calendar.HOUR, -1);

        IndividualChatRoom individualChatRoom1 = new IndividualChatRoom();
        individualChatRoom1.setLastMessage(getContext().getString(R.string.chat1_lastMessage));
        individualChatRoom1.setLastMessageDate(date1.getTime());
        individualChatRoom1.setUnreadMessages(12);
        individualChatRoom1.setFriend(users.get(3));

        Calendar date2 = Calendar.getInstance();
        date2.roll(Calendar.HOUR, -2);
        date2.roll(Calendar.MINUTE, -30);

        Calendar groupCreationDate = Calendar.getInstance();
        groupCreationDate.roll(Calendar.DATE, -28);

        ChatGroup chatGroup1 = new ChatGroup();
        chatGroup1.setTitle(getContext().getString(R.string.chatgroup4_title));
        chatGroup1.setDescription(getContext().getString(R.string.chatgroup4_description));
        chatGroup1.setCreationDate(groupCreationDate.getTime());
        chatGroup1.setPicture(getContext().getResources().getResourceEntryName(R.drawable.kampala));

        GroupChatRoom groupChatRoom1 = new GroupChatRoom();
        groupChatRoom1.setLastMessage(getContext().getString(R.string.chat2_lastMessage));
        groupChatRoom1.setLastMessageDate(date2.getTime());
        groupChatRoom1.setUnreadMessages(2);
        groupChatRoom1.setParticipants(users.subList(0, 4));
        groupChatRoom1.setChatGroup(chatGroup1);

        ChatGroup chatGroup2 = new ChatGroup();
        chatGroup2.setTitle(getContext().getString(R.string.chatgroup5_title));
        chatGroup2.setDescription(getContext().getString(R.string.chatgroup5_description));
        chatGroup2.setCreationDate(groupCreationDate.getTime());
        chatGroup2.setPicture(getContext().getResources().getResourceEntryName(R.drawable.water));

        GroupChatRoom groupChatRoom2 = new GroupChatRoom();
        groupChatRoom2.setLastMessageDate(date2.getTime());
        groupChatRoom2.setParticipants(users.subList(3, 9));
        groupChatRoom2.setChatGroup(chatGroup2);

        Calendar date3 = Calendar.getInstance();
        date3.roll(Calendar.HOUR, -6);
        date3.roll(Calendar.MINUTE, -36);

        IndividualChatRoom individualChatRoom2 = new IndividualChatRoom();
        individualChatRoom2.setLastMessageDate(date1.getTime());
        individualChatRoom2.setFriend(users.get(5));

        chatRooms.add(individualChatRoom1);
        chatRooms.add(groupChatRoom1);
        chatRooms.add(groupChatRoom2);
        chatRooms.add(individualChatRoom2);

        return chatRooms;
    }
}
