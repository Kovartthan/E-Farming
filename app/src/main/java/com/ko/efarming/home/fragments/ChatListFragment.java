package com.ko.efarming.home.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ko.efarming.R;



public class ChatListFragment extends Fragment {
    public ChatListFragment(){

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.under_construction,container,false);
        init(view);
        setupDefault();
        setupEvent();
        return view;
    }

    private void init(View view) {

    }

    private void setupDefault() {

    }

    private void setupEvent() {

    }
}