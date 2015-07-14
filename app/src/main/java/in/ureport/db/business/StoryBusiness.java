package in.ureport.db.business;

import br.com.ilhasoft.support.db.business.AbstractBusiness;
import in.ureport.db.repository.StoryRepository;
import in.ureport.models.Story;

/**
 * Created by johncordeiro on 7/14/15.
 */
public class StoryBusiness extends AbstractBusiness<Story> implements StoryRepository {

    public StoryBusiness() {
        super(Story.class);
    }
}
