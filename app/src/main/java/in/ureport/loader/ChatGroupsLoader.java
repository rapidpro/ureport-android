package in.ureport.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.ureport.R;
import in.ureport.models.ChatGroup;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class ChatGroupsLoader extends AsyncTaskLoader<List<ChatGroup>> {

    public ChatGroupsLoader(Context context) {
        super(context);
    }

    @Override
    public List<ChatGroup> loadInBackground() {
        List<ChatGroup> chatGroups = new ArrayList<>();

        Calendar date = Calendar.getInstance();
        date.roll(Calendar.DATE, -22);

        ChatGroup chatGroup1 = new ChatGroup();
        chatGroup1.setTitle(getContext().getString(R.string.chatgroup1_title));
        chatGroup1.setDescription(getContext().getString(R.string.chatgroup1_description));
        chatGroup1.setPicture(getContext().getResources().getResourceEntryName(R.drawable.water));
        chatGroup1.setCreationDate(date.getTime());

        ChatGroup chatGroup2 = new ChatGroup();
        chatGroup2.setTitle(getContext().getString(R.string.chatgroup2_title));
        chatGroup2.setDescription(getContext().getString(R.string.chatgroup2_description));
        chatGroup2.setPicture(getContext().getResources().getResourceEntryName(R.drawable.story2));
        chatGroup2.setCreationDate(date.getTime());

        ChatGroup chatGroup3 = new ChatGroup();
        chatGroup3.setTitle(getContext().getString(R.string.chatgroup3_title));
        chatGroup3.setDescription(getContext().getString(R.string.chatgroup3_description));
        chatGroup3.setPicture(getContext().getResources().getResourceEntryName(R.drawable.story2));
        chatGroup3.setCreationDate(date.getTime());

        chatGroups.add(chatGroup1);
        chatGroups.add(chatGroup2);
        chatGroups.add(chatGroup3);

        return chatGroups;
    }
}
