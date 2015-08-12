package in.ureport.tasks;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

/**
 * Created by johncordeiro on 12/08/15.
 */
public class GetGoogleAuthTokenTask extends AsyncTask<GoogleApiClient, Void, String> {

    private static final String SERVER_CLIENT_ID = "843441539089-0ming63m3gq1cnptosjkis3h1vb8kvup.apps.googleusercontent.com";

    private static final String TAG = "GetGoogleAuthTokenTask";

    private Context context;

    public GetGoogleAuthTokenTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(GoogleApiClient... clients) {
        if(clients.length == 0) return null;

        Account account = new Account(Plus.AccountApi.getAccountName(clients[0]), GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        String scopes = "audience:server:client_id:" + SERVER_CLIENT_ID;

        try {
            return GoogleAuthUtil.getToken(context, account, scopes);
        } catch(Exception exception) {
            return null;
        }
    }
}
