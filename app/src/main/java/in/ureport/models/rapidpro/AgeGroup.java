package in.ureport.models.rapidpro;

import android.support.annotation.NonNull;

/**
 * Created by john-mac on 7/12/16.
 */
public class AgeGroup {

    public static int MIN_AGE = 0;
    public static int MAX_AGE = 1000;

    private String group;

    private Integer minAge = MIN_AGE;

    private Integer maxAge = MAX_AGE;

    public AgeGroup(String group, Integer minAge) {
        this.group = group;
        this.minAge = minAge;
    }

    public AgeGroup(String group, Integer minAge, Integer maxAge) {
        this.group = group;
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    public String getGroup() {
        return group;
    }

    public AgeGroup setGroup(String group) {
        this.group = group;
        return this;
    }

    @NonNull
    public Integer getMinAge() {
        return minAge;
    }

    public AgeGroup setMinAge(Integer minAge) {
        this.minAge = minAge;
        return this;
    }

    @NonNull
    public Integer getMaxAge() {
        return maxAge;
    }

    public AgeGroup setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
        return this;
    }
}
