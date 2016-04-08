package in.ureport.helpers;

import android.content.Context;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;

import in.ureport.R;
import in.ureport.models.LocalMedia;
import in.ureport.models.Media;

/**
 * Created by johncordeiro on 20/08/15.
 */
public abstract class TransferListenerAdapter implements TransferListener {

    private static final String TAG = "TransferListenerAdapter";

    private static final String URL = "https://%1$s.s3.amazonaws.com/%2$s";

    private String filename;
    private String bucket;

    private LocalMedia localMedia;

    private TransferListenerAdapter(Context context) {
        this.filename = "file";
        this.bucket = context.getString(R.string.amazon_s3_bucket_id);
    }

    public TransferListenerAdapter(Context context, LocalMedia localMedia) {
        this(context);
        this.localMedia = localMedia;
    }

    @Override
    public final void onStateChanged(int id, TransferState state) {
        Log.i(TAG, "onStateChanged: id: " + id + " state: " + state + " filename: " + filename);
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
        Log.e(TAG, "Uploading error! Transfer ID: " + id, ex);
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
