package in.ureport.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import in.ureport.R;
import in.ureport.fragments.CreateStoryFragment;

/**
 * Created by johncordeiro on 7/14/15.
 */
public class CreateStoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);

        if(savedInstanceState == null) {
            addCreateStoryFragment();
        }
    }

    private void addCreateStoryFragment() {
        CreateStoryFragment createStoryFragment = new CreateStoryFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, createStoryFragment)
                .commit();
    }

}
