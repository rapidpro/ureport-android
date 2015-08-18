package in.ureport.helpers;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by johncordeiro on 16/08/15.
 */
public abstract class ValueEventListenerAdapter implements ValueEventListener {

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
}
