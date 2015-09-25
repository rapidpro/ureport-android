package in.ureport.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import in.ureport.R;
import in.ureport.listener.OnMediaSelectedListener;

/**
 * Created by johncordeiro on 25/09/15.
 */
public class MediaSelector {

    public static final int POSITION_GALLERY = 0;
    public static final int POSITION_CAMERA = 1;

    private Context context;

    public MediaSelector(Context context) {
        this.context = context;
    }

    public void selectMedia(final OnMediaSelectedListener listener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.create_story_title_media_source)
                .setItems(R.array.create_story_media_sources, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onMediaSelected(which);
                    }
                })
                .create();
        alertDialog.show();
    }

}
