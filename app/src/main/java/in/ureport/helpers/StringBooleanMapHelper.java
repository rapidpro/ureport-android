package in.ureport.helpers;

import java.util.HashMap;
import java.util.Map;

public class StringBooleanMapHelper {

    private Map<String, Boolean> map;

    public StringBooleanMapHelper() {
        map = new HashMap<>();
    }

    public void add(final String key, final Boolean value) {
        map.put(key, value);
    }

    public boolean check(final String key) {
        final Boolean value = map.get(key);
        return value != null && value;
    }

    public boolean containsKey(final String storyKey) {
        return map.containsKey(storyKey);
    }

}
