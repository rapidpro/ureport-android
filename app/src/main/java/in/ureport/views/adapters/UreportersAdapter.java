package in.ureport.views.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
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
public class UreportersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        UreporterHolderManager {

    private List<User> ureportersList;
    private List<User> searchUreportersList;
    private Set<User> selectedUreporters;

    private Boolean selectionEnabled = false;
    private Boolean searchEnabled = false;
    private Integer maxSelectionCount;

    private OnCreateIndividualChatListener onCreateIndividualChatListener;
    private ItemSelectionListener<User> itemSelectionListener;

    public UreportersAdapter(List<User> ureportersList) {
        this.ureportersList = ureportersList;
    }

    public UreportersAdapter() {
        this.ureportersList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new UreporterViewHolder(inflater.inflate(R.layout.item_ureporter, parent, false), this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((UreporterViewHolder)holder).bindView(getCurrentUreportersList().get(position));
    }

    private List<User> getCurrentUreportersList() {
        if(searchEnabled) {
            return searchUreportersList;
        }
        return ureportersList;
    }

    @Override
    public long getItemId(int position) {
        return getCurrentUreportersList().get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return getCurrentUreportersList().size();
    }

    public List<User> getUreportersList() {
        return ureportersList;
    }

    public void update(List<User> ureportersList) {
        this.ureportersList = ureportersList != null ? ureportersList : new ArrayList<User>();
        notifyDataSetChanged();
    }

    public void setOnCreateIndividualChatListener(OnCreateIndividualChatListener onCreateIndividualChatListener) {
        this.onCreateIndividualChatListener = onCreateIndividualChatListener;
    }

    public void setSelectionEnabled(Boolean selectionEnabled, Integer maxSelectionCount, List<User> users) {
        this.maxSelectionCount = maxSelectionCount;
        this.selectionEnabled = selectionEnabled;

        if(users != null)
            this.selectedUreporters = new HashSet<>(users);
        else
            this.selectedUreporters = new HashSet<>();
    }

    public void setSelectionEnabled(Boolean selectionEnabled, Integer maxSelectionCount) {
        setSelectionEnabled(selectionEnabled, maxSelectionCount, null);
    }

    public void setSelectionEnabled(Boolean selectionEnabled) {
        setSelectionEnabled(selectionEnabled, null, null);
    }

    public void setItemSelectionListener(ItemSelectionListener<User> itemSelectionListener) {
        this.itemSelectionListener = itemSelectionListener;
    }

    @Override
    public ItemSelectionListener<User> getItemSelectionListener() {
        return itemSelectionListener;
    }

    @Override
    public OnCreateIndividualChatListener getOnCreateIndividualChatListener() {
        return onCreateIndividualChatListener;
    }

    @Override
    public User getUser(int position) {
        return ureportersList.get(position);
    }

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

    public void search(String query) {
        searchEnabled = true;
        searchUreportersList = getUreportersByNickname(query);
        notifyDataSetChanged();
    }

    public void clearSearch() {
        searchEnabled = false;
        notifyDataSetChanged();
    }

    @NonNull
    private List<User> getUreportersByNickname(String query) {
        List<User> resultUreportersList = new ArrayList<>();

        List<User> ureportersList = getUreportersList();
        query = query.toLowerCase();
        for (User user : ureportersList) {
            if(user.getNickname().toLowerCase().contains(query)) {
                resultUreportersList.add(user);
            }
        }
        return resultUreportersList;
    }

}
