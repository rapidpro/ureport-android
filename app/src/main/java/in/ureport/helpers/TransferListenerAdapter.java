package in.ureport.helpers;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;

import in.ureport.models.LocalMedia;
import in.ureport.models.Media;

/**
 * Created by johncordeiro on 20/08/15.
 */
public abstract class TransferListenerAdapter implements TransferListener {

    private static final String URL = "https://s3.amazonaws.com/%1$s/%2$s";

    private String filename;
    private String bucket;

    private LocalMedia localMedia;

    private TransferListenerAdapter() {
        this.filename = "file";
        this.bucket = "ureport-app";
    }

    public TransferListenerAdapter(LocalMedia localMedia) {
        this();
        this.localMedia = localMedia;
    }

    @Override
    public final void onStateChanged(int id, TransferState state) {
        if(state == TransferState.COMPLETED) {
            Media media = new Media(localMedia);
            media.setId(getKey());
            media.setUrl(getUrl());

            onTransferFinished(media);
        } else if(state == TransferState.WAITING_FOR_NETWORK) {
            onTransferWaitingNetwork();
        } else if (state == TransferState.FAILED) {
            onTransferFailed();
        }
    }

    @Override
    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

    }

    @Override
    public void onError(int id, Exception ex) {

    }

    public void onTransferFinished(Media media) {}

    public void onTransferFailed(){}

    public void onTransferWaitingNetwork(){}

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getUrl() {
        return String.format(URL, bucket, filename);
    }

    public String getKey() {
        return filename;
    }
}
