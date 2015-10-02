package in.ureport.tasks;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.View;

import java.io.File;

import br.com.ilhasoft.support.tool.ShareManager;
import br.com.ilhasoft.support.tool.UnitConverter;
import br.com.ilhasoft.support.tool.bitmap.ImageStorage;
import in.ureport.R;
import in.ureport.tasks.common.ProgressTask;

/**
 * Created by johncordeiro on 02/10/15.
 */
public abstract class ShareViewTask<T> extends ProgressTask<Void, Void, Void> {

    private static final String filename = "ureport_share";

    private static final int WIDTH = 400;

    protected Fragment fragment;
    private T object;

    private @StringRes int shareTitleId;
    private View view;

    public ShareViewTask(Fragment fragment, T object, @StringRes int shareTitleId) {
        super(fragment.getActivity(), R.string.load_message_wait);
        this.fragment = fragment;
        this.object = object;
        this.shareTitleId = R.string.title_share_story;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        view = createViewForObject(object);
    }

    protected abstract View createViewForObject(T object);

    @Override
    protected Void doInBackground(Void... params) {
        Bitmap bitmap = drawBitmap();

        ImageStorage imageStorage = new ImageStorage();
        File file = imageStorage.saveBitmapToJpg(bitmap, filename);

        ShareManager shareManager = new ShareManager(fragment.getActivity());
        shareManager.shareBinary(file, "image/jpeg", fragment.getString(shareTitleId));
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
                , View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(view != null) view.setDrawingCacheEnabled(false);
    }

}
