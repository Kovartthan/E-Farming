package com.ko.efarming.model;

/**
 * Created by admin on 2/8/2018.
 */

public class OnlineStatus {
    public long timestamp;
    public boolean isOnlineStatus ;

    public OnlineStatus(){

    }

    public OnlineStatus(long timestamp, boolean isOnlineStatus) {
        this.timestamp = timestamp;
        this.isOnlineStatus = isOnlineStatus;
    }
}
