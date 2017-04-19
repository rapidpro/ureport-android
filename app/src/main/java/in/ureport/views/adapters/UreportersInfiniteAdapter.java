package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.marcorei.infinitefire.InfiniteFireArray;
import com.marcorei.infinitefire.InfiniteFireRecyclerViewAdapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.ureport.R;
import in.ureport.listener.ItemSelectionListener;
import in.ureport.listener.OnCreateIndividualChatListener;
import in.ureport.models.User;
import in.ureport.views.holders.UreporterHolderManager;
import in.ureport.views.holders.UreporterViewHolder;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class UreportersInfiniteAdapter extends InfiniteFireRecyclerViewAdapter<User> implements UreporterHolderManager {

    private Set<User> selectedUreporters;

    private Boolean selectionEnabled = false;
    private Integer maxSelectionCount;

    private ItemSelectionListener<User> itemSelectionListener;

    public UreportersInfiniteAdapter(InfiniteFireArray<User> snapshots) {
        super(snapshots, 0, 0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new UreporterViewHolder(inflater.inflate(R.layout.item_ureporter, parent, false), this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((UreporterViewHolder)holder).bindView(getUser(position));
    }

    public void setSelectionEnabled(Boolean selectionEnabled, Integer maxSelectionCount, List<User> users) {
        this.maxSelectionCount = maxSelectionCount;
        this.selectionEnabled = selectionEnabled;

        if(users != null)
            this.selectedUreporters = new HashSet<>(users);
        else
            this.selectedUreporters = new HashSet<>();
    }

    public void setItemSelectionListener(ItemSelectionListener<User> itemSelectionListener) {
        this.itemSelectionListener = itemSelectionListener;
    }

    @Override
    public User getUser(int position) {
        return getItem(position).getValue();
    }

    @Override
    public ItemSelectionListener<User> getItemSelectionListener() {
        return itemSelectionListener;
    }

    @Override
    public OnCreateIndividualChatListener getOnCreateIndividualChatListener() {
        return null;
    }

    @Override
    public Set<User> getSelectedUreporters() {
        return selectedUreporters;
    }

    @Override
    public boolean isSelectionEnabled() {
        return selectionEnabled;
    }

    @Override
    public Integer getMaxSelectionCount() {
        return maxSelectionCount;
    }
}
