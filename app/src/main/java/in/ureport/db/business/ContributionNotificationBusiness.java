package in.ureport.db.business;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import br.com.ilhasoft.support.db.business.AbstractBusiness;
import in.ureport.db.repository.ContributionNotificationRepository;
import in.ureport.models.db.ContributionNotification;

/**
 * Created by johncordeiro on 21/08/15.
 */
public class ContributionNotificationBusiness extends AbstractBusiness<ContributionNotification> implements ContributionNotificationRepository {

    public ContributionNotificationBusiness() {
        super(ContributionNotification.class);
    }

    @Override
    public List<ContributionNotification> getAllOrderedByDate() {
        return new Select().from(getTypeClass())
                .orderBy("date DESC")
                .execute();
    }

    @Override
    public List<ContributionNotification> deleteByStoryId(String storyId) {
        return new Delete().from(getTypeClass())
                .where("storyId=?", storyId)
                .execute();
    }
}
