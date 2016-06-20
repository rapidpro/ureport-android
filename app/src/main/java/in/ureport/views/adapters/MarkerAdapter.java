package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import in.ureport.R;
import in.ureport.listener.ItemSelectionListener;
import in.ureport.models.Marker;

/**
 * Created by johncordeiro on 7/15/15.
 */
public class MarkerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MarkerAdapter";

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_ITEM_ADD = 1;

    private static final long ADD_MEDIA_ITEM_ID = 1000;

    private List<Marker> markers;
    private List<Marker> selectedMarkers;

    private ItemSelectionListener<Marker> itemSelectionListener;

    public MarkerAdapter(List<Marker> markers, List<Marker> selectedMarkers) {
        this.markers = markers;
        this.selectedMarkers = selectedMarkers;
        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch(viewType) {
            case TYPE_ITEM_ADD:
                return new AddItemViewHolder(inflater.inflate(R.layout.item_add_marker, parent, false));
            default:
                return new ItemViewHolder(inflater.inflate(R.layout.item_marker, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == TYPE_ITEM) {
            ((ItemViewHolder)holder).bindView(markers.get(position));
        }
    }

    @Override
    public long getItemId(int position) {
        if(getItemViewType(position) == TYPE_ITEM_ADD) {
            return ADD_MEDIA_ITEM_ID;
        }
        String id = markers.get(position).getName() + position;
        return id.hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == markers.size()) {
            return TYPE_ITEM_ADD;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return markers.size() + 1;
    }

    private void addMarker(Marker marker) {
        if(!markers.contains(marker)) {
            selectedMarkers.add(marker);
            markers.add(marker);
            notifyDataSetChanged();
        }
    }

    public void setItemSelectionListener(ItemSelectionListener<Marker> itemSelectionListener) {
        this.itemSelectionListener = itemSelectionListener;
    }

    private class AddItemViewHolder extends RecyclerView.ViewHolder {

        private EditText name;

        public AddItemViewHolder(View itemView) {
            super(itemView);

            name = (EditText) itemView.findViewById(R.id.name);
            name.setOnEditorActionListener(onNameEditorActionListener);

            Button addMarker = (Button) itemView.findViewById(R.id.addMarker);
            addMarker.setOnClickListener(onMarkerClickListener);
        }

        private void addNewMarker() {
            String nameText = name.getText().toString();
            if(name.length() > 0) {
                Marker marker = new Marker(nameText);
                addMarker(marker);
                name.setText("");
            }
        }

        private View.OnClickListener onMarkerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewMarker();
            }
        };

        private TextView.OnEditorActionListener onNameEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                addNewMarker();
                return false;
            }
        };
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private CheckBox check;

        public ItemViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            check = (CheckBox) itemView.findViewById(R.id.check);
            check.setOnClickListener(onCheckClickListener);
        }

        private void bindView(Marker marker) {
            check.setChecked(selectedMarkers != null && selectedMarkers.contains(marker));
            name.setText(marker.getName());
        }

        private View.OnClickListener onCheckClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemSelectionListener != null) {
                    Marker marker = markers.get(getLayoutPosition());
                    if (check.isChecked()) {
                        itemSelectionListener.onItemSelected(marker);
                    } else {
                        itemSelectionListener.onItemDeselected(marker);
                    }
                }
            }
        };
    }
}
