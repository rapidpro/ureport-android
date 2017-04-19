package in.ureport.views.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import in.ureport.R;
import in.ureport.helpers.ImageLoader;
import in.ureport.models.User;

/**
 * Created by John Cordeiro on 4/19/17.
 * Copyright © 2017 Soloshot, Inc. All rights reserved.
 */

public class UreporterViewHolder extends RecyclerView.ViewHolder {

    private final TextView name;
    private final ImageView picture;
    private final CheckBox selected;
    private final UreporterHolderManager ureporterHolderManager;

    public UreporterViewHolder(View itemView, UreporterHolderManager ureporterHolderManager) {
        super(itemView);

        name = (TextView) itemView.findViewById(R.id.name);
        picture = (ImageView) itemView.findViewById(R.id.picture);
        selected = (CheckBox) itemView.findViewById(R.id.selected);
        this.ureporterHolderManager = ureporterHolderManager;
        itemView.setOnClickListener(ureporterHolderManager.isSelectionEnabled() ?
                null : onItemClickListener);
        selected.setOnCheckedChangeListener(ureporterHolderManager.isSelectionEnabled() ?
                onUserCheckedListener : null);
        selected.setOnClickListener(ureporterHolderManager.isSelectionEnabled() ?
                onUserSelectedListener : null);
    }

    public void bindView(User user) {
        name.setText(user.getNickname());
        ImageLoader.loadPersonPictureToImageView(picture, user.getPicture());

        selected.setVisibility(ureporterHolderManager.isSelectionEnabled() ? View.VISIBLE : View.GONE);
        if(ureporterHolderManager.isSelectionEnabled())
            selected.setChecked(ureporterHolderManager.getSelectedUreporters().contains(user));
    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(ureporterHolderManager.getOnCreateIndividualChatListener() != null)
                ureporterHolderManager.getOnCreateIndividualChatListener()
                        .onCreateIndividualChat(ureporterHolderManager.getUser(getLayoutPosition()));
        }
    };

    private View.OnClickListener onUserSelectedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(ureporterHolderManager.getItemSelectionListener() == null) return;

            User user = ureporterHolderManager.getUser(getLayoutPosition());
            if(selected.isChecked()) {
                ureporterHolderManager.getItemSelectionListener().onItemSelected(user);
            } else {
                ureporterHolderManager.getItemSelectionListener().onItemDeselected(user);
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener onUserCheckedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            User user = ureporterHolderManager.getUser(getLayoutPosition());
            Integer maxSelectionCount = ureporterHolderManager.getMaxSelectionCount();
            Set<User> selectedUreporters = ureporterHolderManager.getSelectedUreporters();

            if(isChecked) {
                if(maxSelectionCount == null || selectedUreporters.size() <= maxSelectionCount)
                    selectedUreporters.add(user);
                else
                    showMaximumNumberLimitError();
            } else {
                selectedUreporters.remove(user);
            }
        }
    };

    private void showMaximumNumberLimitError() {
        Integer maxSelectionCount = ureporterHolderManager.getMaxSelectionCount();

        Toast.makeText(itemView.getContext()
                , itemView.getContext().getString(R.string.ureporters_selected_maximum, maxSelectionCount)
                , Toast.LENGTH_LONG).show();
    }
}
