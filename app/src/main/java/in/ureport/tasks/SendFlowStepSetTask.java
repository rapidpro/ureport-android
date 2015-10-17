package in.ureport.tasks;

import android.content.Context;
import android.util.Log;

import in.ureport.R;
import in.ureport.flowrunner.models.FlowStepSet;
import in.ureport.managers.CountryProgramManager;
import in.ureport.models.CountryProgram;
import in.ureport.network.RapidProServices;
import in.ureport.tasks.common.ProgressTask;

/**
 * Created by johncordeiro on 16/10/15.
 */
public class SendFlowStepSetTask extends ProgressTask<FlowStepSet, Void, Boolean> {

    private static final String TAG = "SendFlowStepSet";

    public SendFlowStepSetTask(Context context) {
        super(context, R.string.message_send_poll);
    }

    @Override
    protected Boolean doInBackground(FlowStepSet... params) {
        try {
            CountryProgram countryProgram = CountryProgramManager.getCurrentCountryProgram();

            FlowStepSet flowStepSet = params[0];

            RapidProServices services = new RapidProServices();
            services.saveFlowStepSet(context.getString(countryProgram.getApiToken()), flowStepSet);

            return true;
        } catch(Exception exception) {
            Log.e(TAG, "instance initializer ", exception);
        }
        return false;
    }
}
