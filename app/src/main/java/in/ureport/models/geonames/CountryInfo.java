package in.ureport.models.geonames;

/**
 * Created by johncordeiro on 18/08/15.
 */
public class CountryInfo {

    private Long geonameId;

    public Long getGeonameId() {
        return geonameId;
    }

    public void setGeonameId(Long geonameId) {
        this.geonameId = geonameId;
    }

    @Override
    public String toString() {
        return "CountryInfo{" +
                "geonameId=" + geonameId +
                '}';
    }
}
