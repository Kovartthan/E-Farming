package com.ko.efarming.company_info;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ko.efarming.R;
import com.ko.efarming.base.BaseActivity;

public class CompanyInfoActivity extends BaseActivity {
    private Toolbar toolbar;
    private TextInputLayout companyAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info);
        init();
        setupDefault();
        setupEvent();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        companyAddress = findViewById(R.id.location_layout);
    }

    private void setupDefault() {
        setupToolbar();
    }

    private void setupToolbar() {
        toolbar.setTitle("Setup Company Info");
    }

    private void setupEvent() {

        companyAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CompanyInfoActivity.this,AddressFetchActivity.class));
            }
        });

    }
}
