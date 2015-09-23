package in.ureport.managers;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import in.ureport.R;
import in.ureport.helpers.ImageLoader;
import in.ureport.listener.OnCloseDialogListener;
import in.ureport.listener.OnUserStartChattingListener;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/22/15.
 */
public class UserViewManager {

    private Context context;

    public UserViewManager(Context context) {
        this.context = context;
    }

    public void showStoryPublishingWarning(final OnCloseDialogListener onCloseDialogListener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.view_story_publish_warning, null);

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(customView)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        if (onCloseDialogListener != null)
                            onCloseDialogListener.onCloseGamefication();
                    }
                })
                .create();
        alertDialog.show();

        Button confirm = (Button) customView.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
    }

    public void showUserInfo(final User user, final OnUserStartChattingListener listener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.view_user_info, null);

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(customView)
                .create();
        alertDialog.show();

        ImageView picture = (ImageView) customView.findViewById(R.id.picture);
        ImageLoader.loadPersonPictureToImageView(picture, user.getPicture());

        TextView nickname = (TextView) customView.findViewById(R.id.nickname);
        nickname.setText(user.getNickname());

        TextView stories = (TextView) customView.findViewById(R.id.stories);
        stories.setText(String.valueOf(getValue(user.getStories())));

        TextView points = (TextView) customView.findViewById(R.id.points);
        points.setText(String.valueOf(getValue(user.getPoints())));

        ImageButton close = (ImageButton) customView.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

        Button startChatting = (Button) customView.findViewById(R.id.startChatting);
        startChatting.setVisibility(!user.getKey().equals(UserManager.getUserId()) ? View.VISIBLE : View.GONE);
        startChatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onUserStartChatting(user);
            }
        });
    }

    private int getValue(Integer value) {
        return value != null ? value : 0;
    }

}
