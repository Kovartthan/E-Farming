package com.ko.efarming.activites;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ko.efarming.EFApp;
import com.ko.efarming.R;
import com.ko.efarming.company_info.CompanyInfoActivity;
import com.ko.efarming.home.HomeActivity;
import com.ko.efarming.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {
    protected int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setTransparentStatusBar(this);
        reDirectToMainScreen();
    }

    private void reDirectToMainScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (EFApp.getApp().getAppPreference().getCheckRedirect() == 0) {
                    if (EFApp.getApp().getFireBaseAuth().getCurrentUser() != null) {
                        startActivity(new Intent(SplashActivity.this, CompanyInfoActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }
                } else if (EFApp.getApp().getAppPreference().getCheckRedirect() == 1) {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    public void setTransparentStatusBar(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            window.getDecorView().setSystemUiVisibility(option);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
