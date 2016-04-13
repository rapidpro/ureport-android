package in.ureport.managers;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.ilhasoft.support.tool.bitmap.IOManager;
import in.ureport.R;
import in.ureport.helpers.TransferListenerAdapter;
import in.ureport.models.LocalMedia;
import in.ureport.models.Media;
import in.ureport.tasks.CompressFileTask;
import in.ureport.tasks.CreateVideoThumbTask;

/**
 * Created by johncordeiro on 20/08/15.
 */
public class TransferManager {

    private static final String FILENAME = "%1$s/%2$s_%3$s";

    private Context context;
    private boolean transferFailed = false;
    private boolean transferCancelled = false;

    private static List<TransferListenerAdapter> transfersRetained;

    public TransferManager(Context context) {
        this.context = context;
        transfersRetained = new ArrayList<>();
    }

    public void transferMedia(LocalMedia media, String parent, final TransferListenerAdapter transferListener)
            throws URISyntaxException, IllegalStateException, IOException {
        transferFile(media.getType(), media.getPath(), parent, transferListener);
    }

    public void cancelTransfer() {
        AmazonServicesManager.getTransferUtility().cancelAllWithType(TransferType.ANY);
        transferCancelled = true;
    }

    public void transferMedias(final List<Media> mediasToUpload, final String parent
            , final OnTransferMediasListener onTransferMediasListener) throws URISyntaxException, IllegalStateException, IOException {
        final Map<LocalMedia, Media> mediasUploaded = new HashMap<>();

        transferFailed = false;
        transferCancelled = false;

        for (Media media : mediasToUpload) {
            if(transferFailed || transferCancelled) break;

            if(!(media instanceof LocalMedia)) {
                mediasUploaded.put(new LocalMedia(Uri.parse(media.getUrl())), media);
                continue;
            }

            final LocalMedia localMedia = (LocalMedia) media;
            TransferListenerAdapter transferListenerAdapter = createTransferListener(mediasToUpload, parent
                    , onTransferMediasListener, mediasUploaded, localMedia);
            transferFile(localMedia.getType(), localMedia.getPath(), parent, transferListenerAdapter);
        }
    }

    @NonNull
    private TransferListenerAdapter createTransferListener(final List<Media> mediasToUpload, final String parent
            , final OnTransferMediasListener onTransferMediasListener, final Map<LocalMedia, Media> mediasUploaded, final LocalMedia localMedia) {
        return new TransferListenerAdapter(context, localMedia) {
            @Override
            public void onTransferFinished(Media newMedia) {
                super.onTransferFinished(newMedia);
                localMedia.setId(getKey());

                if(newMedia.getType() == Media.Type.VideoPhone) {
                    transferVideoWithThumbnail(newMedia, localMedia, parent, mediasUploaded, mediasToUpload, onTransferMediasListener);
                } else {
                    finishTransfer(localMedia, newMedia, mediasToUpload, mediasUploaded, onTransferMediasListener);
                }
            }
            @Override
            public void onTransferWaitingNetwork() {
                super.onTransferWaitingNetwork();
                onTransferMediasListener.onWaitingConnection();
            }
            @Override
            public void onTransferFailed() {
                super.onTransferFailed();
                transferFailed = true;
                onTransferMediasListener.onFailed();
            }
        };
    }

    private void transferFile(Media.Type type, Uri uri, final String parent, final TransferListenerAdapter transferListener)
            throws URISyntaxException, IllegalStateException, IOException {
        IOManager ioManager = new IOManager(context);
        String filePath = ioManager.getFilePathForUri(uri);
        if (filePath == null) throw new IllegalStateException("File does not exists");

        File file = new File(filePath);
        if(type == Media.Type.Picture) {
            compressAndTransferImage(parent, transferListener, file);
        } else {
            transferFile(file, parent, transferListener);
        }
    }

    private void transferVideoWithThumbnail(Media media, LocalMedia localMedia, String parent
            , Map<LocalMedia, Media> mediasUploaded, List<Media> medias, OnTransferMediasListener onTransferMediasListener) {
        new CreateVideoThumbTask(context) {
            @Override
            protected void onPostExecute(File videoThumbFile) {
                super.onPostExecute(videoThumbFile);

                LocalMedia thumbnailLocalMedia = new LocalMedia();
                thumbnailLocalMedia.setType(Media.Type.Picture);

                TransferListenerAdapter thumbnailTransferListener = createVideoThumbnailListener(thumbnailLocalMedia);
                transferFile(videoThumbFile, parent, thumbnailTransferListener);
            }

            @NonNull
            private TransferListenerAdapter createVideoThumbnailListener(final LocalMedia thumbnailLocalMedia) {
                return new TransferListenerAdapter(context, thumbnailLocalMedia) {
                                @Override
                                public void onTransferFinished(Media videoThumbnail) {
                                    super.onTransferFinished(videoThumbnail);
                                    media.setThumbnail(videoThumbnail.getUrl());
                                    finishTransfer(localMedia, media, medias, mediasUploaded, onTransferMediasListener);
                                }
                            };
            }
        }.execute(localMedia.getPath());
    }

    private void compressAndTransferImage(final String parent, final TransferListenerAdapter transferListener, File file) {
        CompressFileTask compressFileTask = new CompressFileTask() {
            @Override
            protected void onPostExecute(File compressedFile) {
                super.onPostExecute(compressedFile);
                compressedFile = compressedFile != null ? compressedFile : file;
                TransferManager.this.transferFile(compressedFile, parent, transferListener);
            }
        };
        compressFileTask.execute(file);
    }

    private void transferFile(File compressedFile, String parent, TransferListenerAdapter transferListener) {
        String filename = getFilename(compressedFile, parent);
        transferListener.setFilename(filename);
        transferListener.setBucket(context.getString(R.string.amazon_s3_bucket_id));

        TransferObserver observer = AmazonServicesManager.getTransferUtility()
                .upload(AmazonServicesManager.BUCKET_ID, filename, compressedFile);
        transfersRetained.add(transferListener);
        observer.setTransferListener(transferListener);
    }

    private String getFilename(File compressedFile, String parent) {
        return String.format(FILENAME, parent, new Date().getTime(), compressedFile.getName());
    }

    private void finishTransfer(LocalMedia localMedia, Media newMedia, List<Media> mediasToUpload
            , Map<LocalMedia, Media> mediasUploaded, OnTransferMediasListener onTransferMediasListener) {
        mediasUploaded.put(localMedia, newMedia);

        if(mediasToUpload.size() == mediasUploaded.size() && !transferFailed) {
            onTransferMediasListener.onTransferMedias(mediasUploaded);
        }
    }

    public interface OnTransferMediasListener {
        void onTransferMedias(Map<LocalMedia, Media> medias);
        void onWaitingConnection();
        void onFailed();
    }

}
