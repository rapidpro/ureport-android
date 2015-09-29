package in.ureport.helpers;

import java.util.Comparator;

import in.ureport.models.rapidpro.Field;

/**
 * Created by johncordeiro on 29/09/15.
 */
public class FieldComparator implements Comparator<Field> {
    @Override
    public int compare(Field field1, Field field2) {
        return field1.getKey().compareTo(field2.getKey());
    }
}
