package in.ureport.helpers;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by john-mac on 3/30/16.
 */
public class IOHelper {

    public static String loadJSONFromAsset(Context context, String filename) {
        try {
            InputStream inputStream = context.getAssets().open(filename);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            return new String(buffer, "UTF-8");
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

}
