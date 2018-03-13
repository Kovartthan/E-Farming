package com.ko.efarming.model;

import java.io.Serializable;

/**
 * Created by admin on 3/13/2018.
 */

public class UserFingerPrint implements Serializable  {
        public String uid;
        public String email;
        public String name;
        public String firebaseToken;
        public String userImage;
        public boolean isCompanyProfileUpdated;
        public String password;
        public UserFingerPrint() {

        }

        public UserFingerPrint(String uid, String email, String firebaseToken,String userImage,boolean isCompanyProfileUpdated,String password) {
            this.uid = uid;
            this.email = email;
            this.firebaseToken = firebaseToken;
            this.userImage = userImage;
            this.isCompanyProfileUpdated = isCompanyProfileUpdated;
            this.password = password;
        }

}
