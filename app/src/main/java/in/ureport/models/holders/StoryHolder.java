package in.ureport.models.holders;

import in.ureport.models.Story;
import in.ureport.models.User;

/**
 * Created by John Cordeiro on 4/19/17.
 * Copyright © 2017 Soloshot, Inc. All rights reserved.
 */

public class StoryHolder {

    private String key;

    private Integer contributions;

    private Integer likes;

    private User userObject;

    public StoryHolder(Story story) {
        this.key = story.getKey();
        this.contributions = story.getContributions();
        this.likes = story.getLikes();
        this.userObject = story.getUserObject();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getContributions() {
        return contributions;
    }

    public void setContributions(Integer contributions) {
        this.contributions = contributions;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public User getUserObject() {
        return userObject;
    }

    public void setUserObject(User userObject) {
        this.userObject = userObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StoryHolder that = (StoryHolder) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
