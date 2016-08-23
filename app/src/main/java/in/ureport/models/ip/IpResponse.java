package in.ureport.models.ip;

/**
 * Created by john-mac on 7/21/16.
 */
public class IpResponse {

    private String as;

    private String city;

    private String countryCode;

    private String status;

    public String getAs() {
        return as;
    }

    public IpResponse setAs(String as) {
        this.as = as;
        return this;
    }

    public String getCity() {
        return city;
    }

    public IpResponse setCity(String city) {
        this.city = city;
        return this;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public IpResponse setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public IpResponse setStatus(String status) {
        this.status = status;
        return this;
    }
}
