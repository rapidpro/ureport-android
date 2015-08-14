package in.ureport.models.converters;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMarshaller;

import in.ureport.models.User;

/**
 * Created by johncordeiro on 13/08/15.
 */
public class UserTypeConverter implements DynamoDBMarshaller<User> {

    @Override
    public String marshall(User getterReturnResult) {
        return getterReturnResult.getIdentityId();
    }

    @Override
    public User unmarshall(Class<User> clazz, String obj) {
        User user = new User();
        user.setIdentityId(obj);

        return user;
    }
}
