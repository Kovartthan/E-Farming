package com.ko.efarming.model;

/**
 * Created by admin on 3/5/2018.
 */

public class ProductInfo {
    public String productName;
    public String productQuantity;
    public String productPrice;
    public String imageUrl;
    public ProductInfo(){

    }
    public ProductInfo(String productName, String productQuantity, String productPrice,String imageUrl) {
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
        this.imageUrl = imageUrl;
    }
}
