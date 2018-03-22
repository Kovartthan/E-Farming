package com.ko.efarming.model;

import java.io.Serializable;

/**
 * Created by admin on 3/2/2018.
 */

public class CompanyInfoPublic implements Serializable {
        public String name;
        public String city;
        public String phone;
        public String location;
        public String photoUrl;
        public double latitude,longitude;
        public String companyType;
        public CompanyInfoPublic(){

        }

        public CompanyInfoPublic(String name, String city, String phone, String location, String photoUrl,double latitude,double longitude,  String companyType) {
            this.name = name;
            this.city = city;
            this.phone = phone;
            this.location = location;
            this.photoUrl = photoUrl;
            this.latitude = latitude;
            this.longitude = longitude;
            this.companyType = companyType;
        }
}
