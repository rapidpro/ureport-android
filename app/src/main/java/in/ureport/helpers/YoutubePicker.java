package in.ureport.helpers;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.ureport.R;

/**
 * Created by johncordeiro on 28/10/15.
 */
public class YoutubePicker {

    private Context context;

    public YoutubePicker(Context context) {
        this.context = context;
    }

    public void pickVideoFromInput(final OnPickYoutubeVideoListener onPickYoutubeVideoListener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View container = inflater.inflate(R.layout.view_youtube_picker, null);
        final EditText editText = (EditText) container.findViewById(R.id.youtubeLink);

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.message_youtube_link))
                .setView(container)
                .setNegativeButton(R.string.cancel_dialog_button, null)
                .setPositiveButton(R.string.confirm_neutral_dialog_button, null)
                .create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String videoId = getVideoIdFromLink(editText.getText().toString());
                if (editText.getText().length() > 0 && videoId != null) {
                    onPickYoutubeVideoListener.onPickYoutubeVideo(videoId, editText.getText().toString());
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(context, R.string.error_empty_link, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getVideoIdFromLink(String youtubeLink) {
        String videoId = null;
        Pattern pattern = Pattern.compile(".*(?:youtu.be\\/|v\\/|u\\/\\w\\/|embed\\/|e\\/|watch\\?v=|watch\\?.*v=)([^#&\\?]*).*",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(youtubeLink);
        if (matcher.matches()) {
            videoId = matcher.group(1);
        }
        return videoId;
    }

    public interface OnPickYoutubeVideoListener {
        void onPickYoutubeVideo(String videoId, String videoUrl);
    }
}
