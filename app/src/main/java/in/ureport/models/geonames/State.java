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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state = (State) o;
        return name.equals(state.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
