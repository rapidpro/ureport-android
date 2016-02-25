package in.ureport.db.business;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import in.ureport.db.repository.ChatNotificationRepository;
import in.ureport.models.db.ChatNotification;

/**
 * Created by johncordeiro on 21/08/15.
 */
public class ChatNotificationBusiness extends AbstractBusiness<ChatNotification> implements ChatNotificationRepository {

    public ChatNotificationBusiness() {
        super(ChatNotification.class);
    }

    @Override
    public List<ChatNotification> getAllOrderedByDate() {
        return new Select().from(getTypeClass())
                .orderBy("date DESC")
                .execute();
    }

    @Override
    public List<ChatNotification> deleteByChatRoomId(String chatRoomId) {
        return new Delete().from(getTypeClass())
                .where("chatRoomId=?", chatRoomId)
                .execute();
    }
}
