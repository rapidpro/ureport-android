package in.ureport.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.db.business.UserBusiness;
import in.ureport.db.repository.UserRepository;
import in.ureport.models.ChatGroup;
import in.ureport.models.User;

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

        ChatGroup chatGroup1 = new ChatGroup();
        chatGroup1.setTitle(getContext().getString(R.string.chatgroup1_title));
        chatGroup1.setDescription(getContext().getString(R.string.chatgroup1_description));

        ChatGroup chatGroup2 = new ChatGroup();
        chatGroup2.setTitle(getContext().getString(R.string.chatgroup2_title));
        chatGroup2.setDescription(getContext().getString(R.string.chatgroup2_description));

        ChatGroup chatGroup3 = new ChatGroup();
        chatGroup3.setTitle(getContext().getString(R.string.chatgroup3_title));
        chatGroup3.setDescription(getContext().getString(R.string.chatgroup3_description));

        chatGroups.add(chatGroup1);
        chatGroups.add(chatGroup2);
        chatGroups.add(chatGroup3);

        return chatGroups;
    }
}
