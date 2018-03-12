package com.ko.efarming.model;

import java.io.Serializable;

/**
 * Created by admin on 3/2/2018.
 */

public class CompanyInfoPublic implements Serializable {
        public String name;
        public String email;
        public String phone;
        public String location;
        public String photoUrl;
        public double latitude,longitude;
        public CompanyInfoPublic(){

        }

        public CompanyInfoPublic(String name, String email, String phone, String location, String photoUrl,double latitude,double longitude) {
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.location = location;
            this.photoUrl = photoUrl;
            this.latitude = latitude;
            this.longitude = longitude;
        }
}
