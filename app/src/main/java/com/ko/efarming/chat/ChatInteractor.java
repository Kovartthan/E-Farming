package com.ko.efarming.chat;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ko.efarming.fcm.FcmNotificationBuilder;
import com.ko.efarming.model.Chat;
import com.ko.efarming.model.OnlineStatus;
import com.ko.efarming.model.ProductInfo;
import com.ko.efarming.util.Constants;
import com.ko.efarming.util.SharedPrefUtil;

import static com.ko.efarming.EFApp.getApp;
import static com.ko.efarming.util.Constants.ONLINE_STATUS;


public class ChatInteractor implements ChatContract.Interactor {
    private static final String TAG = "ChatInteractor";

    private ChatContract.OnSendMessageListener mOnSendMessageListener;
    private ChatContract.OnGetMessagesListener mOnGetMessagesListener;
    private ChatContract.OnOnlineStatusListener mOnOnlineStatusListener;

    public ChatInteractor(ChatContract.OnSendMessageListener onSendMessageListener) {
        this.mOnSendMessageListener = onSendMessageListener;
    }

    public ChatInteractor(ChatContract.OnGetMessagesListener onGetMessagesListener) {
        this.mOnGetMessagesListener = onGetMessagesListener;
    }

    public ChatInteractor(ChatContract.OnSendMessageListener onSendMessageListener,
                          ChatContract.OnGetMessagesListener onGetMessagesListener,ChatContract.OnOnlineStatusListener onOnlineStatusListener) {
        this.mOnSendMessageListener = onSendMessageListener;
        this.mOnGetMessagesListener = onGetMessagesListener;
        this.mOnOnlineStatusListener = onOnlineStatusListener;
    }

    @Override
    public void sendMessageToFirebaseUser(final Context context, final Chat chat, final String receiverFirebaseToken, final ProductInfo productInfo) {
        final String room_type_1 = chat.senderUid + "_" + chat.receiverUid;
        final String room_type_2 = chat.receiverUid + "_" + chat.senderUid;

        final DatabaseReference adminDatabaseReference =   FirebaseDatabase.getInstance()
                .getReference().child(Constants.PRODUCT_INFO).child(productInfo.productID).child("all_chats").child(chat.receiverUid);

        final  DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference().child(Constants.USERS).child(chat.senderUid).child("all_chats").child(productInfo.productID).child(chat.receiverUid);

        final String fbKey =   adminDatabaseReference.push().getKey();

        databaseReference.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               /* if (dataSnapshot.hasChild(room_type_1)) {
                    Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_1 + " exists");
                    databaseReference.child(room_type_1).child(String.valueOf(chat.timestamp)).setValue(chat);
                } else*/ if (dataSnapshot.hasChild(room_type_2)) {
                    Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_2 + " exists");
                    databaseReference.child(room_type_2).child(String.valueOf(fbKey)).setValue(chat);
                } else {
                    Log.e(TAG, "sendMessageToFirebaseUser: success");
//                    databaseReference.child(room_type_1).child(String.valueOf(chat.timestamp)).setValue(chat);
                    databaseReference.child(room_type_2).child(String.valueOf(fbKey)).setValue(chat);
                    getMessageFromFirebaseUser(chat.senderUid, chat.receiverUid,productInfo);
                }
                // send push notification to the receiver
                sendPushNotificationToReceiver(chat.sender,
                        chat.message,
                        chat.senderUid,
                        FirebaseInstanceId.getInstance().getToken(),
                        receiverFirebaseToken);
                mOnSendMessageListener.onSendMessageSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnSendMessageListener.onSendMessageFailure("Unable to send message: " + databaseError.getMessage());
            }
        });

        adminDatabaseReference.child(Constants.CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               /* if (dataSnapshot.hasChild(room_type_1)) {
                    Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_1 + " exists");
                    adminDatabaseReference.child(Constants.CHAT_ROOMS).child(room_type_1).child(String.valueOf(chat.timestamp)).setValue(chat);
                } else */if (dataSnapshot.hasChild(room_type_2)) {
                    Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_2 + " exists");
                    adminDatabaseReference.child(Constants.CHAT_ROOMS).child(room_type_2).child(String.valueOf(fbKey)).setValue(chat);
                } else {
                    Log.e(TAG, "sendMessageToFirebaseUser: success");
                    adminDatabaseReference.child(Constants.CHAT_ROOMS).child(room_type_2).child(String.valueOf(fbKey)).setValue(chat);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void sendPushNotificationToReceiver(String username,
                                                String message,
                                                String uid,
                                                String firebaseToken,
                                                String receiverFirebaseToken) {
        FcmNotificationBuilder.initialize()
                .title(username)
                .message(message)
                .username(username)
                .uid(uid)
                .firebaseToken(firebaseToken)
                .receiverFirebaseToken(receiverFirebaseToken)
                .send();
    }

    @Override
    public void getMessageFromFirebaseUser(final String senderUid, final String receiverUid, final ProductInfo receiver) {
        final String room_type_1 = senderUid + "_" + receiverUid;
        final String room_type_2 = receiverUid + "_" + senderUid;

        final  DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference().child(Constants.USERS).child(senderUid).child("all_chats").child(receiver.productID).child(receiverUid);

        databaseReference.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    Log.e(TAG, "getMessageFromFirebaseUser: " + room_type_1 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference().child(Constants.USERS).child(senderUid).child("all_chats").child(receiver.productID).child(receiverUid)
                            .child(room_type_1).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Chat chat = dataSnapshot.getValue(Chat.class);
                            mOnGetMessagesListener.onGetMessagesSuccess(chat);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
                        }
                    });
                } else if (dataSnapshot.hasChild(room_type_2)) {
                    Log.e(TAG, "getMessageFromFirebaseUser: " + room_type_2 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference().child(Constants.USERS).child(senderUid).child("all_chats").child(receiver.productID).child(receiverUid)
                            .child(room_type_2).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Chat chat = dataSnapshot.getValue(Chat.class);
                            mOnGetMessagesListener.onGetMessagesSuccess(chat);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
                        }
                    });
                } else {
                    Log.e(TAG, "getMessageFromFirebaseUser: no such room available");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
            }
        });
    }

    void getOnlineStatusForReceiver(final String receiverId){
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.CLIENT_USERS)
                .child(receiverId).child(ONLINE_STATUS).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                OnlineStatus onlineStatus = dataSnapshot.getValue(OnlineStatus.class);
                mOnOnlineStatusListener.onSendOnlineStatus(onlineStatus.isOnlineStatus,onlineStatus.timestamp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Online status unavailable");
            }
        });
    }
}
