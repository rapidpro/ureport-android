package in.ureport.listener;

/**
 * Created by john-mac on 2/5/16.
 */
public interface OnPickMediaListener {

    void onPickFromCamera();
    void onPickFromGallery();
    void onPickVideo();
    void onPickFile();
    void onPickAudioRecord();
    void onPickYoutubeLink();

}
