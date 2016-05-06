package in.ureport.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;

import java.util.Date;

import in.ureport.R;
import in.ureport.helpers.ChildEventListenerAdapter;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.managers.UserManager;
import in.ureport.models.Contribution;
import in.ureport.models.Poll;
import in.ureport.models.User;
import in.ureport.network.ContributionServices;
import in.ureport.network.UserServices;
import in.ureport.views.adapters.ContributionAdapter;
import in.ureport.views.adapters.ContributionAdapter.OnContributionRemoveListener;

/**
 * Created by john-mac on 5/6/16.
 */
public class PollsResultsContributionsDialog extends BottomSheetDialogFragment implements OnContributionRemoveListener {

    private static final String EXTRA_POLL = "poll";

    private BottomSheetBehavior<View> bottomSheetBehavior;

    private ContributionServices contributionServices;
    private UserServices userServices;

    private User user;
    private Poll poll;

    private EditText contribution;
    private TextView contribute;

    private ContributionAdapter contributionAdapter;

    public static PollsResultsContributionsDialog newInstance(Poll poll) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_POLL, poll);

        PollsResultsContributionsDialog fragment = new PollsResultsContributionsDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.dialog_poll_contributions, null);
        setupObjects();
        setupView(view);
        loadData();
        dialog.setContentView(view);

        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);
        return dialog;
    }

    private void loadData() {
        contributionServices.addChildEventListener(poll.getKey(), contributionChildEventListener);
    }

    private void setupObjects() {
        poll = getArguments().getParcelable(EXTRA_POLL);

        user = new User();
        user.setKey(UserManager.getUserId());

        contributionServices = new ContributionServices(ContributionServices.Type.Poll);
        userServices = new UserServices();
    }

    private void setupView(View view) {
        ImageButton addContribution = (ImageButton) view.findViewById(R.id.addContribution);
        addContribution.setOnClickListener((buttonView) -> this.onAddNewContribution());

        contribution = (EditText) view.findViewById(R.id.contribution);
        contribution.setHint(R.string.poll_comment);
        contribution.setOnEditorActionListener(onDescriptionEditorActionListener);

        RecyclerView contributionList = (RecyclerView) view.findViewById(R.id.contributionList);
        ((SimpleItemAnimator) contributionList.getItemAnimator()).setSupportsChangeAnimations(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext()
                , LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setAutoMeasureEnabled(true);
        linearLayoutManager.setStackFromEnd(true);
        contributionList.setLayoutManager(linearLayoutManager);

        contributionAdapter = new ContributionAdapter();
        contributionAdapter.setOnContributionRemoveListener(this);
        contributionList.setAdapter(contributionAdapter);

        contribute = (TextView) view.findViewById(R.id.contribute);
        contribute.setText(R.string.title_comment_poll);
    }

    private TextView.OnEditorActionListener onDescriptionEditorActionListener = (textView, i, keyEvent) -> {
        onAddNewContribution();
        return true;
    };

    private void onAddNewContribution() {
        if(contribution.getText().length() > 0) {
            addContribution(contribution.getText().toString());
        }
    }

    public void addContribution(String content) {
        if(UserManager.validateKeyAction(getActivity())) {
            final Contribution contribution = new Contribution(content, user);
            contribution.setCreatedDate(new Date());

            contributionServices.saveContribution(poll.getKey(), contribution, (firebaseError, firebase) -> {
                if(firebaseError == null) {
                    userServices.incrementContributionPoint();
                    PollsResultsContributionsDialog.this.contribution.setText(null);
                }
            });
        }
    }

    private ChildEventListenerAdapter contributionChildEventListener = new ChildEventListenerAdapter() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChild) {
            super.onChildAdded(dataSnapshot, previousChild);

            Contribution contribution = getContributionFromSnapshot(dataSnapshot);
            loadUserFromContribution(contribution);

            contribute.setVisibility(View.GONE);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            super.onChildRemoved(dataSnapshot);

            Contribution contribution = getContributionFromSnapshot(dataSnapshot);
            contributionAdapter.removeContribution(contribution);

            if(contributionAdapter.getItemCount() <= 0) {
                contribute.setVisibility(View.VISIBLE);
            }
        }
    };

    private void loadUserFromContribution(final Contribution contribution) {
        userServices.getUser(contribution.getAuthor().getKey(), new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                contribution.setAuthor(user);
                contributionAdapter.addContribution(contribution);
            }
        });
    }

    @NonNull
    private Contribution getContributionFromSnapshot(DataSnapshot dataSnapshot) {
        final Contribution contribution = dataSnapshot.getValue(Contribution.class);
        contribution.setKey(dataSnapshot.getKey());
        return contribution;
    }

    @Override
    public void onStart() {
        super.onStart();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onContributionRemove(Contribution contribution) {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null
                , getString(R.string.load_message_wait), true, false);
        contributionServices.removeContribution(poll.getKey(), contribution, (firebaseError, firebase) -> {
            progressDialog.dismiss();
            if (firebaseError == null) {
                displayToast(R.string.message_success_remove);
            } else {
                displayToast(R.string.error_remove);
            }
        });
    }

    private void displayToast(@StringRes int messageId) {
        Toast.makeText(getActivity(), messageId, Toast.LENGTH_SHORT).show();
    }

    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            switch (newState) {
                case BottomSheetBehavior.STATE_COLLAPSED:
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    break;
                case BottomSheetBehavior.STATE_HIDDEN:
                    dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };
}
