package in.ureport.tasks;

import android.content.Context;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;

import in.ureport.R;
import in.ureport.exception.UreportException;
import in.ureport.managers.CognitoCredentialsLoginManager;
import in.ureport.managers.DynamoDBManager;
import in.ureport.models.User;
import in.ureport.tasks.common.ProgressTask;

/**
 * Created by johncordeiro on 13/08/15.
 */
public class RegisterUserTask extends ProgressTask<User, Void, Boolean> {

    public RegisterUserTask(Context context) {
        super(context, R.string.user_registering_load_message);
    }

    @Override
    protected Boolean doInBackground(User... users) {
        try {
            User user = users[0];
            checkUserFields(user);

            String identityId = CognitoCredentialsLoginManager.getCredentialsProvider().getIdentityId();
            user.setIdentityId(identityId);
            DynamoDBManager.getMapper().save(user);
            return true;
        } catch(Exception exception) {
            setException(exception);
        }
        return false;
    }

    private void checkUserFields(User user) throws UreportException {
        PaginatedQueryList<User> usersWithNickname = queryNickname(user);
        if(!usersWithNickname.isEmpty()) {
            throw new UreportException(context.getString(R.string.error_nickname_already_exists));
        }

        PaginatedQueryList<User> usersWithEmail = queryEmail(user);
        if(!usersWithEmail.isEmpty()) {
            throw new UreportException(context.getString(R.string.error_email_already_exists));
        }
    }

    private PaginatedQueryList<User> queryNickname(User user) {
        User nicknameUser = new User();
        nicknameUser.setNickname(user.getNickname());

        return queryUser(nicknameUser);
    }

    private PaginatedQueryList<User> queryEmail(User user) {
        User emailUser = new User();
        emailUser.setEmail(user.getEmail());

        return queryUser(emailUser);
    }

    private PaginatedQueryList<User> queryUser(User emailUser) {
        DynamoDBQueryExpression<User> queryExpression = new DynamoDBQueryExpression<User>()
                .withHashKeyValues(emailUser)
                .withConsistentRead(false);

        return DynamoDBManager.getMapper().query(User.class, queryExpression);
    }
}
