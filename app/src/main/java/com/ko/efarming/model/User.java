package com.ko.efarming.model;

import java.io.Serializable;

public class User implements Serializable {
    public String uid;
    public String email;
    public String firebaseToken;

    public User() {}

    public User(String uid, String email, String firebaseToken) {
        this.uid = uid;
        this.email = email;
        this.firebaseToken = firebaseToken;
    }
}