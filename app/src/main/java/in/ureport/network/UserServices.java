package in.ureport.network;

import java.util.List;
import java.util.Map;

import in.ureport.managers.DynamoDBManager;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 14/08/15.
 */
public class UserServices {

    public List<User> loadUsers(List<Object> users) {
        Map<String, List<Object>> itemsLoaded = DynamoDBManager.getMapper().batchLoad(users);
        return (List)itemsLoaded.get("User");
    }

}
