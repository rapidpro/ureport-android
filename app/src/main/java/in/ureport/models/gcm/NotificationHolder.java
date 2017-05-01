package in.ureport.models.gcm;

import com.google.gson.annotations.Expose;

/**
 * Created by John Cordeiro on 5/1/17.
 * Copyright © 2017 Soloshot, Inc. All rights reserved.
 */

public class NotificationHolder {

    @Expose
    private Type type;

    public Type getType() {
        return type;
    }

    public NotificationHolder setType(Type type) {
        this.type = type;
        return this;
    }

}
