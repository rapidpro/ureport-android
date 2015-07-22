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

        String [] markerStringArray = getContext().getResources().getStringArray(R.array.markers);
        for (String marker : markerStringArray) {
            markers.add(new Marker(marker));
        }

        return markers;
    }

}
