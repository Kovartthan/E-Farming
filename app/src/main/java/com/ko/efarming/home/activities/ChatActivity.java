package com.ko.efarming.home.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ko.efarming.R;
import com.ko.efarming.base.BaseActivity;
import com.ko.efarming.chat.ChatContract;
import com.ko.efarming.chat.ChatPresenter;
import com.ko.efarming.home.adapters.ChatRecyclerAdapter;
import com.ko.efarming.model.Chat;
import com.ko.efarming.model.ProductInfo;
import com.ko.efarming.model.User;
import com.ko.efarming.util.Constants;
import com.ko.efarming.util.DeviceUtils;
import com.ko.efarming.util.TextUtils;


import java.util.ArrayList;


public class ChatActivity extends BaseActivity implements ChatContract.View, TextView.OnEditorActionListener {
    private RecyclerView mRecyclerViewChat;
    private EditText mETxtMessage;

    private ProgressDialog mProgressDialog;

    private ChatRecyclerAdapter mChatRecyclerAdapter;

    private ChatPresenter mChatPresenter;

    private ProductInfo productInfo;

    private String recevierId;

    private String receiverFirebaseToken;

    private String receiver;
    private String receiverUid;

    private TextView txtChatUserName, txtRequestFor;
    private ImageView imgProfile;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chat);
        init();
    }

    private void init() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getString(R.string.loading));
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setIndeterminate(true);


        mRecyclerViewChat = (RecyclerView) findViewById(R.id.recycler_view_chat);
        mETxtMessage = (EditText) findViewById(R.id.edit_text_message);
        txtChatUserName = (TextView) findViewById(R.id.txt_user_name);
        txtRequestFor = findViewById(R.id.txt_request_for);
        imgProfile = findViewById(R.id.img_profile_img);
        mChatRecyclerAdapter = new ChatRecyclerAdapter(this, new ArrayList<Chat>());
        mRecyclerViewChat.setAdapter(mChatRecyclerAdapter);
        mETxtMessage.setOnEditorActionListener(this);

        productInfo = (ProductInfo) getIntent().getSerializableExtra("Product_id");
        recevierId = getIntent().getStringExtra("receiver");

        getReceiverInfo();

        getUserInfo();

        mChatPresenter = new ChatPresenter(this);

        findViewById(R.id.fab_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceUtils.hideSoftKeyboard(ChatActivity.this);
                sendMessage();
            }
        });

        findViewById(R.id.img_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              onBackPressed();
            }
        });

    }

    private void getUserInfo() {
        if (getIntent() != null && getIntent().hasExtra("user")) {
            user = (User) getIntent().getSerializableExtra("user");
            txtChatUserName.setText(user.email);
            txtRequestFor.setText("Online");
            Glide.with(this).load(user.userImage).into(imgProfile);
        }
    }

    private void getReceiverInfo() {
        FirebaseDatabase.getInstance()
                .getReference().child("client_users").child(recevierId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    User user = dataSnapshot.getValue(User.class);
                    receiver = user.email;
                    receiverUid = user.uid;
                    receiverFirebaseToken = user.firebaseToken;
                    mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                            receiverUid, productInfo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            sendMessage();
            return true;
        }
        return false;
    }

    private void sendMessage() {
        String message = mETxtMessage.getText().toString();

        if (TextUtils.isNullOrEmpty(message))
            return;

        String sender = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Chat chat = new Chat(sender,
                receiver,
                senderUid,
                receiverUid,
                message,
                System.currentTimeMillis());
        mChatPresenter.sendMessage(this.getApplicationContext(),
                chat,
                receiverFirebaseToken, productInfo);
    }

    @Override
    public void onSendMessageSuccess() {
        mETxtMessage.setText("");
        Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSendMessageFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetMessagesSuccess(Chat chat) {
        if (chat == null) {
            return;
        }
        mChatRecyclerAdapter.add(chat);
        mChatRecyclerAdapter.notifyDataSetChanged();
        mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
        mChatRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGetMessagesFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetOnlineStatus(boolean isOnline, long timeStamp) {

    }


//    @Subscribe
//    public void onPushNotificationEvent(PushNotificationEvent pushNotificationEvent) {
//        if (mChatRecyclerAdapter == null || mChatRecyclerAdapter.getItemCount() == 0) {
//            mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
//                    pushNotificationEvent.getUid());
//        }
//    }
}
