package in.ureport.models.holders;

/**
 * Created by johncordeiro on 7/9/15.
 */
public class Login {

    private String username;

    private String password;

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
