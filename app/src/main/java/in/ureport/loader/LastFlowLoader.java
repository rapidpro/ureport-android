package in.ureport.loader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.ureport.flowrunner.managers.FlowRunnerManager;
import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.FlowRun;
import in.ureport.helpers.ContactBuilder;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.UserManager;
import in.ureport.flowrunner.models.Contact;
import in.ureport.network.ProxyApi;
import in.ureport.network.ProxyServices;
import in.ureport.network.RapidProServices;

/**
 * Created by johncordeiro on 13/10/15.
 */
public class LastFlowLoader extends AsyncTaskLoader<FlowDefinition> {

    private static final String TAG = "LastFlowLoader";

    private static final int EARLY_MONTHS = 1;

    private RapidProServices rapidProServices;

    public LastFlowLoader(Context context) {
        super(context);
    }

    @Override
    public FlowDefinition loadInBackground() {
        try {
            loadCountryTokenIfNeeded();
            String rapidproEndpoint = getContext().getString(CountryProgramManager
                    .getCurrentCountryProgram().getRapidproEndpoint());
            this.rapidProServices = new RapidProServices(rapidproEndpoint);

            Contact contact = loadContact();
            List<FlowRun> flowRuns = rapidProServices.loadRuns(getApiToken(), UserManager.getUserRapidUuid(), getMinimumDate());

            FlowRun lastFlowRun = flowRuns.get(0);
            FlowDefinition flowDefinition = rapidProServices.loadFlowDefinition(getApiToken(), lastFlowRun.getFlowUuid());
            flowDefinition.setContact(contact);
            flowDefinition.setFlowRun(lastFlowRun);

            if(!FlowRunnerManager.isFlowExpired(flowDefinition))
                return flowDefinition;
        } catch(Exception exception) {
            Log.e(TAG, "loadInBackground ", exception);
        }
        return null;
    }

    @Override
    protected void onStartLoading() {
        if (takeContentChanged())
            forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    private void loadCountryTokenIfNeeded() {
        if(!UserManager.isCountryTokenValid()) {
            ProxyServices proxyServices = new ProxyServices(getContext());
            ProxyApi.Response response = proxyServices.getAuthenticationTokenByCountry(UserManager.getCountryCode());
            UserManager.updateCountryToken(response.token);
        }
    }

    @NonNull
    private Date getMinimumDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -EARLY_MONTHS);
        return calendar.getTime();
    }

    private Contact loadContact() {
        ContactBuilder contactBuilder = new ContactBuilder();
        Contact contact = rapidProServices.loadContact(getApiToken(), contactBuilder.formatExtUrn(UserManager.getUserId()));

        if(!UserManager.isUserRapidUuidValid())
            UserManager.updateUserRapidUuid(contact.getUuid());

        return contact;
    }

    @NonNull
    private String getApiToken() {
        return UserManager.getCountryToken();
    }

}
