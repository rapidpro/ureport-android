package in.ureport.listener;

import in.ureport.models.rapidpro.Message;

/**
 * Created by johncordeiro on 01/09/15.
 */
public interface OnRapidproLastMessageLoadedListener {

    void onRapidproLastMessageLoaded(Message lastMessage);

}
