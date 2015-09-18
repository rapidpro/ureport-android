package in.ureport.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by johncordeiro on 19/07/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Contact {

    private String name;

    private String phoneNumber;

    public Contact(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
