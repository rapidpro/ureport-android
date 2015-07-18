package in.ureport.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.ureport.R;
import in.ureport.listener.PollQuestionAnswerListener;
import in.ureport.models.Poll;
import in.ureport.models.PollQuestion;
import in.ureport.views.adapters.PollQuestionAdapter;
import in.ureport.views.widgets.ContentPager;

/**
 * Created by johncordeiro on 7/17/15.
 */
public class AnswerPollFragment extends Fragment implements PollQuestionAnswerListener {

    private static final String TAG = "AnswerPollFragment";

    private static final String EXTRA_POLL = "poll";

    private Poll poll;
    private ContentPager questionsList;

    private AnswerPollListener answerPollListener;

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

        questionsList = (ContentPager) view.findViewById(R.id.questionsList);
        questionsList.setPagingEnabled(false);

        PollQuestionAdapter pollQuestionAdapter = new PollQuestionAdapter(getFragmentManager(), poll.getQuestions());
        pollQuestionAdapter.setPollQuestionAnswerListener(this);
        questionsList.setAdapter(pollQuestionAdapter);
    }

    @Override
    public void onQuestionAnswered(PollQuestion pollQuestion) {
        int indexOfQuestion = poll.getQuestions().indexOf(pollQuestion);
        if(indexOfQuestion != poll.getQuestions().size()-1) {
            questionsList.setCurrentItem(indexOfQuestion+1, true);
        } else {
            showConfirmDialog();
        }
    }

    private void showConfirmDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.answer_poll_greetings_title)
                .setMessage(R.string.answer_poll_greeting_message)
                .setNeutralButton(R.string.confirm_neutral_dialog_button, onConfirmClickListener)
                .setCancelable(false)
                .create();
        dialog.show();
    }

    private DialogInterface.OnClickListener onConfirmClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (answerPollListener != null)
                answerPollListener.onPollAnswered(poll);
        }
    };

    public void setAnswerPollListener(AnswerPollListener answerPollListener) {
        this.answerPollListener = answerPollListener;
    }

    public interface AnswerPollListener {
        void onPollAnswered(Poll poll);
    }
}
