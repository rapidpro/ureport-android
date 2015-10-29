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

    private Activity activity;

    public YoutubePlayer(Activity activity) {
        this.activity = activity;
    }

    public void playVideoMedia(Media media) {
        Intent intent = YouTubeStandalonePlayer.createVideoIntent(activity
                , activity.getString(R.string.youtube_api_key), media.getId(), 0, true, false);
        activity.startActivity(intent);
    }

}
