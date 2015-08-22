package in.ureport.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import in.ureport.models.ChatMessage;
import in.ureport.models.ChatRoom;
import in.ureport.network.GcmServices;

/**
 * Created by johncordeiro on 21/08/15.
 */
public class SendGcmChatTask extends AsyncTask<ChatMessage, Void, Void> {

    private static final String TAG = "SendGcmChatTask";

    private Context context;
    private ChatRoom chatRoom;

    public SendGcmChatTask(Context context, ChatRoom chatRoom) {
        this.context = context;
        this.chatRoom = chatRoom;
    }

    @Override
    protected Void doInBackground(ChatMessage... params) {
        try {
            GcmServices gcmServices = new GcmServices(context);
            gcmServices.sendChatMessage(chatRoom, params[0]);
        } catch(Exception exception) {
            Log.e(TAG, "doInBackground ", exception);
        }
        return null;
    }

}
