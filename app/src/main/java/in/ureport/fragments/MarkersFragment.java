package in.ureport.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.listener.ItemSelectionListener;
import in.ureport.listener.SelectionResultListener;
import in.ureport.loader.MarkersLoader;
import in.ureport.models.Marker;
import in.ureport.views.adapters.MarkerAdapter;

/**
 * Created by johncordeiro on 7/15/15.
 */
public class MarkersFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Marker>>, ItemSelectionListener<Marker> {

    private static final String TAG = "MarkersFragment";
    private static final String EXTRA_SELECTED_MARKERS = "selectedMarkers";

    private List<Marker> selectedMarkers;
    private SelectionResultListener<Marker> selectionResultListener;

    private RecyclerView markersList;

    public static MarkersFragment newInstance(ArrayList<Marker> markers) {
        MarkersFragment markersFragment = new MarkersFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_SELECTED_MARKERS, markers);
        markersFragment.setArguments(args);

        return markersFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof SelectionResultListener) {
            selectionResultListener = (SelectionResultListener<Marker>) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        if(extras != null && extras.containsKey(EXTRA_SELECTED_MARKERS)) {
            selectedMarkers = extras.getParcelableArrayList(EXTRA_SELECTED_MARKERS);
        } else if(savedInstanceState != null && savedInstanceState.containsKey(EXTRA_SELECTED_MARKERS)) {
            selectedMarkers = savedInstanceState.getParcelableArrayList(EXTRA_SELECTED_MARKERS);
        } else {
            selectedMarkers = new ArrayList<>();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_SELECTED_MARKERS, (ArrayList<Marker>) selectedMarkers);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_markers, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public void onResume() {
        super.onResume();

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_done_white_24dp);
    }

    private void setupView(View view) {
        setHasOptionsMenu(true);

        markersList = (RecyclerView) view.findViewById(R.id.markersList);
        markersList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(selectionResultListener != null)
            selectionResultListener.onSelectionResult(selectedMarkers);
    }

    @Override
    public Loader<List<Marker>> onCreateLoader(int id, Bundle args) {
        return new MarkersLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Marker>> loader, List<Marker> data) {
        fillChoosenMarker(data);
        MarkerAdapter markerAdapter = new MarkerAdapter(data, selectedMarkers);
        markerAdapter.setItemSelectionListener(this);
        markersList.setAdapter(markerAdapter);
    }

    private void fillChoosenMarker(List<Marker> data) {
        if(selectedMarkers != null) {
            for (Marker selectedMarker : selectedMarkers) {
                if(!data.contains(selectedMarker)) {
                    data.add(selectedMarker);
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Marker>> loader) {}

    @Override
    public void onItemSelected(Marker item) {
        selectedMarkers.add(item);
    }

    @Override
    public void onItemDeselected(Marker item) {
        selectedMarkers.remove(item);
    }

}
