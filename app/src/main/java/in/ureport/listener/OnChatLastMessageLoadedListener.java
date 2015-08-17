package in.ureport.listener;

import in.ureport.models.ChatMessage;

/**
 * Created by johncordeiro on 16/08/15.
 */
public interface OnChatLastMessageLoadedListener {

    void onChatLastMessageLoaded(ChatMessage chatMessage);

}
