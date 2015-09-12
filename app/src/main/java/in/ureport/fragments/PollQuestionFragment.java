package in.ureport.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;

import in.ureport.R;
import in.ureport.helpers.ChildEventListenerAdapter;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.listener.OnSeeLastPollsListener;
import in.ureport.managers.UserManager;
import in.ureport.models.User;
import in.ureport.models.rapidpro.Message;
import in.ureport.network.RapidProServices;
import in.ureport.network.UserServices;

/**
 * Created by johncordeiro on 11/09/15.
 */
public class PollQuestionFragment extends Fragment {

    private static final int SCROLL_DELAY = 500;

    private TextView userInfo;
    private TextView question;
    private EditText response;
    private ScrollView contentScroll;

    private User user;

    private RapidProServices rapidProServices;
    private UserServices userServices;

    private OnSeeLastPollsListener onSeeLastPollsListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_poll_question, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupObjects();
        setupView(view);
        loadData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnSeeLastPollsListener) {
            onSeeLastPollsListener = (OnSeeLastPollsListener) context;
        }
    }

    private void loadData() {
        if(UserManager.isUserLoggedIn()) {
            userServices.getUser(UserManager.getUserId(), new ValueEventListenerAdapter() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    super.onDataChange(dataSnapshot);
                    user = dataSnapshot.getValue(User.class);
                    updateUserInfo();
                }
            });
        } else {
            updateUserInfoForLogged();
        }
    }

    private void updateUserInfoForLogged() {
        userInfo.setText(R.string.title_poll_question_no_login);
        response.setEnabled(false);
        question.setText(null);
    }

    private void updateUserInfo() {
        userInfo.setText(getString(R.string.title_poll_question_user, user.getNickname()));
        rapidProServices.addLastMessageChildEventListener(onLastMessageChildEventListener);
    }

    private void setupView(View view) {
        contentScroll = (ScrollView) view.findViewById(R.id.contentScroll);

        userInfo = (TextView) view.findViewById(R.id.userInfo);
        question = (TextView) view.findViewById(R.id.question);

        response = (EditText) view.findViewById(R.id.response);
        response.setOnEditorActionListener(onResponseEditorActionListener);

        Button viewPreviousPolls = (Button) view.findViewById(R.id.viewPreviousPolls);
        viewPreviousPolls.setOnClickListener(onViewPreviousPollsClickListener);
    }

    private void sendMessage(String message) {
        response.setText(null);
        rapidProServices.sendMessage(getActivity(), message);
        Toast.makeText(getActivity(), R.string.response_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rapidProServices.removeLastMessageChildEventListener(onLastMessageChildEventListener);
    }

    private void setupObjects() {
        userServices = new UserServices();
        rapidProServices = new RapidProServices();
    }

    private ChildEventListenerAdapter onLastMessageChildEventListener = new ChildEventListenerAdapter() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {
            super.onChildAdded(dataSnapshot, previousChildKey);
            updateLastMessageBySnapshot(dataSnapshot);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildKey) {
            super.onChildChanged(dataSnapshot, previousChildKey);
            updateLastMessageBySnapshot(dataSnapshot);
        }
    };

    private void updateLastMessageBySnapshot(DataSnapshot dataSnapshot) {
        Message lastMessage = dataSnapshot.getValue(Message.class);
        lastMessage.setKey(dataSnapshot.getKey());
        question.setText(lastMessage.getText());
        scrollDownDelayed();
    }

    private void scrollDownDelayed() {
        contentScroll.postDelayed(new Runnable() {
            @Override
            public void run() {
                contentScroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, SCROLL_DELAY);
    }

    private TextView.OnEditorActionListener onResponseEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
            String message = textView.getText().toString();
            sendMessage(message);
            return true;
        }
    };

    private View.OnClickListener onViewPreviousPollsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(onSeeLastPollsListener != null) {
                onSeeLastPollsListener.onSeeLastPolls();
            }
        }
    };
}
