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
import in.ureport.models.rapidpro.Contact;
import in.ureport.network.RapidProServices;

/**
 * Created by johncordeiro on 13/10/15.
 */
public class LastFlowLoader extends AsyncTaskLoader<FlowDefinition> {

    private static final String TAG = "LastFlowLoader";

    private static final int EARLY_MONTHS = 1;

    private final RapidProServices rapidProServices;

    public LastFlowLoader(Context context) {
        super(context);
        this.rapidProServices = new RapidProServices();
    }

    @Override
    public FlowDefinition loadInBackground() {
        try {
            loadUserUuidIfNeeded();
            List<FlowRun> flowRuns = rapidProServices.loadRuns(getApiToken(), UserManager.getUserRapidUuid(), getMinimumDate());

            FlowRun lastFlowRun = flowRuns.get(0);
            if(FlowRunnerManager.isFlowActive(lastFlowRun)) {
                return rapidProServices.loadFlowDefinition(getApiToken(), lastFlowRun.getFlowUuid());
            }
        } catch(Exception exception) {
            Log.e(TAG, "loadInBackground ", exception);
        }
        return null;
    }

    @NonNull
    private Date getMinimumDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.MONTH, -EARLY_MONTHS);
        return calendar.getTime();
    }

    private void loadUserUuidIfNeeded() {
        if(!UserManager.isUserRapidUuidValid()) {
            ContactBuilder contactBuilder = new ContactBuilder();

            Contact contact = rapidProServices.loadContact(getApiToken(), contactBuilder.formatUrn(UserManager.getUserId()));
            UserManager.updateUserRapidUuid(contact.getUuid());
        }
    }

    @NonNull
    private String getApiToken() {
        return getContext().getString(CountryProgramManager.getCurrentCountryProgram().getApiToken());
    }

}
