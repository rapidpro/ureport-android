package in.ureport.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import in.ureport.models.ChatMessage;
import in.ureport.models.ChatRoom;
import in.ureport.models.User;
import in.ureport.network.GcmServices;

/**
 * Created by johncordeiro on 21/08/15.
 */
public class SendGcmChatTask extends AsyncTask<ChatMessage, Void, Void> {

    private static final String TAG = "SendGcmChatTask";

    private Context context;

    private ChatRoom chatRoom;
    private User user;

    public SendGcmChatTask(Context context, ChatRoom chatRoom, User user) {
        this.context = context;
        this.chatRoom = chatRoom;
        this.user = user;
    }

    @Override
    protected Void doInBackground(ChatMessage... params) {
        try {
            ChatMessage chatMessage = params[0];
            chatMessage.setUser(user);

            GcmServices gcmServices = new GcmServices(context);
            gcmServices.sendChatMessage(chatRoom, chatMessage);
        } catch(Exception exception) {
            Log.e(TAG, "doInBackground ", exception);
        }
        return null;
    }

}
