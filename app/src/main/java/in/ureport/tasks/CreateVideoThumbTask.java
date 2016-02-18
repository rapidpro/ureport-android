package in.ureport.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

import br.com.ilhasoft.support.tool.bitmap.BitmapCompressor;
import br.com.ilhasoft.support.tool.bitmap.BitmapHelper;

/**
 * Created by john-mac on 2/15/16.
 */
public class CreateVideoThumbTask extends AsyncTask<Uri, Void, File> {

    private static final String TAG = "CreateVideoThumb";

    private Context context;

    public CreateVideoThumbTask(Context context) {
        this.context = context;
    }

    @Override
    protected File doInBackground(Uri... params) {
        try {
            Uri uri = params[0];
            Bitmap bitmap = BitmapHelper.getThumbnailFromVideoUri(context, uri);

            File createdFile = BitmapHelper.createImageFile();

            BitmapCompressor compressor = new BitmapCompressor();
            return compressor.setBitmapToNewFileCompressed(bitmap, createdFile);
        } catch(Exception exception) {
            Log.e(TAG, "doInBackground: ", exception);
        }
        return null;
    }

}
