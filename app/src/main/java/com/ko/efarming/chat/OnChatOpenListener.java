package com.ko.efarming.chat;

import com.ko.efarming.model.ProductInfo;
import com.ko.efarming.model.User;

/**
 * Created by NEW on 3/9/2018.
 */

public interface OnChatOpenListener {
    void openChat(String receiver ,ProductInfo productInfo,User user);
}
