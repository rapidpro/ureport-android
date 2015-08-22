package in.ureport.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.ureport.db.business.ChatNotificationBusiness;
import in.ureport.models.ChatRoom;
import in.ureport.models.db.ChatNotification;

/**
 * Created by johncordeiro on 22/08/15.
 */
public class GetUnreadMessagesTask extends AsyncTask<Void, Void, Map<ChatRoom, Integer>> {
    @Override
    protected Map<ChatRoom, Integer> doInBackground(Void... params) {
        Map<ChatRoom, Integer> map = new HashMap<>();

        List<ChatNotification> chatNotificationList = new ChatNotificationBusiness().getAllOrderedByDate();
        for (ChatNotification chatNotification : chatNotificationList) {
            ChatRoom chatRoom = new ChatRoom() {};
            chatRoom.setKey(chatNotification.getChatRoomId());
            map.put(chatRoom, incrementValue(map, chatRoom));
        }

        return map;
    }

    @NonNull
    private Integer incrementValue(Map<ChatRoom, Integer> map, ChatRoom chatRoom) {
        Integer value = map.get(chatRoom);
        return value != null ? value+1 : 1;
    }
}
