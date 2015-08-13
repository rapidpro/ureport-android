package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import in.ureport.R;
import in.ureport.listener.ItemSelectionListener;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/15/15.
 */
public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<User> users;
    private List<User> selectedCoauthors;

    private ItemSelectionListener<User> itemSelectionListener;

    public UserAdapter(List<User> users, List<User> selectedCoauthors) {
        this.users = users;
        this.selectedCoauthors = selectedCoauthors;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_user, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).bindView(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setItemSelectionListener(ItemSelectionListener<User> itemSelectionListener) {
        this.itemSelectionListener = itemSelectionListener;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private CheckBox check;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            check = (CheckBox) itemView.findViewById(R.id.check);
            check.setOnClickListener(onCheckClickListener);
        }

        private void bindView(User user) {
            check.setChecked(selectedCoauthors != null && selectedCoauthors.contains(user));
            name.setText(user.getNickname());
        }

        private View.OnClickListener onCheckClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemSelectionListener != null) {
                    User user = users.get(getLayoutPosition());
                    if (check.isChecked()) {
                        itemSelectionListener.onItemSelected(user);
                    } else {
                        itemSelectionListener.onItemDeselected(user);
                    }
                }
            }
        };
    }
}
