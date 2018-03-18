package com.ko.efarming.home.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ko.efarming.R;
import com.ko.efarming.chat.OnChatOpenListener;
import com.ko.efarming.model.ProductBean;
import com.ko.efarming.util.TextUtils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by NEW on 3/10/2018.
 */

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ProductBean> chatList;
    private Context context;
    private OnChatOpenListener onChatOpenListener;


    public void setOnChatOpenListener(OnChatOpenListener onChatOpenListener) {
        this.onChatOpenListener = onChatOpenListener;
    }

    public ChatListAdapter(Context context, ArrayList<ProductBean> chatList) {
        this.chatList = chatList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_item, parent, false);
        return new MyChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ProductBean productBean = chatList.get(position);
        ((MyChatViewHolder) holder).txtChatUserName.setText(TextUtils.capitalizeFirstLetter(chatList.get(position).user.email));
        ((MyChatViewHolder) holder).txtRequestFor.setText("Requested for the product : " + TextUtils.capitalizeFirstLetter(chatList.get(position).productInfo.productName));
        if (!TextUtils.isNullOrEmpty(chatList.get(position).user.userImage)) {
//            Picasso.get().load(productBean.user.userImage).into(((MyChatViewHolder) holder).imgProfile);
        }
        ((MyChatViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChatOpenListener.openChat(productBean.receiverID, productBean.productInfo,productBean.user);
            }
        });
    }


    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void updateList(ArrayList<ProductBean> chatList) {
        this.chatList = chatList;
        notifyDataSetChanged();
    }

    private static class MyChatViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChatUserName, txtRequestFor;
        private ImageView imgProfile;

        public MyChatViewHolder(View itemView) {
            super(itemView);
            txtChatUserName = (TextView) itemView.findViewById(R.id.txt_user_name);
            txtRequestFor = itemView.findViewById(R.id.txt_request_for);
            imgProfile = itemView.findViewById(R.id.img_profile_img);
        }
    }

    public void clearList() {
        chatList.clear();
        notifyDataSetChanged();
    }

}
