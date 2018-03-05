package com.ko.efarming.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.hendraanggrian.kota.content.Themes;
import com.hendraanggrian.reveallayout.Radius;
import com.hendraanggrian.reveallayout.RevealableLayout;
import com.ko.efarming.R;
import com.ko.efarming.model.RectClass;
import com.ko.efarming.util.Constants;

public class AddProductActivity extends AppCompatActivity {
    RectClass rect;
    RevealableLayout revealLayout;
    Toolbar toolbar;
    ViewGroup layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        init();
        setupDefault();
        setupEvent();
    }

    private void init() {
        revealLayout = findViewById(R.id.revealLayout);
        toolbar = findViewById(R.id.toolbar);
//        layout = findViewById(R.id.layout);
        rect = (RectClass) getIntent().getSerializableExtra(Constants.SEND_RECT);
    }

    private void setupDefault() {
        doRevealAction();
    }

    private void doRevealAction() {
        layout.post(new Runnable() {
            @Override
            public void run() {
                Animator animator = revealLayout.reveal(layout, HomeActivity.rectClass.getRect().centerX(),  HomeActivity.rectClass.getRect().centerY(), Radius.GONE_ACTIVITY);
                animator.setDuration(1000);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (Build.VERSION.SDK_INT >= 21) {
                            getWindow().setStatusBarColor(Themes.getColor(getTheme(), R.attr.colorPrimaryDark, true));
                        }
                    }
                });
                animator.start();
            }
        });
    }

    @Override
    public void onBackPressed() {
        exitRevealAction();
    }

    private void exitRevealAction(){
        Animator animator = revealLayout.reveal(layout,  HomeActivity.rectClass.getRect().centerX(),  HomeActivity.rectClass.getRect().centerY(), Radius.ACTIVITY_GONE);
        animator.setDuration(1000);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (Build.VERSION.SDK_INT >= 21) {
                    getWindow().setStatusBarColor(Themes.getColor(getTheme(), R.attr.colorPrimaryDark, true));
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                layout.setVisibility(View.INVISIBLE);
                finish();
                overridePendingTransition(0, 0);
            }
        });
        animator.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupEvent() {

    }
}
