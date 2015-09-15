package in.ureport.network;

import com.firebase.client.ValueEventListener;

import in.ureport.models.Poll;

/**
 * Created by johncordeiro on 14/09/15.
 */
public class PollServices extends ProgramServices {

    private static final String pollPath = "poll";
    private static final String pollResultPath = "poll_result";

    public void getPolls(ValueEventListener listener) {
        getDefaultRoot().child(pollPath).orderByKey().addListenerForSingleValueEvent(listener);
    }

    public void getPollResults(Poll poll, ValueEventListener listener) {
        getDefaultRoot().child(pollResultPath).child(poll.getKey()).addListenerForSingleValueEvent(listener);
    }

}
