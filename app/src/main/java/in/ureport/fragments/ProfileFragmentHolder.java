package in.ureport.fragments;

import com.firebase.client.DataSnapshot;

import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.managers.UserManager;
import in.ureport.network.UserServices;

public class ProfileFragmentHolder {

    private static ValueEventListenerAdapter firebaseValueEventListenerAdapter;

    public static void registerFirebaseValueEventListenerAdapter(ValueEventListenerAdapter listenerAdapter) {
        firebaseValueEventListenerAdapter = listenerAdapter;
    }

    public static void loadUser() {
        UserServices userServices = new UserServices();
        userServices.getUser(UserManager.getUserId(), new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseValueEventListenerAdapter.onDataChange(dataSnapshot);
            }
        });
    }

}
