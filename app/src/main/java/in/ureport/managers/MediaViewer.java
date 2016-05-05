package in.ureport.managers;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.activities.MediaActivity;
import in.ureport.fragments.MediaViewFragment;
import in.ureport.fragments.RecordAudioFragment;
import in.ureport.helpers.YoutubePlayer;
import in.ureport.models.Media;
import in.ureport.views.adapters.MediaAdapter;

/**
 * Created by john-mac on 4/11/16.
 */
public class MediaViewer implements MediaAdapter.OnMediaViewListener {

    private static final String DOCUMENT_URL = "http://docs.google.com/gview?embedded=true&url=%1$s";

    private AppCompatActivity activity;

    public MediaViewer(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void viewMedia(Media media, ImageView imageView) {
        switch (media.getType()) {
            case Video:
                onYoutubeMediaView(media); break;
            case File:
                onFileMediaView(media); break;
            case Audio:
                onAudioMediaView(media); break;
            case VideoPhone:
                onVideoMediaView(media); break;
            default:
                onMediaView(media, imageView);
        }
    }

    private void onYoutubeMediaView(Media media) {
        try {
            YoutubePlayer youtubePlayer = new YoutubePlayer(activity);
            youtubePlayer.playVideoMedia(media);
        } catch(Exception exception) {
            Toast.makeText(activity, R.string.error_message_no_youtube, Toast.LENGTH_SHORT).show();
        }
    }

    public void onMediaView(Media media, ImageView mediaImageView) {
        Intent mediaViewIntent = new Intent(activity, MediaActivity.class);
        mediaViewIntent.putExtra(MediaActivity.EXTRA_MEDIA, media);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity
                , mediaImageView, activity.getString(R.string.transition_media));
        ActivityCompat.startActivity(activity, mediaViewIntent, options.toBundle());
    }

    @Override
    public void onMediaView(List<Media> medias, int position) {
        MediaViewFragment mediaViewFragment = MediaViewFragment.newInstance((ArrayList<Media>)medias, position);
        addFragment(mediaViewFragment);
    }

    @Override
    public void onVideoMediaView(Media media) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(media.getUrl()), "video/mp4");
        activity.startActivity(intent);
    }

    @Override
    public void onFileMediaView(Media media) {
        Intent viewFileIntent = new Intent(Intent.ACTION_VIEW);
        String url = media.getUrl();
        if(isGoogleDocsSupported(url))
            url = String.format(DOCUMENT_URL, media.getUrl());
        viewFileIntent.setData(Uri.parse(url));
        activity.startActivity(viewFileIntent);
    }

    private boolean isGoogleDocsSupported(String url) {
        return url.endsWith(".pdf") || url.endsWith(".doc") || url.endsWith(".docx")
                || url.endsWith(".ppt") || url.endsWith(".xls") || url.endsWith(".xlsx")
                || url.endsWith(".csv") || url.endsWith(".ods") || url.endsWith(".txt")
                || url.endsWith(".svg");
    }

    @Override
    public void onAudioMediaView(Media media) {
        RecordAudioFragment recordAudioFragment = RecordAudioFragment.newInstance(media);
        recordAudioFragment.show(activity.getSupportFragmentManager(), "recordAudioFragment");
    }

    private void addFragment(Fragment fragment) {
        activity.getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.content, fragment)
                .addToBackStack(null)
                .commit();
    }

}
