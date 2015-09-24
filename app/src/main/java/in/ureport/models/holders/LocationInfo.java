package in.ureport.models.holders;

import java.util.List;

import in.ureport.models.geonames.Location;

/**
 * Created by johncordeiro on 24/09/15.
 */
public class LocationInfo {

    private List<Location> states;

    private List<Location> districts;

    public LocationInfo() {
    }

    public LocationInfo(List<Location> states, List<Location> districts) {
        this.states = states;
        this.districts = districts;
    }

    public List<Location> getStates() {
        return states;
    }

    public void setStates(List<Location> states) {
        this.states = states;
    }

    public List<Location> getDistricts() {
        return districts;
    }

    public void setDistricts(List<Location> districts) {
        this.districts = districts;
    }
}
