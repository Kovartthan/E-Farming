package com.ko.efarming.home;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ko.efarming.R;
import com.ko.efarming.base.BaseActivity;
import com.ko.efarming.company_info.AddressFetchActivity;
import com.ko.efarming.login.LoginActivity;
import com.ko.efarming.model.RectClass;
import com.ko.efarming.util.Constants;

public class HomeActivity extends BaseActivity {
    public static RectClass rectClass ;
    private FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Home");
        init();
        setupDefault();
        setupEvent();

    }

    private void init() {
        floatingActionButton = findViewById(R.id.btn_add_product);
    }

    private void setupDefault() {

    }

    private void setupEvent() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AddProductActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                rectClass = new RectClass(findViewById(R.id.btn_add_product));
                intent.putExtra(Constants.SEND_RECT, rectClass);
                startActivity(intent);
            }
        });
//        getApp().getFireBaseAuth().addAuthStateListener(authListener);
    }


//    FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
//        @Override
//        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//            FirebaseUser user = firebaseAuth.getCurrentUser();
//            if (user == null) {
//                if(efProgressDialog.isShowing())
//                efProgressDialog.dismiss();
//                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
//                finish();
//            }
//        }
//    };
//
//    public void doLogout(View view) {
//        if(efProgressDialog != null)
//            efProgressDialog.show();
//        getApp().getFireBaseAuth().signOut();
//    }
}
