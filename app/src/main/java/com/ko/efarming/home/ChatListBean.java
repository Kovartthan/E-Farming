package com.ko.efarming.home;

import com.ko.efarming.model.ProductInfo;

/**
 * Created by admin on 3/15/2018.
 */

public class ChatListBean {
    public ProductInfo productInfo;
    public String key;

    public ChatListBean(ProductInfo productInfo, String key) {
        this.productInfo = productInfo;
        this.key = key;
    }
}
