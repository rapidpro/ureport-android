package in.ureport.models.holders;

import com.google.gson.annotations.Expose;

import in.ureport.models.Contribution;
import in.ureport.models.Story;
import in.ureport.network.GcmApi;
import in.ureport.services.GcmListenerService;

/**
 * Created by johncordeiro on 06/10/15.
 */
public class ContributionHolder extends GcmApi.NotificationHolder {

    @Expose
    public Story story;
    @Expose
    public Contribution contribution;

    public ContributionHolder(Story story, Contribution contribution) {
        this.story = story;
        this.contribution = contribution;
    }
}
