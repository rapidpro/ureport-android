package in.ureport.managers;

/**
 * Created by johncordeiro on 09/11/15.
 */
public class FlowManager {

    private static Boolean flowNotification = true;
    private static Boolean flowNotificationAfterNext = false;

    public static void enableFlowNotificationAfterNext() {
        enableFlowNotificiation();
        flowNotificationAfterNext = true;
    }

    public static void enableFlowNotificiation() {
        flowNotificationAfterNext = false;
        flowNotification = true;
    }

    public static void disableFlowNotification() {
        flowNotification = false;
    }

    public static Boolean canShowNextNotification() {
        if(flowNotificationAfterNext) {
            flowNotificationAfterNext = false;
            return false;
        }
        return flowNotification;
    }

}
