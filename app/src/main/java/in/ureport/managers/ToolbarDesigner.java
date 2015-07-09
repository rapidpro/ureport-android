package in.ureport.managers;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

/**
 * Created by ilhasoft on 7/9/15.
 */
public class ToolbarDesigner {

    private static final String TAG = "ToolbarDesigner";

    public void setupFragmentDefaultToolbar(Toolbar toolbar, Fragment fragment) {
        try {
            AppCompatActivity activity = ((AppCompatActivity) fragment.getActivity());
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            fragment.setHasOptionsMenu(true);
        } catch(Exception exception) {
            Log.e(TAG, "setupFragmentDefaultToolbar ", exception);
        }
    }

}
