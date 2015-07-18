package in.ureport.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import in.ureport.fragments.MultipleChoiceQuestionFragment;
import in.ureport.listener.PollQuestionAnswerListener;
import in.ureport.models.MultipleChoiceQuestion;
import in.ureport.models.PollQuestion;

/**
 * Created by johncordeiro on 7/17/15.
 */
public class PollQuestionAdapter extends FragmentPagerAdapter {

    private List<? extends PollQuestion> pollQuestions;

    private PollQuestionAnswerListener pollQuestionAnswerListener;

    public PollQuestionAdapter(FragmentManager manager, List<? extends PollQuestion> pollQuestions) {
        super(manager);
        this.pollQuestions = pollQuestions;
    }

    @Override
    public Fragment getItem(int position) {
        PollQuestion pollQuestion = pollQuestions.get(position);
        if(pollQuestion instanceof MultipleChoiceQuestion) {
            MultipleChoiceQuestionFragment multipleChoiceQuestionFragment = MultipleChoiceQuestionFragment
                    .newInstance((MultipleChoiceQuestion) pollQuestion);
            multipleChoiceQuestionFragment.setPollQuestionAnswerListener(pollQuestionAnswerListener);
            return multipleChoiceQuestionFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return pollQuestions.size();
    }

    public void setPollQuestionAnswerListener(PollQuestionAnswerListener pollQuestionAnswerListener) {
        this.pollQuestionAnswerListener = pollQuestionAnswerListener;
    }
}
