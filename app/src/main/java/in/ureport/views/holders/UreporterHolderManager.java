package in.ureport.views.holders;

import java.util.Set;

import in.ureport.listener.ItemSelectionListener;
import in.ureport.listener.OnCreateIndividualChatListener;
import in.ureport.models.User;

/**
 * Created by John Cordeiro on 4/19/17.
 * Copyright © 2017 Soloshot, Inc. All rights reserved.
 */

public interface UreporterHolderManager {

    ItemSelectionListener<User> getItemSelectionListener();

    OnCreateIndividualChatListener getOnCreateIndividualChatListener();

    User getUser(int position);

    Set<User> getSelectedUreporters();

    boolean isSelectionEnabled();

    Integer getMaxSelectionCount();

}
