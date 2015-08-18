package in.ureport.helpers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by johncordeiro on 18/08/15.
 */
public class GsonDateDeserializer implements JsonDeserializer<Date> {

    private static final String TAG = "GsonDateDeserializer";

    private static final String [] DATE_FORMATS = new String[] {
            "yyyy-MM-dd'T'HH:mm:ss.SS'Z'",
            "dd-MM-yyyy HH:mm"
    };

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        for (String dateFormat : DATE_FORMATS) {
            try {
                return new SimpleDateFormat(dateFormat, Locale.US).parse(json.getAsString());
            } catch (ParseException ignored) {}
        }
        return null;
    }
}
