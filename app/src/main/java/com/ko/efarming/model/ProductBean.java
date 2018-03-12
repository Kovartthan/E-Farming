package com.ko.efarming.model;

/**
 * Created by NEW on 3/10/2018.
 */

public class ProductBean {
    public String receiverID;
    public User user;
    public ProductInfo productInfo;

    public ProductBean(String receiverID, User nameInfo, ProductInfo productInfo) {
        this.receiverID = receiverID;
        this.user = nameInfo;
        this.productInfo = productInfo;
    }

    public ProductBean(User nameInfo, ProductInfo productInfo) {
        this.user = nameInfo;
        this.productInfo = productInfo;
    }
}
