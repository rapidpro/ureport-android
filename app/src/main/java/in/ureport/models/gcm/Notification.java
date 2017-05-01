package in.ureport.models.gcm;

import com.google.gson.annotations.Expose;

/**
 * Created by John Cordeiro on 5/1/17.
 * Copyright © 2017 Soloshot, Inc. All rights reserved.
 */

public class Notification {

    @Expose
    private String title;

    @Expose
    private String body;

    public Notification(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
