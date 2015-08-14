package in.ureport.db.repository;

import br.com.ilhasoft.support.db.repository.AbstractRepository;
import in.ureport.models.User;
import in.ureport.models.holders.Login;

/**
 * Created by johncordeiro on 7/9/15.
 */
public interface UserRepository extends AbstractRepository<User> {

    User get();

    User login(Login login);

}
