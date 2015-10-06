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

import br.com.ilhasoft.support.widget.ParallaxPageTransformer;
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
        ParallaxPageTransformer parallaxPageTransformer = new ParallaxPageTransformer();
        parallaxPageTransformer.addViewToParallax(new ParallaxPageTransformer.ParallaxTransformInformation(R.id.image
                , -0.65f, ParallaxPageTransformer.ParallaxTransformInformation.PARALLAX_EFFECT_DEFAULT));
        parallaxPageTransformer.addViewToParallax(new ParallaxPageTransformer.ParallaxTransformInformation(R.id.description
                , -0.85f, ParallaxPageTransformer.ParallaxTransformInformation.PARALLAX_EFFECT_DEFAULT));

        tutorialPager.setPageTransformer(true, parallaxPageTransformer);
    }

    @NonNull
    private List<Tutorial> getTutorialList() {
        List<Tutorial> tutorialList = new ArrayList<>();
        tutorialList.add(new Tutorial(getString(R.string.tutorial_title1)
                                    , getString(R.string.tutorial_description1)
                                    , R.drawable.tutorial_item1));

        tutorialList.add(new Tutorial(getString(R.string.tutorial_title2)
                                    , getString(R.string.tutorial_description2)
                                    , R.drawable.tutorial_item2));

        tutorialList.add(new Tutorial(getString(R.string.tutorial_title3)
                                    , getString(R.string.tutorial_description3)
                                    , R.drawable.tutorial_item3));

        return tutorialList;
    }

    private View.OnClickListener onGetStartedClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
}
