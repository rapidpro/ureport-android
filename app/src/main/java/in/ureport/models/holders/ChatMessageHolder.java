package in.ureport.models.holders;

import com.google.gson.annotations.Expose;

import in.ureport.models.ChatMessage;
import in.ureport.models.ChatRoom;
import in.ureport.models.gcm.NotificationHolder;

/**
 * Created by John Cordeiro on 5/1/17.
 * Copyright © 2017 Soloshot, Inc. All rights reserved.
 */

public class ChatMessageHolder extends NotificationHolder {

    @Expose
    private ChatRoom chatRoom;

    @Expose
    private ChatMessage chatMessage;

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public ChatMessageHolder setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
        return this;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public ChatMessageHolder setChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
        return this;
    }
}
