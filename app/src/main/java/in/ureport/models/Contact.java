package in.ureport.models;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class Contact {

    private String name;

    private String phoneNumber;

    public Contact(String name) {
        this.name = name;
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
