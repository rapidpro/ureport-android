package in.ureport.db.business;

import com.activeandroid.query.Select;

import java.util.List;

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

    public List<Story> getAll() {
        return new Select().from(getTypeClass())
                .orderBy("createdDate DESC")
                .execute();
    }
}
