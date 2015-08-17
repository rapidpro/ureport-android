package in.ureport.tasks;

import android.os.AsyncTask;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

/**
 * Created by johncordeiro on 12/08/15.
 */
public class GetGoogleAuthTokenTask extends AsyncTask<GoogleApiClient, Void, String> {

    @Override
    protected String doInBackground(GoogleApiClient... clients) {
        if(clients.length == 0) return null;

        GoogleApiClient client = clients[0];
        String scopes = String.format("oauth2:%s", new Scope(Scopes.PROFILE));

        try {
            return GoogleAuthUtil.getToken(client.getContext(), Plus.AccountApi.getAccountName(client), scopes);
        } catch(Exception exception) {
            return null;
        }
    }
}
