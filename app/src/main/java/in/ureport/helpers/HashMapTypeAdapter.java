package in.ureport.helpers;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by johncordeiro on 15/10/15.
 */
public class HashMapTypeAdapter implements JsonDeserializer<HashMap> {

    @Override
    public HashMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        HashMap<String, String> map;

        if(json.isJsonObject()) {
            Gson gson = new Gson();
            map = gson.fromJson(json, typeOfT);
        } else {
            String value = json.getAsString();
            map = new HashMap<>();
            map.put("key", value);
        }

        return map;
    }

}
