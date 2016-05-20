package in.ureport.tasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

import br.com.ilhasoft.support.tool.bitmap.IOManager;
import br.com.ilhasoft.videoeditor.MediaController;

/**
 * Created by john-mac on 2/18/16.
 */
public class CompressVideoTask extends AsyncTask<Uri, Void, Uri> {

    private static final String TAG = "CompressVideoTask";

    private Context context;

    public CompressVideoTask(Context context) {
        this.context = context;
    }

    @Override
    protected Uri doInBackground(Uri... params) {
        Uri uri = params[0];

        try {
            IOManager ioManager = new IOManager(context);
            String filePath = ioManager.getFilePathForUri(uri);

            File currentFile = new File(filePath);
            File newFile = ioManager.createVideoFilePath();

            float fileSizeInMb = ioManager.getFileSizeInMb(currentFile);
            if(fileSizeInMb > 0.7) {
                MediaController mediaController = new MediaController();
                boolean converted = mediaController.convertVideo(filePath, newFile.getAbsolutePath());
                if(converted) {
                    return Uri.fromFile(newFile);
                }
            }
        } catch(Exception exception) {
            Log.e(TAG, "doInBackground: ", exception);
        }
        return uri;
    }

    @Override
    protected void onPostExecute(Uri uri) {
        super.onPostExecute(uri);
    }
}
