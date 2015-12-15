package in.ureport.models.geonames;

/**
 * Created by johncordeiro on 18/08/15.
 */
public class Location {

    private String boundary;

    private String name;

    private String toponymName;

    private String parent;

    public String getBoundary() {
        return boundary;
    }

    public void setBoundary(String boundary) {
        this.boundary = boundary;
    }

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

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Location() {
    }

    public Location(String name, String toponymName) {
        this.name = name;
        this.toponymName = toponymName;
    }

    @Override
    public String toString() {
        return name != null ? name : toponymName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;
        return name.equals(location.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
