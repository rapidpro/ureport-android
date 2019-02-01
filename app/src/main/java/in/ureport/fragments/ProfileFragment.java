package in.ureport.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import in.ureport.R;
import in.ureport.activities.ProfileActivity;
import in.ureport.helpers.ImageLoader;
import in.ureport.helpers.MediaSelector;
import in.ureport.helpers.TransferListenerAdapter;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.listener.OnEditProfileListener;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.TransferManager;
import in.ureport.managers.UserManager;
import in.ureport.models.LocalMedia;
import in.ureport.models.Media;
import in.ureport.models.User;
import in.ureport.models.holders.NavigationItem;
import in.ureport.network.UserServices;
import in.ureport.views.adapters.NavigationAdapter;

/**
 * Created by johncordeiro on 18/07/15.
 */
public class ProfileFragment extends ProgressFragment {

    public static final String TAG = "ProfileFragment";

    private static final String EXTRA_USER = "user";
    private static final int RANKING_POSITION = 1;

    private TextView name;
    private TextView location;
    private ViewPager pager;
    private TextView points;
    private TextView stories;
    private ImageView picture;
    private TabLayout tabs;

    private User user;

    private OnEditProfileListener onEditProfileListener;

    private MediaSelector mediaSelector;
    private UserServices userServices;

