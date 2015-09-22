package in.ureport.helpers;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;

/**
 * Created by johncordeiro on 22/09/15.
 */
public class ValueIncrementerTransaction implements Transaction.Handler {

    private static final String TAG = "ValueIncrementer";

    private int increment;

    public ValueIncrementerTransaction(int increment) {
        this.increment = increment;
    }

    @Override
    public Transaction.Result doTransaction(MutableData mutableData) {
        if (mutableData.getValue() == null) {
            mutableData.setValue(1);
        } else {
            mutableData.setValue((Long) mutableData.getValue() + increment);
        }
        return Transaction.success(mutableData);
    }

    @Override
    public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
        if(firebaseError != null) {
            Log.e(TAG, "onComplete " + firebaseError);
        }
    }
}
