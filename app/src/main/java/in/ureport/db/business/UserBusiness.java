package in.ureport.db.business;

import com.activeandroid.query.Select;

import java.util.List;

import br.com.ilhasoft.support.db.business.AbstractBusiness;
import in.ureport.db.repository.UserRepository;
import in.ureport.models.User;
import in.ureport.models.holders.Login;

/**
 * Created by johncordeiro on 7/9/15.
 */
public class UserBusiness extends AbstractBusiness<User> implements UserRepository {

    public UserBusiness() {
        super(User.class);
    }

    @Override
    public User get() {
        return new Select().from(getTypeClass()).executeSingle();
    }

    @Override
    public User login(Login login) {
        return new Select().from(getTypeClass())
                    .where("username = ? AND password = ?", login.getUsername(), login.getPassword())
                    .executeSingle();
    }
}
