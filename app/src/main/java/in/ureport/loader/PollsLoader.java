package in.ureport.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.ureport.R;
import in.ureport.models.Poll;

/**
 * Created by johncordeiro on 7/16/15.
 */
public class PollsLoader extends AsyncTaskLoader<List<Poll>> {

    public PollsLoader(Context context) {
        super(context);
    }

    @Override
    public List<Poll> loadInBackground() {
        Calendar date1 = Calendar.getInstance();
        date1.roll(Calendar.DATE, 5);

        Poll poll1 = new Poll();
        poll1.setDescription(getContext().getString(R.string.poll1_description));
        poll1.setExpirationDate(date1.getTime());
        poll1.setResponseRate(8);
        poll1.setResponded(42957);
        poll1.setPolled(519574);

        Calendar date2 = Calendar.getInstance();
        date2.roll(Calendar.MONTH, -2);

        Poll poll2 = new Poll();
        poll2.setDescription(getContext().getString(R.string.poll2_description));
        poll2.setExpirationDate(date2.getTime());
        poll2.setResponseRate(6);
        poll2.setResponded(30321);
        poll2.setPolled(469380);

        List<Poll> polls = new ArrayList<>();
        polls.add(poll1);
        polls.add(poll2);

        Log.i("PollsLoader", "loadInBackground " + polls.size());

        return polls;
    }
}
