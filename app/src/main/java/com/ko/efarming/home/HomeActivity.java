package com.ko.efarming.home;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ko.efarming.R;
import com.ko.efarming.base.BaseActivity;
import com.ko.efarming.login.LoginActivity;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Home");
        getApp().getFireBaseAuth().addAuthStateListener(authListener);
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

    public void doLogout(View view) {
        if(efProgressDialog != null)
            efProgressDialog.show();
        getApp().getFireBaseAuth().signOut();
    }
}
