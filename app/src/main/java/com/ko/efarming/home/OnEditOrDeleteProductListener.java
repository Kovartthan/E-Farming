package com.ko.efarming.home;


import com.ko.efarming.model.ProductInfo;

public interface OnEditOrDeleteProductListener {
    void onEditOrDeleteProduct(boolean isEdit, ProductInfo productInfo);
}
