package in.ureport.loader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.ureport.R;
import in.ureport.models.MultipleChoiceQuestion;
import in.ureport.models.MultipleResult;
import in.ureport.models.OpenQuestion;
import in.ureport.models.Poll;
import in.ureport.models.PollCategory;
import in.ureport.models.PollQuestion;
import in.ureport.models.PollResult;
import in.ureport.models.User;
import in.ureport.models.WordsResult;

/**
 * Created by johncordeiro on 7/16/15.
 */
public class PollsLoader extends AsyncTaskLoader<List<Poll>> {

    private boolean publicType = true;
    private User user;

    public PollsLoader(Context context) {
        super(context);
    }

    public PollsLoader(Context context, User user) {
        super(context);
        this.user = user;
        this.publicType = false;
    }

    @Override
    public List<Poll> loadInBackground() {
        Calendar date1 = Calendar.getInstance();
        date1.add(Calendar.DATE, 5);

        Poll poll1 = new Poll();
        poll1.setDescription(getContext().getString(R.string.poll1_description));
        poll1.setExpirationDate(date1.getTime());
        poll1.setResponseRate(8);
        poll1.setResponded(42957);
        poll1.setPolled(519574);

        PollCategory waterCategory = new PollCategory();
        waterCategory.setColor(R.color.poll_category_water);
        waterCategory.setIcon(R.drawable.poll_category_water);
        waterCategory.setName(getContext().getString(R.string.poll_category_water));

        poll1.setCategory(waterCategory);

        List<PollQuestion> questionsForPoll1 = getPollQuestionsForPoll1();
        poll1.setQuestions(questionsForPoll1);

        Calendar date2 = Calendar.getInstance();
        date2.add(Calendar.MONTH, -2);

        Poll poll2 = new Poll();
        poll2.setDescription(getContext().getString(R.string.poll2_description));
        poll2.setExpirationDate(date2.getTime());
        poll2.setResponseRate(6);
        poll2.setResponded(30321);
        poll2.setPolled(469380);

        PollCategory childCategory = new PollCategory();
        childCategory.setColor(R.color.poll_category_child);
        childCategory.setIcon(R.drawable.poll_category_child);
        childCategory.setName(getContext().getString(R.string.poll_category_child));

        poll2.setCategory(childCategory);

        List<PollQuestion> questionsForPoll2 = getPollQuestionsForPoll2();
        poll2.setQuestions(questionsForPoll2);

        List<PollResult> resultsForPoll2 = getResultsForPoll2(poll2);
        poll2.setResults(resultsForPoll2);

        List<Poll> polls = new ArrayList<>();
        if(publicType) polls.add(poll1);
        polls.add(poll2);

        return polls;
    }

    @NonNull
    private List<PollResult> getResultsForPoll2(Poll poll2) {
        List<PollResult> resultsForPoll2 = new ArrayList<>();

        MultipleResult multipleResult1 = new MultipleResult();
        multipleResult1.setDate(poll2.getExpirationDate());
        multipleResult1.setImage(R.drawable.poll2_question1_image);
        multipleResult1.setPolled(469380);
        multipleResult1.setResponded(71941);
        multipleResult1.setQuestion(poll2.getQuestions().get(0));

        MultipleResult multipleResult2 = new MultipleResult();
        multipleResult2.setDate(poll2.getExpirationDate());
        multipleResult2.setImage(R.drawable.poll2_question2_image);
        multipleResult2.setPolled(71941);
        multipleResult2.setResponded(47176);
        multipleResult2.setQuestion(poll2.getQuestions().get(1));

        WordsResult wordsResult = new WordsResult();
        wordsResult.setDate(poll2.getExpirationDate());
        wordsResult.setPolled(47176);
        wordsResult.setResponded(30321);
        wordsResult.setQuestion(poll2.getQuestions().get(2));

        List<String> words = getWordsForPoll2();
        wordsResult.setResults(words);

        resultsForPoll2.add(multipleResult1);
        resultsForPoll2.add(multipleResult2);
        resultsForPoll2.add(wordsResult);
        return resultsForPoll2;
    }

    private List<String> getWordsForPoll2() {
        return getWords(R.string.poll2_question3_word1
                                        , R.string.poll2_question3_word2
                                        , R.string.poll2_question3_word3
                                        , R.string.poll2_question3_word4
                                        , R.string.poll2_question3_word5
                                        , R.string.poll2_question3_word6
                                        , R.string.poll2_question3_word7
                                        , R.string.poll2_question3_word8);
    }

    private List<String> getWords(@StringRes int... wordsIds) {
        List<String> words = new ArrayList<>();
        for (int wordId : wordsIds) {
            words.add(getContext().getString(wordId));
        }
        return words;
    }

    @NonNull
    private List<PollQuestion> getPollQuestionsForPoll2() {
        List<PollQuestion> questionsForPoll2 = new ArrayList<>();
        questionsForPoll2.add(getMultipleChoiceQuestion(R.string.poll2_question1
                , R.string.poll2_question1_answer1, R.string.poll2_question1_answer2));
        questionsForPoll2.add(getMultipleChoiceQuestion(R.string.poll2_question2
                , R.string.poll2_question2_answer1, R.string.poll2_question2_answer2));
        OpenQuestion openQuestion = new OpenQuestion();
        openQuestion.setQuestion(getContext().getString(R.string.poll2_question3));
        questionsForPoll2.add(openQuestion);
        return questionsForPoll2;
    }

    @NonNull
    private List<PollQuestion> getPollQuestionsForPoll1() {
        List<PollQuestion> multipleChoiceQuestionList = new ArrayList<>();
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
