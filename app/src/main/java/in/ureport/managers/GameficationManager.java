package in.ureport.managers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import in.ureport.R;
import in.ureport.activities.ProfileActivity;
import in.ureport.listener.OnCloseDialogListener;

/**
 * Created by johncordeiro on 7/24/15.
 */
public class GameficationManager {

    public static final int STORIES_POINTS = 5;
    public static final int CONTRIBUTION_POINTS = 1;

    private Context context;

    public GameficationManager(Context context) {
        this.context = context;
    }

    public void showGameficationAlert(final OnCloseDialogListener onCloseDialogListener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.view_points_earning, null);

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(customView)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        if(onCloseDialogListener != null)
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

        Button ranking = (Button) customView.findViewById(R.id.ranking);
        ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(context, ProfileActivity.class);
                profileIntent.setAction(ProfileActivity.ACTION_DISPLAY_RANKING);
                context.startActivity(profileIntent);
            }
        });
    }

}
