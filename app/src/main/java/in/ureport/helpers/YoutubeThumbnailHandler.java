package in.ureport.helpers;

/**
 * Created by johncordeiro on 28/10/15.
 */
public class YoutubeThumbnailHandler {

    private static final String THUMBNAIL_BASE_URL = "http://img.youtube.com/vi/%1$s/%2$s.jpg";

    public enum ThumbnailSizeClass {
        Default("default"),
        HighQuality("hqdefault"),
        MediumQuality("mqdefault"),
        StandardQuality("sddefault"),
        MaximumResolution("maxresdefault");

        final String sizeClass;

        ThumbnailSizeClass(String sizeClass) {
            this.sizeClass = sizeClass;
        }

        public String getSizeClass() {
            return sizeClass;
        }

    }

    public String getThumbnailUrlFromVideo(String videoId, ThumbnailSizeClass thumbnailSizeClass) {
        return String.format(THUMBNAIL_BASE_URL, videoId, thumbnailSizeClass.getSizeClass());
    }

}
