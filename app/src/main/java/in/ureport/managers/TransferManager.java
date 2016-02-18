package in.ureport.managers;

import android.content.ContentResolver;
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

import br.com.ilhasoft.support.tool.bitmap.IOManager;
import in.ureport.helpers.TransferListenerAdapter;
import in.ureport.models.LocalMedia;
import in.ureport.models.Media;
import in.ureport.tasks.CompressFileTask;
import in.ureport.tasks.CreateVideoThumbTask;

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

        ContentResolver contentResolver = context.getContentResolver();
        String type = contentResolver.getType(uri);

        File file = new File(filePath);
        if(type == null || type.startsWith("image")) {
            compressAndTransferImage(parent, transferListener, file);
        } else {
            transferFile(file, parent, transferListener);
        }
    }

    public void transferMedia(LocalMedia media, String parent, final TransferListenerAdapter transferListener)
            throws URISyntaxException, IllegalStateException, IOException {
        transferFile(media.getPath(), parent, transferListener);
    }

    public void transferMedias(final List<Media> medias, final String parent, final OnTransferMediasListener onTransferMediasListener)
            throws URISyntaxException, IllegalStateException, IOException {
        final List<Media> mediasUploaded = new ArrayList<>();

        for (Media media : medias) {
            if(!(media instanceof LocalMedia)) {
                mediasUploaded.add(media);
                continue;
            }

            final LocalMedia localMedia = (LocalMedia) media;
            transferFile(localMedia.getPath(), parent, new TransferListenerAdapter(localMedia.getType(), localMedia.getName()) {
                @Override
                public void onTransferFinished(Media media) {
                    super.onTransferFinished(media);
                    localMedia.setId(getKey());

                    if(media.getType() == Media.Type.VideoPhone) {
                        transferVideoWithThumbnail(media, localMedia, parent, mediasUploaded, medias, onTransferMediasListener);
                    } else {
                        finishTransfer(media, medias, mediasUploaded, onTransferMediasListener);
                    }
                }
            });
        }
    }

    private void transferVideoWithThumbnail(Media media, LocalMedia localMedia, String parent
            , List<Media> mediasUploaded, List<Media> medias, OnTransferMediasListener onTransferMediasListener) {
        new CreateVideoThumbTask(context) {
            @Override
            protected void onPostExecute(File videoThumbFile) {
                super.onPostExecute(videoThumbFile);

                transferFile(videoThumbFile, parent, new TransferListenerAdapter(Media.Type.Picture) {
                    @Override
                    public void onTransferFinished(Media videoThumbnail) {
                        super.onTransferFinished(videoThumbnail);
                        media.setThumbnail(videoThumbnail.getUrl());
                        finishTransfer(media, medias, mediasUploaded, onTransferMediasListener);
                    }
                });
            }
        }.execute(localMedia.getPath());
    }

    private void compressAndTransferImage(final String parent, final TransferListenerAdapter transferListener, File file) {
        CompressFileTask compressFileTask = new CompressFileTask() {
            @Override
            protected void onPostExecute(File compressedFile) {
                super.onPostExecute(compressedFile);
                TransferManager.this.transferFile(compressedFile, parent, transferListener);
            }
        };
        compressFileTask.execute(file);
    }

    private void transferFile(File compressedFile, String parent, TransferListenerAdapter transferListener) {
        String filename = getFilename(compressedFile, parent);
        transferListener.setFilename(filename);

        TransferObserver observer = AmazonServicesManager.getTransferUtility()
                .upload(AmazonServicesManager.BUCKET_ID, filename, compressedFile);
        observer.setTransferListener(transferListener);
    }

    private String getFilename(File compressedFile, String parent) {
        return String.format(FILENAME, parent, new Date().getTime(), compressedFile.getName());
    }

    private void finishTransfer(Media media, List<Media> medias, List<Media> mediasUploaded
            , OnTransferMediasListener onTransferMediasListener) {
        mediasUploaded.add(media);

        if(medias.size() == mediasUploaded.size()) {
            onTransferMediasListener.onTransferMedias(mediasUploaded);
        }
    }

    public interface OnTransferMediasListener {
        void onTransferMedias(List<Media> medias);
    }

}
