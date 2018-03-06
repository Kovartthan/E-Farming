package com.ko.efarming.home.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ko.efarming.R;
import com.ko.efarming.home.adapters.ProductListAdapter;
import com.ko.efarming.model.ProductInfo;
import com.ko.efarming.util.Constants;

import java.util.ArrayList;

import static com.ko.efarming.EFApp.getApp;
import static com.ko.efarming.util.Constants.REFRESH_PRODUCT;


public class ProductListFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<ProductInfo> productInfoArrayList;
    private ProductListAdapter productListAdapter;
    public ProductListFragment (){

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
         if(requestCode == REFRESH_PRODUCT){
             getProductListFromDataBase();
         }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list,container,false);
        init(view);
        setupDefault();
        setupEvent();
        return view;
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.rv_product_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        productInfoArrayList = new ArrayList<>();
        productListAdapter = new ProductListAdapter(getActivity(),productInfoArrayList);
        recyclerView.setAdapter(productListAdapter);
    }

    private void setupDefault() {
        getProductListFromDataBase();

    }

    private void getProductListFromDataBase() {
        productInfoArrayList = new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.USERS)
                .child(getApp().getFireBaseAuth().getCurrentUser().getUid())
                .child(Constants.COMPANY_INFO)
                .child(Constants.PRODUCT_INFO).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null) {
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        ProductInfo productInfo = snapshot.getValue(ProductInfo.class);
                        productInfoArrayList.add(productInfo);
                    }
                }
                productListAdapter.updateList(productInfoArrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG","onCancelled"+databaseError);
            }
        });
        for(ProductInfo productInfo :productInfoArrayList){
            Log.e("TAG",""+productInfo.productName);
        }

    }

    private void setupEvent() {

    }
}
