package in.ureport.db.repository;

import java.util.List;

import br.com.ilhasoft.support.db.repository.AbstractRepository;
import in.ureport.models.User;
import in.ureport.models.holders.Login;

/**
 * Created by johncordeiro on 7/9/15.
 */
public interface UserRepository extends AbstractRepository<User> {

    User login(Login login);

    List<User> getAllOrdered();

    List<User> getAllExcluding(Long id);

}
