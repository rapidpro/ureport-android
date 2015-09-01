package in.ureport.managers;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Date;

import in.ureport.helpers.TransferListenerAdapter;

/**
 * Created by johncordeiro on 20/08/15.
 */
public class TransferManager {

    private static final String FILENAME = "%1$s/%2$s%3$s";
    private Context context;

    public TransferManager(Context context) {
        this.context = context;
    }

    @NonNull
    public void transferFile(Uri uri, String parent, TransferListenerAdapter transferListener)
            throws URISyntaxException, IllegalStateException {
        IOManager ioManager = new IOManager(context);
        String filePath = ioManager.getFilePathForUri(uri);

        if (filePath == null) throw new IllegalStateException("File does not exists");

        File file = new File(filePath);
        String filename = String.format(FILENAME, parent, new Date().getTime(), file.getName());
        transferListener.setFilename(filename);

        TransferObserver observer = AmazonServicesManager.getTransferUtility()
                .upload(AmazonServicesManager.BUCKET_ID, filename, file);
        observer.setTransferListener(transferListener);
    }

}
