package in.ureport.db.repository;

import java.util.List;

import in.ureport.models.db.ContributionNotification;

/**
 * Created by johncordeiro on 07/10/15.
 */
public interface ContributionNotificationRepository extends AbstractRepository<ContributionNotification> {

    List<ContributionNotification> getAllOrderedByDate();

    List<ContributionNotification> deleteByStoryId(String storyId);

}
