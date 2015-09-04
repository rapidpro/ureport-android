package in.ureport.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

import br.com.ilhasoft.support.tool.bitmap.BitmapCompressor;

/**
 * Created by johncordeiro on 04/09/15.
 */
public class CompressFileTask extends AsyncTask<File, Void, File> {

    private static final String TAG = "CompressFileTask";

    @Override
    protected File doInBackground(File... params) {
        try {
            File file = params[0];

            BitmapCompressor bitmapCompressor = new BitmapCompressor();
            return bitmapCompressor.compressFile(file);
        } catch(Exception exception) {
            Log.e(TAG, "doInBackground ");
        }
        return null;
    }
}
