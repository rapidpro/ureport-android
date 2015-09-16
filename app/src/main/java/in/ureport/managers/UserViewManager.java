package in.ureport.managers;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import in.ureport.R;
import in.ureport.listener.OnCloseDialogListener;

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
    }

}
