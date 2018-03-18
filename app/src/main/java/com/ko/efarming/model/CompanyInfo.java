package com.ko.efarming.model;

import java.io.Serializable;

/**
 * Created by admin on 3/2/2018.
 */

public class CompanyInfo implements Serializable{
    public String name;
    public String city;
    public String phone;
    public String location;

    public CompanyInfo(){

    }

    public CompanyInfo(String name, String city, String phone, String location) {
        this.name = name;
        this.city = city;
        this.phone = phone;
        this.location = location;
    }
}
