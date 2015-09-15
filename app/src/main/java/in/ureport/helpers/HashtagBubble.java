package in.ureport.helpers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import in.ureport.R;

/**
 * Created by dev on 06/02/2015.
 */
public class HashtagBubble<T> {

    private static final String TAG_FORMAT = "%1$s. %2$s";
    private static final int DEFAULT_MAX_QUANTITY = 20;

    private TextView textView;
    private BuildSpannableAsyncTask<T> spannableAsyncTask;

    private int maxQuantity = DEFAULT_MAX_QUANTITY;

    public HashtagBubble(TextView textView) {
        this.textView = textView;
    }

    public void setList(List<T> hashtagList) {
        if (spannableAsyncTask != null) spannableAsyncTask.cancel(true);

        spannableAsyncTask = new BuildSpannableAsyncTask<>(textView, hashtagList);
        spannableAsyncTask.execute();
    }

    private class BuildSpannableAsyncTask<T> extends AsyncTask<Void, Void, SpannableStringBuilder> {

        TextView textView;
        List<T> hashtagList;

        private BuildSpannableAsyncTask(TextView textView, List<T> hashtagList) {
            this.textView = textView;
            this.hashtagList = hashtagList;
        }

        @Override
        protected SpannableStringBuilder doInBackground(Void... params) {
            final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            if (hashtagList != null)
                for (int i = 0; i < hashtagList.size() && i < maxQuantity; i++) {
                    Object hashtagObject = hashtagList.get(i);

                    String hashtag = String.format(TAG_FORMAT, i+1, hashtagObject.toString());
                    TextView textView = createTextView(hashtag);
                    BitmapDrawable bitmapDrawable = convertViewToDrawable(textView);

                    spannableStringBuilder.append(hashtag + " ");
                    spannableStringBuilder.setSpan(new ImageSpan(bitmapDrawable)
                            , spannableStringBuilder.length() - (hashtag.length() + 1)
                            , spannableStringBuilder.length() - 1
                            , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            return spannableStringBuilder;
        }

        @Override
        protected void onPostExecute(SpannableStringBuilder sb) {
            textView.setText(sb);
        }

        private TextView createTextView(String hashtag) {
            TextView createdTextView = (TextView) View.inflate(textView.getContext(), R.layout.item_hashtag, null);
            createdTextView.setText(hashtag);
            return createdTextView;
        }

    }

    private static BitmapDrawable convertViewToDrawable(View view) {
        measureView(view);
        drawBitmap(view);
        return createBitmapDrawable(view);
    }

    @NonNull
    private static BitmapDrawable createBitmapDrawable(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap cacheBmp = view.getDrawingCache();
        Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
        view.destroyDrawingCache();

        BitmapDrawable bitmapDrawable = new BitmapDrawable(viewBmp);
        bitmapDrawable.setBounds(0, 0, viewBmp.getWidth(), viewBmp.getHeight());
        return bitmapDrawable;
    }

    private static void measureView(View view) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    private static void drawBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.translate(-view.getScrollX(), -view.getScrollY());
        view.draw(canvas);
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }
}
