package in.ureport.models.geonames;

/**
 * Created by johncordeiro on 18/08/15.
 */
public class State {

    private String name;

    private String toponymName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToponymName() {
        return toponymName;
    }

    public void setToponymName(String toponymName) {
        this.toponymName = toponymName;
    }

    @Override
    public String toString() {
        return toponymName != null ? toponymName : name;
    }
}
