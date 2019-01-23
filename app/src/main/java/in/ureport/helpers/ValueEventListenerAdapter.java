package in.ureport.helpers;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by johncordeiro on 16/08/15.
 */
public abstract class ValueEventListenerAdapter implements ValueEventListener {

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) { }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) { }

}
