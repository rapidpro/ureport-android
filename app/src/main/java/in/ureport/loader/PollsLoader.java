package in.ureport.loader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.ureport.R;
import in.ureport.models.MultipleChoiceQuestion;
import in.ureport.models.Poll;

/**
 * Created by johncordeiro on 7/16/15.
 */
public class PollsLoader extends AsyncTaskLoader<List<Poll>> {

    public PollsLoader(Context context) {
        super(context);
    }

    @Override
    public List<Poll> loadInBackground() {
        Calendar date1 = Calendar.getInstance();
        date1.roll(Calendar.DATE, 5);

        Poll poll1 = new Poll();
        poll1.setDescription(getContext().getString(R.string.poll1_description));
        poll1.setExpirationDate(date1.getTime());
        poll1.setResponseRate(8);
        poll1.setResponded(42957);
        poll1.setPolled(519574);

        List<MultipleChoiceQuestion> multipleChoiceQuestionList = getMultipleChoiceQuestions();
        poll1.setQuestions(multipleChoiceQuestionList);

        Calendar date2 = Calendar.getInstance();
        date2.roll(Calendar.MONTH, -2);

        Poll poll2 = new Poll();
        poll2.setDescription(getContext().getString(R.string.poll2_description));
        poll2.setExpirationDate(date2.getTime());
        poll2.setResponseRate(6);
        poll2.setResponded(30321);
        poll2.setPolled(469380);

        List<Poll> polls = new ArrayList<>();
        polls.add(poll1);
        polls.add(poll2);

        return polls;
    }

    @NonNull
    private List<MultipleChoiceQuestion> getMultipleChoiceQuestions() {
        List<MultipleChoiceQuestion> multipleChoiceQuestionList = new ArrayList<>();
        multipleChoiceQuestionList.add(getMultipleChoiceQuestion(R.string.poll1_question1
                , R.string.poll1_question1_answer1, R.string.poll1_question1_answer2));
        multipleChoiceQuestionList.add(getMultipleChoiceQuestion(R.string.poll1_question2
                , R.string.poll1_question2_answer1, R.string.poll1_question2_answer2
                , R.string.poll1_question2_answer3, R.string.poll1_question2_answer4));
        multipleChoiceQuestionList.add(getMultipleChoiceQuestion(R.string.poll1_question3
                , R.string.poll1_question3_answer1, R.string.poll1_question3_answer2
                , R.string.poll1_question3_answer3));
        return multipleChoiceQuestionList;
    }

    private MultipleChoiceQuestion getMultipleChoiceQuestion(int questionId, int... choicesId) {
        String question = getContext().getString(questionId);
        List<String> choices = new ArrayList<>();

        for (int choiceId : choicesId) {
            choices.add(getContext().getString(choiceId));
        }

        MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion();
        multipleChoiceQuestion.setQuestion(question);
        multipleChoiceQuestion.setChoices(choices);
        return multipleChoiceQuestion;
    }
}
