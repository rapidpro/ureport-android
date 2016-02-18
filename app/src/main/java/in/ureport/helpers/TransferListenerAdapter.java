package in.ureport.helpers;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;

import in.ureport.models.Media;

/**
 * Created by johncordeiro on 20/08/15.
 */
public abstract class TransferListenerAdapter implements TransferListener {

    private static final String URL = "https://s3.amazonaws.com/ureport-app/%1$s";

    private String filename;

    private Media.Type type;
    private String name;

    public TransferListenerAdapter(Media.Type type) {
        this.type = type;
    }

    public TransferListenerAdapter(Media.Type type, String name) {
        this(type);
        this.name = name;
    }

    @Override
    public final void onStateChanged(int id, TransferState state) {
        if(state == TransferState.COMPLETED) {
            onTransferFinished(new Media(getKey(), getUrl(), type, name));
        }
    }

    @Override
    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

    }

    @Override
    public void onError(int id, Exception ex) {

    }

    public void onTransferFinished(Media media) {
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUrl() {
        return String.format(URL, filename);
    }

    public String getKey() {
        return filename;
    }
}
