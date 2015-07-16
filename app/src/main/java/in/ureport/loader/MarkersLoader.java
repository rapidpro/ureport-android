package in.ureport.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

import br.com.ilhasoft.support.db.business.AbstractBusiness;
import br.com.ilhasoft.support.db.repository.AbstractRepository;
import in.ureport.R;
import in.ureport.models.Marker;

/**
 * Created by johncordeiro on 7/15/15.
 */
public class MarkersLoader extends AsyncTaskLoader<List<Marker>> {

    public MarkersLoader(Context context) {
        super(context);
    }

    @Override
    public List<Marker> loadInBackground() {
        List<Marker> markers = new ArrayList<>();
        markers.add(new Marker(getContext().getString(R.string.marker_name_water)));
        markers.add(new Marker(getContext().getString(R.string.marker_name_sanitation)));
        markers.add(new Marker(getContext().getString(R.string.marker_name_politics)));
        markers.add(new Marker(getContext().getString(R.string.marker_name_education)));
        markers.add(new Marker(getContext().getString(R.string.marker_name_violence)));

        return markers;
    }

}
