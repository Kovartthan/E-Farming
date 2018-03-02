package com.ko.efarming.model;

/**
 * Created by admin on 3/2/2018.
 */

public class CompanyInfo {
    public String name;
    public String email;
    public String phone;
    public String location;

    public CompanyInfo(){

    }

    public CompanyInfo(String name, String email, String phone, String location) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.location = location;
    }
}
