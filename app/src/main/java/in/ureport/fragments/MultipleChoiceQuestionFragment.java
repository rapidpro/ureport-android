package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.ilhasoft.support.tool.UnitConverter;
import in.ureport.R;
import in.ureport.listener.PollQuestionAnswerListener;
import in.ureport.models.MultipleChoiceQuestion;
import in.ureport.models.PollQuestion;
import in.ureport.util.SpaceItemDecoration;
import in.ureport.views.adapters.MultipleChoiceQuestionAdapter;

/**
 * Created by johncordeiro on 7/17/15.
 */
public class MultipleChoiceQuestionFragment extends Fragment {

    private static final String EXTRA_POLL_QUESTION = "multipleChoiceQuestion";

    private MultipleChoiceQuestion multipleChoiceQuestion;

    private PollQuestionAnswerListener pollQuestionAnswerListener;

    public static MultipleChoiceQuestionFragment newInstance(MultipleChoiceQuestion multipleChoiceQuestion) {
        MultipleChoiceQuestionFragment multipleChoiceQuestionFragment = new MultipleChoiceQuestionFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_POLL_QUESTION, multipleChoiceQuestion);
        multipleChoiceQuestionFragment.setArguments(args);

        return multipleChoiceQuestionFragment;
    }

    @Override
     public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null && getArguments().containsKey(EXTRA_POLL_QUESTION)) {
            multipleChoiceQuestion = getArguments().getParcelable(EXTRA_POLL_QUESTION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_multiple_choice_question, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupView(view);
    }

    private void setupView(View view) {
        TextView question = (TextView) view.findViewById(R.id.question);
        question.setText(multipleChoiceQuestion.getQuestion());

        UnitConverter unitConverter = new UnitConverter(getActivity());

        RecyclerView choiceList = (RecyclerView) view.findViewById(R.id.choicesList);
        choiceList.setLayoutManager(new LinearLayoutManager(getActivity()));

        SpaceItemDecoration spaceItemDecoration = new SpaceItemDecoration();
        spaceItemDecoration.setVerticalSpaceHeight((int)unitConverter.convertDpToPx(5));
        choiceList.addItemDecoration(spaceItemDecoration);

        MultipleChoiceQuestionAdapter multipleChoiceQuestionAdapter = new MultipleChoiceQuestionAdapter(multipleChoiceQuestion);
        multipleChoiceQuestionAdapter.setPollQuestionAnswerListener(pollQuestionAnswerListener);
        choiceList.setAdapter(multipleChoiceQuestionAdapter);
    }

    public void setPollQuestionAnswerListener(PollQuestionAnswerListener pollQuestionAnswerListener) {
        this.pollQuestionAnswerListener = pollQuestionAnswerListener;
    }
}
