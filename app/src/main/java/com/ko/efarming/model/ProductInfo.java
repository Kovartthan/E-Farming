package com.ko.efarming.model;

import com.google.firebase.auth.UserInfo;

import java.io.Serializable;

/**
 * Created by admin on 3/5/2018.
 */

public class ProductInfo implements Serializable {
    public String productName;
    public String productQuantity;
    public String productPrice;
    public String imageUrl;
    public String productID;
    public CompanyInfoPublic company_info;
    public User user_info;
    public int overallRating;
    public int ratingNoOfPerson;
    public ProductInfo(){

    }
    public ProductInfo(String productName, String productQuantity, String productPrice,String imageUrl,String productID,int overallRating,int ratingNoOfPerson) {
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
        this.imageUrl = imageUrl;
        this.productID = productID;
        this.overallRating = overallRating;
        this.ratingNoOfPerson = ratingNoOfPerson;
    }
}
