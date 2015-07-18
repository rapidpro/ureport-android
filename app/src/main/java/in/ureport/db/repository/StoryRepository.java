package in.ureport.db.repository;

import java.util.List;

import br.com.ilhasoft.support.db.repository.AbstractRepository;
import in.ureport.models.Story;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/14/15.
 */
public interface StoryRepository extends AbstractRepository<Story> {

    List<Story> getStoryByUser(User user);

}
