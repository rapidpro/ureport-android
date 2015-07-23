package in.ureport.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import in.ureport.R;
import in.ureport.fragments.AnswerPollFragment;
import in.ureport.fragments.NewsViewFragment;
import in.ureport.managers.CountryProgramManager;
import in.ureport.models.Poll;

/**
 * Created by johncordeiro on 7/17/15.
 */
public class AnswerPollActivity extends AppCompatActivity implements AnswerPollFragment.AnswerPollListener {

    public static final String EXTRA_POLL = "poll";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CountryProgramManager.setThemeIfNeeded(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic);

        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null && extras.containsKey(EXTRA_POLL)) {
                Poll poll = extras.getParcelable(EXTRA_POLL);

                AnswerPollFragment answerPollFragment = AnswerPollFragment.newInstance(poll);
                answerPollFragment.setAnswerPollListener(this);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.content, answerPollFragment)
                        .commit();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onPollAnswered(Poll poll) {
        finish();
    }
}
