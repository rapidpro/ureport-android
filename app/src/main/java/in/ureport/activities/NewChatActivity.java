package in.ureport.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import in.ureport.R;
import in.ureport.fragments.NewChatFragment;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class NewChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);
        setupView();

        if(savedInstanceState == null) {
            NewChatFragment newChatFragment = new NewChatFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, newChatFragment)
                    .commit();
        }
    }

    private void setupView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
