package in.ureport.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import br.com.ilhasoft.support.widget.FadePageTransformer;
import in.ureport.R;
import in.ureport.models.holders.Tutorial;
import in.ureport.pref.SystemPreferences;
import in.ureport.views.adapters.TutorialAdapter;

/**
 * Created by johncordeiro on 05/10/15.
 */
public class TutorialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_OK);
        setContentView(R.layout.activity_tutorial);
        setupView();
        setupTutorialView();
    }

    private void setupTutorialView() {
        SystemPreferences systemPreferences = new SystemPreferences(this);
        systemPreferences.setTutorialView(true);
    }

    private void setupView() {
        ViewPager tutorialPager = (ViewPager) findViewById(R.id.tutorialPager);

        TutorialAdapter tutorialAdapter = new TutorialAdapter(getSupportFragmentManager(), getTutorialList());
        tutorialPager.setAdapter(tutorialAdapter);

        CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(tutorialPager);

        TextView getStarted = (TextView) findViewById(R.id.getStarted);
        getStarted.setOnClickListener(onGetStartedClickListener);

        setupPagerTransformer(tutorialPager);
    }

    private void setupPagerTransformer(ViewPager tutorialPager) {
        FadePageTransformer fadePageTransformer = new FadePageTransformer();
        fadePageTransformer.addViewToFade(new FadePageTransformer.TransformInformation(R.id.image));
        fadePageTransformer.addViewToFade(new FadePageTransformer.TransformInformation(R.id.infoBackground));

        tutorialPager.setPageTransformer(true, fadePageTransformer);
    }

    @NonNull
    private List<Tutorial> getTutorialList() {
        List<Tutorial> tutorialList = new ArrayList<>();
        tutorialList.add(new Tutorial(getString(R.string.tutorial_title1)
                                    , getString(R.string.tutorial_description1)
                                    , R.drawable.tutorial_item1
                                    , R.color.tutorial_background1));

        tutorialList.add(new Tutorial(getString(R.string.tutorial_title2)
                                    , getString(R.string.tutorial_description2)
                                    , R.drawable.tutorial_item2
                                    , R.color.tutorial_background2));

        tutorialList.add(new Tutorial(getString(R.string.tutorial_title3)
                                    , getString(R.string.tutorial_description3)
                                    , R.drawable.tutorial_item3
                                    , R.color.tutorial_background3));

        return tutorialList;
    }

    private View.OnClickListener onGetStartedClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
}
