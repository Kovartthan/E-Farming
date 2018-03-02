package com.ko.efarming.model;

import java.io.Serializable;

public class User implements Serializable {
    public String uid;
    public String email;
    public String name;
    public String firebaseToken;
    public String userImage;
    public boolean isCompanyProfileUpdated;
    public User() {

    }

    public User(String uid, String email, String firebaseToken,String userImage,boolean isCompanyProfileUpdated) {
        this.uid = uid;
        this.email = email;
        this.firebaseToken = firebaseToken;
        this.userImage = userImage;
        this.isCompanyProfileUpdated = isCompanyProfileUpdated;
    }
}