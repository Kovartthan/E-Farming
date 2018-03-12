package com.ko.efarming.chat;

import android.content.Context;

import com.ko.efarming.model.Chat;
import com.ko.efarming.model.ProductInfo;


public interface ChatContract {
    interface View {
        void onSendMessageSuccess();

        void onSendMessageFailure(String message);

        void onGetMessagesSuccess(Chat chat);

        void onGetMessagesFailure(String message);
    }

    interface Presenter {
        void sendMessage(Context context, Chat chat, String receiverFirebaseToken, ProductInfo key);

        void getMessage(String senderUid, String receiverUid, String key);
    }

    interface Interactor {
        void sendMessageToFirebaseUser(Context context, Chat chat, String receiverFirebaseToken, ProductInfo key);

        void getMessageFromFirebaseUser(String senderUid, String receiverUid, String key);
    }

    interface OnSendMessageListener {
        void onSendMessageSuccess();

        void onSendMessageFailure(String message);
    }

    interface OnGetMessagesListener {
        void onGetMessagesSuccess(Chat chat);

        void onGetMessagesFailure(String message);
    }
}
