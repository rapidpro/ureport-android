package in.ureport.managers;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.net.URISyntaxException;

/**
 * Created by johncordeiro on 20/08/15.
 */
public class IOManager {

    private static final String EXTERNAL_STORAGE_AUTHORITY = "com.android.externalstorage.documents";
    private static final String DOWNLOAD_DOCUMENTS_AUTHORITY = "com.android.providers.downloads.documents";
    private static final String MEDIA_DOCUMENT_AUTHORITY = "com.android.providers.media.documents";
    public static final String DOWNLOAD_DOCUMENTS_URI_PREFIX = "content://downloads/public_downloads";

    private Context context;

    public IOManager(Context context) {
        this.context = context;
    }

    @SuppressLint("NewApi")
    public String getFilePathForUri(Uri uri) throws URISyntaxException {
        boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;

        if (needToCheckUri && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                return getPathForExternalStorageDocument(uri);
            } else if (isDownloadsDocument(uri)) {
                uri = getUriForDownloadsDocument(uri);
            } else if (isMediaDocument(uri)) {
                final String documentId = DocumentsContract.getDocumentId(uri);
                final String[] split = documentId.split(":");
                final String type = split[0];
                uri = getUriByType(uri, type);
                selection = "_id=?";
                selectionArgs = new String[] { split[1] };
            }
        }
        return getPathByProviderQuery(uri, selection, selectionArgs);
    }

    private Uri getUriByType(Uri uri, String type) {
        switch (type) {
            case "image":
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                break;
            case "video":
                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                break;
            case "audio":
                uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                break;
        }
        return uri;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private Uri getUriForDownloadsDocument(Uri uri) {
        final String id = DocumentsContract.getDocumentId(uri);
        return ContentUris.withAppendedId(Uri.parse(DOWNLOAD_DOCUMENTS_URI_PREFIX), Long.valueOf(id));
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @NonNull
    private String getPathForExternalStorageDocument(Uri uri) {
        final String documentId = DocumentsContract.getDocumentId(uri);
        final String[] split = documentId.split(":");
        return Environment.getExternalStorageDirectory() + "/" + split[1];
    }

    @Nullable
    private String getPathByProviderQuery(Uri uri, String selection, String[] selectionArgs) {
        if (uri.getScheme().equalsIgnoreCase("file")) {
            return uri.getPath();
        } else if (uri.getScheme().equalsIgnoreCase("content")) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst())
                    return cursor.getString(dataIndex);
            } catch (Exception ignored) {
                return null;
            } finally {
                if (cursor != null) cursor.close();
            }
        }
        return null;
    }

    private boolean isExternalStorageDocument(Uri uri) {
        return uri.getAuthority().equals(EXTERNAL_STORAGE_AUTHORITY);
    }

    private boolean isDownloadsDocument(Uri uri) {
        return uri.getAuthority().equals(DOWNLOAD_DOCUMENTS_AUTHORITY);
    }

    private boolean isMediaDocument(Uri uri) {
        return uri.getAuthority().equals(MEDIA_DOCUMENT_AUTHORITY);
    }

}
