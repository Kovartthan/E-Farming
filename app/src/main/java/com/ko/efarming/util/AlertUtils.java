package com.ko.efarming.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.ko.efarming.R;


public class AlertUtils {

    private AlertDialog.Builder progressAlert;

    private AlertDialog progressAlertDialog;

    public AlertUtils(Context context) {
        progressAlert = new AlertDialog.Builder(context, R.style.Theme_Alert);
    }

    public static void showAlert(Context context, String title, String message,
                                 DialogInterface.OnClickListener onClick, boolean cancelable) {
        if (!((Activity) context).isFinishing()) {
            new AlertDialog.Builder(context, R.style.Theme_Alert)
                    .setMessage(message)
                    .setTitle((title != null && !title.equals("")) ? title : context.getString(R.string.app_name)) //TODO null check the title with TextUtils.
                    .setCancelable(cancelable)
                    .setPositiveButton(android.R.string.ok, onClick)
                    .create().show();
        }
    }

    public static void showSnack(Context context, View parent, String message) {
        try {
            Snackbar.make(parent, message, Snackbar.LENGTH_LONG).show();
        } catch (Exception e) {

        }

    }


   /* public static void showCustomAlert(Context context, String title, String message, final DialogInterface.OnClickListener onClick, boolean cancelable ){
        if (!((Activity) context).isFinishing()) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View subView = inflater.inflate(R.layout.cusrom_spannable_alert_layout, null);
            TextView txtTitle = (TextView) subView.findViewById(R.id.txt_title);
            TextView txtMessage = (TextView) subView.findViewById(R.id.txt_message);
            ImageView imgClose = (ImageView) subView.findViewById(R.id.img_close);
            txtTitle.setText((title != null && !title.equals("")) ? title : context.getString(R.string.app_name));
            txtMessage.setText(message);

            final AlertDialog dialog=new AlertDialog.Builder(context)
                    .setView(subView)
                    .setCancelable(cancelable)
                    .create();
            dialog.show();
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClick.onClick(dialog,0);
                }
            });
        }
    }*/

    public static AlertDialog.Builder getBuilder(Context context) {
        return new AlertDialog.Builder(context, R.style.Dialog);
    }

    public static void showBackAlert(final Context context, String title, String message) {
        if (!((Activity) context).isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_Alert);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ((Activity) context).onBackPressed();
                }
            });
            builder.show();
        }
    }

    public static void showAlert(Context context, String message) {
        showAlert(context, null, message, null, false);
    }

    public static void showAlert(Context context, String title, String message) {
        showAlert(context, title, message, null, false);
    }

    public static void showAlert(Context context, String message, DialogInterface.OnClickListener onClick, boolean cancelable) {
        showAlert(context, null, message, onClick, cancelable);
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    public static void showAlert(Context context, String title, String message,
                                 DialogInterface.OnClickListener okClick, DialogInterface.OnClickListener cancelClick, boolean cancelable) {
        if (!((Activity) context).isFinishing()) {
            new AlertDialog.Builder(context, R.style.Theme_Alert)
                    .setMessage(message)
                    .setTitle((title != null && !title.equals("")) ? title : context.getString(R.string.app_name)) //TODO null check the title with TextUtils.
                    .setCancelable(cancelable)
                    .setPositiveButton(android.R.string.ok, okClick)
                    .setNegativeButton(android.R.string.cancel, cancelClick)
                    .create().show();
        }
    }

    public static void showAlertWithYesNo(Context context, String title, String message,
                                          DialogInterface.OnClickListener yesClick, DialogInterface.OnClickListener noClick, boolean cancelable) {
        if (!((Activity) context).isFinishing()) {
            new AlertDialog.Builder(context, R.style.Theme_Alert)
                    .setMessage(message)
                    .setTitle((title != null && !title.equals("")) ? title : context.getString(R.string.app_name)) //TODO null check the title with TextUtils.
                    .setCancelable(cancelable)
                    .setPositiveButton("Yes", yesClick)
                    .setNegativeButton("No", noClick)
                    .create().show();
        }
    }


    public static void showCommonAlertDialogWithPositive(Context context, String title, String message, String positiveButtonText, DialogInterface.OnClickListener positiveClick) {
        if (context != null && !((Activity) context).isFinishing()) {
            new android.app.AlertDialog.Builder(context).setTitle(title).setMessage(message).
                    setPositiveButton(positiveButtonText, positiveClick).
                    setCancelable(false)
                    .show();
        }
    }
}
