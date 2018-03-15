package com.ko.efarming.chat;

import android.content.Context;

import com.ko.efarming.model.Chat;
import com.ko.efarming.model.ProductInfo;


public class ChatPresenter implements ChatContract.Presenter, ChatContract.OnSendMessageListener,
        ChatContract.OnGetMessagesListener ,ChatContract.OnOnlineStatusListener {
    private ChatContract.View mView;
    private ChatInteractor mChatInteractor;

    public ChatPresenter(ChatContract.View view) {
        this.mView = view;
        mChatInteractor = new ChatInteractor(this, this,this);
    }

    @Override
    public void sendMessage(Context context, Chat chat, String receiverFirebaseToken,ProductInfo key) {
        mChatInteractor.sendMessageToFirebaseUser(context, chat, receiverFirebaseToken,key);
    }

    @Override
    public void getMessage(String senderUid, String receiverUid,ProductInfo key) {
        mChatInteractor.getMessageFromFirebaseUser(senderUid, receiverUid,key);
    }

    @Override
    public void onSendMessageSuccess() {
        mView.onSendMessageSuccess();
    }

    @Override
    public void onSendMessageFailure(String message) {
        mView.onSendMessageFailure(message);
    }

    @Override
    public void onGetMessagesSuccess(Chat chat) {
        mView.onGetMessagesSuccess(chat);
    }

    @Override
    public void onGetMessagesFailure(String message) {
        mView.onGetMessagesFailure(message);
    }

    @Override
    public void getOnlineStatus(String receiverUid) {
        mChatInteractor.getOnlineStatusForReceiver(receiverUid);
    }

    @Override
    public void onSendOnlineStatus(boolean isOnline, long timeStamp) {
        mView.onGetOnlineStatus(isOnline,timeStamp);
    }
}