    private static ValueEventListenerAdapter firebaseValueEventListenerAdapter;
    private static TransferListenerAdapter firebaseImageTransferListenerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        if (extras != null && extras.containsKey(EXTRA_USER)) {
            user = extras.getParcelable(EXTRA_USER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObjects();
        setupView(view);
        setupContextDependencies();
        loadUser();
        setLoadingMessage(getString(R.string.load_message_uploading_image));
    }

    private void setupObjects() {
        userServices = new UserServices();
        mediaSelector = new MediaSelector(getContext());
    }

    private void setupContextDependencies() {
        firebaseValueEventListenerAdapter = new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                user = dataSnapshot.getValue(User.class);
                if (isAdded()) updateUser(user);
            }
        };
        firebaseImageTransferListenerAdapter = new TransferListenerAdapter(getContext(), null) {
            @Override
            public void onStart() {
                super.onStart();
                showLoading();
            }

            @Override
            public void onTransferFinished(Media media) {
                super.onTransferFinished(media);
                dismissLoading();
                if (user == null)
                    return;

                showLoading();
                user.setPicture(media.getUrl());
                userServices.editUserPicture(user, (firebaseError, firebase) -> {
                    dismissLoading();
                    if (firebaseError == null)
                        ImageLoader.loadPersonPictureToImageView(picture, media.getUrl());
                    else
                        displayPictureError();
                });
            }

            @Override
            public void onTransferFailed() {
                super.onTransferFailed();
                dismissLoading();
                displayPictureError();
            }

            @Override
            public void onError(int id, Exception ex) {
                super.onError(id, ex);
                dismissLoading();
                displayPictureError();
            }
        };
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEditProfileListener) {
            onEditProfileListener = (OnEditProfileListener) context;
        }
    }

    public void loadUser() {
        if (firebaseValueEventListenerAdapter == null)
            return;

        UserServices userServices = new UserServices();
        userServices.getUser(UserManager.getUserId(), new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseValueEventListenerAdapter.onDataChange(dataSnapshot);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mediaSelector.onActivityResult(this, onLoadLocalMediaListener, requestCode, resultCode, data);
    }

    private void setupView(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

        picture = view.findViewById(R.id.picture);
        name = view.findViewById(R.id.name);
        location = view.findViewById(R.id.location);
        picture.setOnClickListener(onPictureClickListener);

        points = view.findViewById(R.id.points);
        stories = view.findViewById(R.id.storiesCount);

//        pager = (ViewPager)view.findViewById(R.id.pager);
//        tabs = (TabLayout)view.findViewById(R.id.tabs);

//        Button logout = (Button) view.findViewById(R.id.logout);
//        logout.setOnClickListener(onLogoutClickListener);

//        Button edit = (Button) view.findViewById(R.id.edit);
//        edit.setOnClickListener(onEditClickListener);
    }

    private void updateUser(User user) {
//        setupPagerWithUser(user);

        name.setText(user.getNickname());
        ImageLoader.loadPersonPictureToImageView(picture, user.getPicture());

        final String country = CountryProgramManager.getCurrentCountryProgram().getName();
        location.setText(country.concat(", ").concat(user.getState()));

        final String pointsCount = String.valueOf(getIntegerValue(user.getPoints()));
        final String storiesCount = String.valueOf(getIntegerValue(user.getStories()));

        points.setText(makeUserMetricTextTemplate(pointsCount, getString(R.string.label_view_points).toLowerCase()));
        stories.setText(makeUserMetricTextTemplate(storiesCount, getString(R.string.label_view_stories).toLowerCase()));
    }

    private CharSequence makeUserMetricTextTemplate(final String count, final String label) {
        final SpannableString spannableString = new SpannableString(count.concat("\n").concat(label));
        spannableString.setSpan(new RelativeSizeSpan(2.1f), 0, count.length(), 0);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, count.length(), 0);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, count.length(), 0);
        return spannableString;
    }

    private int getIntegerValue(Integer value) {
        return value != null ? value : 0;
    }

    private void setupPagerWithUser(User user) {
        NavigationItem storiesItem = new NavigationItem(StoriesListFragment.newInstance(user), getString(R.string.profile_my_stories));
        NavigationItem rankingItem = new NavigationItem(RankingFragment.newInstance(user), getString(R.string.profile_ranking));

        NavigationAdapter navigationAdapter = new NavigationAdapter(getChildFragmentManager(), storiesItem, rankingItem);
        pager.setAdapter(navigationAdapter);
        pager.setOffscreenPageLimit(2);
        tabs.setupWithViewPager(pager);

        checkRankingAction();
    }

    private void checkRankingAction() {
        String action = getActivity().getIntent().getAction();
        if (action != null && action.equals(ProfileActivity.ACTION_DISPLAY_RANKING)) {
            pager.setCurrentItem(RANKING_POSITION);
        }
    }

    private View.OnClickListener onLogoutClickListener = view -> logout();

    private View.OnClickListener onEditClickListener = view -> {
        if (onEditProfileListener != null) {
            onEditProfileListener.onEditProfile(user);
        }
    };

    private View.OnClickListener onPictureClickListener = view -> {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.message_question_profile_picture)
                .setNegativeButton(R.string.cancel_dialog_button, null)
                .setPositiveButton(R.string.confirm_neutral_dialog_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mediaSelector.selectImage(ProfileFragment.this);
                    }
                })
                .show();
    };

    private MediaSelector.OnLoadLocalMediaListener onLoadLocalMediaListener = new MediaSelector.OnLoadLocalMediaListener() {
        @Override
        public void onLoadLocalImage(Uri uri) {
            LocalMedia localMedia = new LocalMedia(uri);
            localMedia.setType(Media.Type.Picture);
            transferMedia(getContext(), localMedia);
        }

        @Override
        public void onLoadLocalVideo(Uri uri) { }

        @Override
        public void onLoadFile(Uri uri) { }

        @Override
        public void onLoadAudio(Uri uri, int duration) { }

        private void transferMedia(Context context, LocalMedia localMedia) {
            if (firebaseImageTransferListenerAdapter == null)
                return;

            firebaseImageTransferListenerAdapter.onStart();
            try {
                TransferManager transferManager = new TransferManager(context);
                transferManager.transferMedia(localMedia, "user", new TransferListenerAdapter(context, localMedia) {
                    @Override
                    public void onTransferFinished(Media media) {
                        super.onTransferFinished(media);
                        firebaseImageTransferListenerAdapter.onTransferFinished(media);
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        super.onError(id, ex);
                        firebaseImageTransferListenerAdapter.onError(id, ex);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                firebaseImageTransferListenerAdapter.onTransferFailed();
            }
        }
    };

    private void displayPictureError() {
        Toast.makeText(getContext(), R.string.error_image_upload, Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        UserManager.logout(getContext());
        UserManager.startLoginFlow(getContext());
        getActivity().finish();
    }

}
