package in.ureport.helpers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by johncordeiro on 18/08/15.
 */
public class GsonDateTypeAdapter implements JsonDeserializer<Date>, JsonSerializer<Date> {

    private static final String TAG = "GsonDateDeserializer";

    String [] DATE_FORMATS = new String[] {
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

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        try {
            String date = new SimpleDateFormat(DATE_FORMATS[0], Locale.US).format(src);
            return new JsonPrimitive(date);
        } catch(Exception exception) {
            return null;
        }
    }

    public String serializeDate(Date date) {
        try {
            return new SimpleDateFormat(DATE_FORMATS[0], Locale.US).format(date);
        } catch(Exception ignored) {}
        return date.toString();
    }
}
