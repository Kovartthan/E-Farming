package com.ko.efarming.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.Window;
import android.widget.ProgressBar;

import com.ko.efarming.R;


public class EFProgessDialog extends Dialog {

    private Context mContext;
    ProgressBar progressBar;

    public EFProgessDialog(Context context) {
        super(context, R.style.TransparentProgressDialog);
        this.mContext = (context).getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom_progress_loader);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        Drawable normalDrawable = mContext.getResources().getDrawable(R.drawable.progress_diag_spinner_anim);
//        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
//        DrawableCompat.setTint(wrapDrawable, mContext.getResources().getColor(R.color.progress_color));
        this.setCancelable(false);
        progressBar.setIndeterminateDrawable(normalDrawable);
    }


    @Override
    public void dismiss() {
        super.dismiss();
    }

}