package in.ureport.models.holders;

/**
 * Created by ilhasoft on 7/9/15.
 */
public class Login {

    private String email;

    private String password;

    public Login(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
