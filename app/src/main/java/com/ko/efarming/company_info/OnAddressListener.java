package com.ko.efarming.company_info;

/**
 * Created by admin on 3/2/2018.
 */

public interface OnAddressListener {
    void onFetchedAddress(String address,String city);
    void onFetchFailure(String status);
}
