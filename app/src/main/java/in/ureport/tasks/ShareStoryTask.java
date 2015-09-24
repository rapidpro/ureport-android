package in.ureport.tasks;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;

import java.io.File;

import br.com.ilhasoft.support.tool.ShareManager;
import br.com.ilhasoft.support.tool.UnitConverter;
import br.com.ilhasoft.support.tool.bitmap.ImageStorage;
import in.ureport.R;
import in.ureport.models.Story;
import in.ureport.views.holders.StoryItemViewHolder;

/**
 * Created by johncordeiro on 8/3/15.
 */
public class ShareStoryTask extends AsyncTask<Void, Void, Void> {

    private static final String filename = "ureport_story";

    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;

    private Fragment fragment;
    private Story story;

    private ProgressDialog progress;
    private View view;

    public ShareStoryTask(Fragment fragment, Story story) {
        this.fragment = fragment;
        this.story = story;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        createViewForStory();
        progress = ProgressDialog.show(fragment.getActivity(), null, fragment.getString(R.string.load_message_wait, true, false));
    }

    private void createViewForStory() {
        LayoutInflater inflater = LayoutInflater.from(fragment.getContext());
        StoryItemViewHolder itemViewHolder = new StoryItemViewHolder(inflater.inflate(R.layout.item_story, null), null, null);
        itemViewHolder.bind(story);
        itemViewHolder.bindInfo(R.string.story_share_info);
        view = itemViewHolder.itemView;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Bitmap bitmap = drawBitmap();

        ImageStorage imageStorage = new ImageStorage();
        File file = imageStorage.saveBitmapToJpg(bitmap, filename);

        ShareManager shareManager = new ShareManager(fragment.getActivity());
        shareManager.shareBinary(file, "image/jpeg", fragment.getString(R.string.title_share_story));
        return null;
    }

    @NonNull
    private Bitmap drawBitmap() {
        UnitConverter unitConverter = new UnitConverter(fragment.getContext());
        measureView((int)unitConverter.convertDpToPx(WIDTH));

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void measureView(int width) {
        view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
                , View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progress.dismiss();
        if(view != null) view.setDrawingCacheEnabled(false);
    }
}
