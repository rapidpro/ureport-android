package in.ureport.helpers;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;

/**
 * Created by johncordeiro on 20/08/15.
 */
public abstract class TransferListenerAdapter implements TransferListener {

    private static final String URL = "https://s3.amazonaws.com/ureport-app/%1$s";

    private String filename;

    @Override
    public void onStateChanged(int id, TransferState state) {

    }

    @Override
    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

    }

    @Override
    public void onError(int id, Exception ex) {

    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUrl() {
        return String.format(URL, filename);
    }
}
