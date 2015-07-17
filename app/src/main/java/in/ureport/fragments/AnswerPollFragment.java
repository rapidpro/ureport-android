package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import in.ureport.R;
import in.ureport.models.Poll;
import in.ureport.views.adapters.PollQuestionAdapter;
import in.ureport.views.widgets.ContentPager;

/**
 * Created by johncordeiro on 7/17/15.
 */
public class AnswerPollFragment extends Fragment {

    private static final String EXTRA_POLL = "poll";

    private Poll poll;

    public static AnswerPollFragment newInstance(Poll poll) {
        AnswerPollFragment answerPollFragment = new AnswerPollFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_POLL, poll);
        answerPollFragment.setArguments(args);

        return answerPollFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null && getArguments().containsKey(EXTRA_POLL)) {
            poll = getArguments().getParcelable(EXTRA_POLL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_answer_poll, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupView(view);
    }

    private void setupView(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ContentPager questionsList = (ContentPager) view.findViewById(R.id.questionsList);
//        questionsList.setPagingEnabled(false);

        PollQuestionAdapter pollQuestionAdapter = new PollQuestionAdapter(getFragmentManager(), poll.getQuestions());
        questionsList.setAdapter(pollQuestionAdapter);
    }
}
