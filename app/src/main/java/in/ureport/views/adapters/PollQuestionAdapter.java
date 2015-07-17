package in.ureport.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import in.ureport.fragments.MultipleChoiceQuestionFragment;
import in.ureport.models.MultipleChoiceQuestion;
import in.ureport.models.PollQuestion;

/**
 * Created by johncordeiro on 7/17/15.
 */
public class PollQuestionAdapter extends FragmentPagerAdapter {

    private List<? extends PollQuestion> pollQuestions;

    public PollQuestionAdapter(FragmentManager manager, List<? extends PollQuestion> pollQuestions) {
        super(manager);
        this.pollQuestions = pollQuestions;
    }

    @Override
    public Fragment getItem(int position) {
        PollQuestion pollQuestion = pollQuestions.get(position);
        if(pollQuestion instanceof MultipleChoiceQuestion) {
            return MultipleChoiceQuestionFragment.newInstance((MultipleChoiceQuestion)pollQuestion);
        }
        return null;
    }

    @Override
    public int getCount() {
        return pollQuestions.size();
    }
}
