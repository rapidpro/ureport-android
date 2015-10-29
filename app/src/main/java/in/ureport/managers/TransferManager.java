package in.ureport.managers;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.ureport.helpers.TransferListenerAdapter;
import in.ureport.models.LocalMedia;
import in.ureport.models.Media;
import in.ureport.tasks.CompressFileTask;

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
    public void transferFile(Uri uri, final String parent, final TransferListenerAdapter transferListener)
            throws URISyntaxException, IllegalStateException, IOException {
        IOManager ioManager = new IOManager(context);
        String filePath = ioManager.getFilePathForUri(uri);

        if (filePath == null) throw new IllegalStateException("File does not exists");

        File file = new File(filePath);

        CompressFileTask compressFileTask = new CompressFileTask() {
            @Override
            protected void onPostExecute(File compressedFile) {
                super.onPostExecute(compressedFile);

                String filename = String.format(FILENAME, parent, new Date().getTime(), compressedFile.getName());
                transferListener.setFilename(filename);

                TransferObserver observer = AmazonServicesManager.getTransferUtility()
                        .upload(AmazonServicesManager.BUCKET_ID, filename, compressedFile);
                observer.setTransferListener(transferListener);
            }
        };
        compressFileTask.execute(file);
    }

    public void transferMedia(LocalMedia media, String parent, final TransferListenerAdapter transferListener)
            throws URISyntaxException, IllegalStateException, IOException {
        transferFile(media.getPath(), parent, transferListener);
    }

    public void transferMedias(final List<Media> medias, String parent, final OnTransferMediasListener onTransferMediasListener)
            throws URISyntaxException, IllegalStateException, IOException {
        final List<Media> mediasUploaded = new ArrayList<>();

        for (Media media : medias) {
            if(!(media instanceof LocalMedia)) {
                mediasUploaded.add(media);
                continue;
            }

            final LocalMedia localMedia = (LocalMedia) media;
            transferFile(localMedia.getPath(), parent, new TransferListenerAdapter() {
                @Override
                public void onTransferFinished(Media media) {
                    super.onTransferFinished(media);
                    localMedia.setId(getKey());
                    mediasUploaded.add(media);

                    if(medias.size() == mediasUploaded.size()) {
                        onTransferMediasListener.onTransferMedias(mediasUploaded);
                    }
                }
            });
        }
    }

    public interface OnTransferMediasListener {
        void onTransferMedias(List<Media> medias);
    }

}
