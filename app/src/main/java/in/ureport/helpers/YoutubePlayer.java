package in.ureport.helpers;

import android.app.Activity;
import android.content.Intent;

import com.google.android.youtube.player.YouTubeStandalonePlayer;

import in.ureport.R;
import in.ureport.models.Media;

/**
 * Created by johncordeiro on 29/10/15.
 */
public class YoutubePlayer {

    private static final String YOUTUBE_FORMAT_URL = "http://youtu.be/%1$s";

    private Activity activity;

    public YoutubePlayer(Activity activity) {
        this.activity = activity;
    }

    public void playVideoMedia(Media media) {
        Intent intent = YouTubeStandalonePlayer.createVideoIntent(activity
                , activity.getString(R.string.youtube_api_key), media.getId(), 0, true, false);
        activity.startActivity(intent);
    }

    public String getYoutubeKey() {
        return activity.getString(R.string.youtube_api_key);
    }

    public static String getYoutubeLinkById(String id) {
        return String.format(YOUTUBE_FORMAT_URL, id);
    }

}
