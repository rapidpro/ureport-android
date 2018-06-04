package in.ureport.tasks;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.view.View;

import in.ureport.R;
import in.ureport.models.News;
import in.ureport.models.Story;
import in.ureport.tasks.common.ProgressTask;

/**
 * Created by johncordeiro on 02/10/15.
 */
public abstract class ShareViewTask<T> extends ProgressTask<Void, Void, Void> {

    protected Fragment fragment;
    private T object;

    private @StringRes int shareTitleId;
    private View view;

    public ShareViewTask(Fragment fragment, T object, @StringRes int shareTitleId) {
        super(fragment.getActivity(), R.string.load_message_wait);
        this.fragment = fragment;
        this.object = object;
        this.shareTitleId = shareTitleId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        view = createViewForObject(object);
    }

    protected abstract View createViewForObject(T object);

    @Override
    protected Void doInBackground(Void... params) {
        StringBuilder sharableTextBuilder = new StringBuilder();
        if (object instanceof Story) {
            Story story = ((Story) object);
            sharableTextBuilder
                    .append(story.getContent());

            if (story.getMedias() != null)
                sharableTextBuilder
                        .append("\n\n")
                        .append(story.getCover().getUrl());
        } else if (object instanceof News) {
            News news = ((News) object);
            sharableTextBuilder
                    .append(news.getTitle())
                    .append("\n\n")
                    .append(news.getSummary());

            if (news.getImages() != null && !news.getImages().isEmpty())
                sharableTextBuilder
                        .append("\n\n")
                        .append(news.getImages().get(0));
        }
        shareContent(fragment.getActivity(), fragment.getString(shareTitleId), sharableTextBuilder.toString());
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (view != null) view.setDrawingCacheEnabled(false);
    }

    private void shareContent(Activity activity, String title, String sharableText) {
        Intent sharingIntent = ShareCompat.IntentBuilder
                .from(activity)
                .setType("text/plain")
                .setText(sharableText)
                .getIntent();
        if (sharingIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(Intent.createChooser(sharingIntent, title));
        }
    }

}
