package in.ureport.tasks;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;

import in.ureport.managers.CognitoCredentialsLoginManager;
import in.ureport.managers.DynamoDBManager;
import in.ureport.models.User;
import in.ureport.tasks.common.ProgressTask;

/**
 * Created by johncordeiro on 13/08/15.
 */
public class GetDbUserTask extends ProgressTask<Void, Void, User> {

    private static final String TAG = "CheckUserConfirmation";

    public GetDbUserTask(Context context, @StringRes int text) {
        super(context, text);
    }

    @Override
    protected User doInBackground(Void... params) {
        try {
            CognitoCredentialsLoginManager.getCredentialsProvider().refresh();

            String identityId = CognitoCredentialsLoginManager.getCredentialsProvider().getIdentityId();

            User user = new User();
            user.setIdentityId(identityId);

            DynamoDBQueryExpression<User> queryExpression = new DynamoDBQueryExpression<>();
            queryExpression.withHashKeyValues(user);

            PaginatedQueryList<User> queryList = DynamoDBManager.getMapper().query(User.class, queryExpression);
            return queryList.size() > 0 ? queryList.get(0) : null;
        } catch(Exception exception) {
            setException(exception);
            Log.e(TAG, "doInBackground ", exception);
        }
        return null;
    }
}
