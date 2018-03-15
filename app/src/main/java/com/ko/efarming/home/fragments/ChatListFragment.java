package com.ko.efarming.home.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ko.efarming.R;
import com.ko.efarming.chat.OnChatOpenListener;
import com.ko.efarming.home.ChatListBean;
import com.ko.efarming.home.activities.ChatActivity;
import com.ko.efarming.home.adapters.ChatListAdapter;
import com.ko.efarming.model.ProductBean;
import com.ko.efarming.model.ProductInfo;
import com.ko.efarming.model.User;
import com.ko.efarming.util.Constants;

import java.util.ArrayList;
import java.util.HashSet;

import static com.ko.efarming.EFApp.getApp;


public class ChatListFragment extends Fragment implements OnChatOpenListener {
    private RecyclerView recyclerView;
    private ArrayList<ProductBean> chatList;
    private ChatListAdapter chatListAdapter;
    private String receiverID;
    private boolean isPause;
    private ArrayList<ProductInfo> productInfoArrayList;

    public ChatListFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.under_construction, container, false);
        init(view);
        setupDefault();
        setupEvent();
        return view;
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_chat);
        chatList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(getActivity(), chatList);
        chatListAdapter.setOnChatOpenListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(chatListAdapter);

    }

    private void setupDefault() {
        getProductInfo();

    }

    private void getProductInfo() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference().child(Constants.USERS).child(getApp().getFireBaseAuth().getCurrentUser().getUid()).child("all_chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            ArrayList<ChatListBean> chatListBeanArrayList = new ArrayList<>();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        ProductInfo productInfo = null;
                        for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                            if (snapshot2.getKey().equals("detail_info")) {
                                productInfo = snapshot2.getValue(ProductInfo.class);
                            }
                        }
                        String key = snapshot1.getKey();
                        chatListBeanArrayList.add(new ChatListBean(productInfo, key));
                    }
                }
                HashSet<ChatListBean> hashSet = new HashSet<ChatListBean>();
                hashSet.addAll(chatListBeanArrayList);
                chatListBeanArrayList.clear();
                chatListBeanArrayList.addAll(hashSet);
                for (int i = 0; i < chatListBeanArrayList.size(); i++) {
                    getChatUserInfo(chatListBeanArrayList.get(i).key, chatListBeanArrayList.get(i).productInfo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void getChatsList() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference().child(Constants.USERS).child(getApp().getFireBaseAuth().getCurrentUser().getUid())
                .child("all_chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            ProductInfo productInfo;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (snapshot1.getKey().equals("detail_info")) {
                            productInfo = snapshot1.getValue(ProductInfo.class);
                        }
                    }
                    getChatUserInfo(snapshot.getKey(), productInfo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getChatUserInfo(final String key, final ProductInfo productInfo) {
        FirebaseDatabase.getInstance()
                .getReference().child("client_users").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                chatList.add(new ProductBean(key, user, productInfo));
                chatListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setupEvent() {

    }

    @Override
    public void openChat(String key, ProductInfo productInfo, User user) {
        startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("Product_id", productInfo).putExtra("receiver", key).putExtra("user", user));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isPause) {
            chatList = new ArrayList<>();
            chatListAdapter.updateList(chatList);
//            getChatsList();
            getProductInfo();
            isPause = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isPause = true;
    }
}