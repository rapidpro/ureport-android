package in.ureport.db.repository;

import java.util.List;

import br.com.ilhasoft.support.db.repository.AbstractRepository;
import in.ureport.models.db.ChatNotification;

/**
 * Created by johncordeiro on 21/08/15.
 */
public interface ChatNotificationRepository extends AbstractRepository<ChatNotification> {

    List<ChatNotification> getAllOrderedByDate();

    List<ChatNotification> deleteByChatRoomId(String chatRoomId);

}
