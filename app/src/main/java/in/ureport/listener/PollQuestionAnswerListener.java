package in.ureport.listener;

import in.ureport.models.PollQuestion;

/**
 * Created by johncordeiro on 18/07/15.
 */
public interface PollQuestionAnswerListener {

    void onQuestionAnswered(PollQuestion pollQuestion);

}
