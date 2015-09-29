package in.ureport.models.rapidpro;

/**
 * Created by johncordeiro on 29/09/15.
 */
public class Field {

    private String key;

    private String label;

    private String value_type;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue_type() {
        return value_type;
    }

    public void setValue_type(String value_type) {
        this.value_type = value_type;
    }

    public Field() {
    }

    public Field(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Field field = (Field) o;
        return key.equals(field.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
