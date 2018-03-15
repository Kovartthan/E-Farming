package com.ko.efarming.home.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ko.efarming.R;
import com.ko.efarming.base.BaseActivity;
import com.ko.efarming.home.adapters.TabsPagerAdapter;
import com.ko.efarming.home.fragments.ChatListFragment;
import com.ko.efarming.home.fragments.ProductListFragment;
import com.ko.efarming.login.LoginActivity;
import com.ko.efarming.model.RectClass;
import com.ko.efarming.util.Constants;

import static com.ko.efarming.util.Constants.REFRESH_PRODUCT;

public class HomeActivity extends BaseActivity {
    public static RectClass rectClass ;
    private FloatingActionButton floatingActionButton;
    protected ProductListFragment productListFragment;
    protected ChatListFragment chatListFragment;
    protected ViewPager viewPager;
    protected TabsPagerAdapter mAdapter;
    protected TabLayout tabLayout;
    private Toolbar toolbar;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            productListFragment.onActivityResult(requestCode,resultCode,data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        setupDefault();
        setupEvent();

    }

    private void init() {
        floatingActionButton = findViewById(R.id.btn_add_product);
        viewPager =  findViewById(R.id.pager);
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        tabLayout =  findViewById(R.id.tabs);
        toolbar = findViewById(R.id.toolbar);
        productListFragment = new ProductListFragment();
        chatListFragment = new ChatListFragment();
    }

    private void setupDefault() {
        toolbar.setTitle("Home");
        mAdapter.addFragment(productListFragment, "Products");
        mAdapter.addFragment(chatListFragment, "Chats");
        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);
        setOnlineStatus(true);
    }

    private void setupEvent() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AddOrEditProductActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                rectClass = new RectClass(findViewById(R.id.btn_add_product));
                intent.putExtra(Constants.SEND_RECT, rectClass);
                startActivityForResult(intent,REFRESH_PRODUCT);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0)
                    floatingActionButton.setVisibility(View.VISIBLE);
                else
                    floatingActionButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        getApp().getFireBaseAuth().addAuthStateListener(authListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                doLogout();
                break;
        }
        return false;
    }


    FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                if(efProgressDialog.isShowing())
                efProgressDialog.dismiss();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
            }
        }
    };

    public void doLogout() {
        if(efProgressDialog != null)
            efProgressDialog.show();
        getApp().getFireBaseAuth().signOut();
    }
}
