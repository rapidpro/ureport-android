package in.ureport.tasks;

import android.content.Context;
import android.util.Log;

import in.ureport.R;
import in.ureport.flowrunner.models.FlowAction;
import in.ureport.flowrunner.models.FlowStep;
import in.ureport.flowrunner.models.FlowStepSet;
import in.ureport.helpers.ContactBuilder;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.FlowManager;
import in.ureport.managers.UserManager;
import in.ureport.models.CountryProgram;
import in.ureport.network.RapidProServices;
import in.ureport.tasks.common.ProgressTask;

/**
 * Created by johncordeiro on 16/10/15.
 */
public class SendFlowReponsesTask extends ProgressTask<FlowStepSet, Void, Boolean> {

    private static final String TAG = "SendFlowStepSet";
    private static final int DELAY_BETWEEN_RESPONSES = 1000;

    public SendFlowReponsesTask(Context context) {
        super(context, R.string.message_send_poll);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        FlowManager.disableFlowNotification();
    }

    @Override
    protected Boolean doInBackground(FlowStepSet... params) {
        try {
            FlowStepSet flowStepSet = params[0];
            CountryProgram countryProgram = CountryProgramManager.getCurrentCountryProgram();
            ContactBuilder contactBuilder = new ContactBuilder();

            RapidProServices services = new RapidProServices();
            for (FlowStep flowStep : flowStepSet.getSteps()) {
                services.sendReceivedMessage(UserManager.getCountryToken()
                        , getContext().getString(countryProgram.getChannel())
                        , contactBuilder.formatUserId(UserManager.getUserId())
                        , getMessageFromStep(flowStep));
                Thread.sleep(DELAY_BETWEEN_RESPONSES);
            }
            return true;
        } catch(Exception exception) {
            Log.e(TAG, "instance initializer ", exception);
        }
        return false;
    }

    private String getMessageFromStep(FlowStep flowStep) {
        FlowAction flowAction = flowStep.getActions().get(0);
        return flowAction.getMessage().values().iterator().next();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        FlowManager.enableFlowNotificationAfterNext();
    }
}
