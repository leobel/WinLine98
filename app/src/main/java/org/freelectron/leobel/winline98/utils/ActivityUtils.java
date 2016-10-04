package org.freelectron.leobel.winline98.utils;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.freelectron.leobel.winline98.dialogs.WinlineDialog;

/**
 * Created by leobel on 10/4/16.
 */
public class ActivityUtils {

    public static void showDialog(Activity activity, String message){
        if (activity == null || !(activity instanceof AppCompatActivity)) return;

        WinlineDialog dialogFragment = WinlineDialog.newInstance(message);
        dialogFragment.show(((AppCompatActivity)activity).getSupportFragmentManager(), "dialog");
    }

    public static void showDialog(Activity activity, String message, String title){
        if (activity == null || !(activity instanceof AppCompatActivity)) return;

        WinlineDialog dialogFragment = WinlineDialog.newInstance(message, title);
        dialogFragment.show(((AppCompatActivity)activity).getSupportFragmentManager(), "dialog");
    }

    public static void showDialog(Activity activity, String message, String title, Boolean showCancelButton){
        if (activity == null || !(activity instanceof AppCompatActivity)) return;

        WinlineDialog dialogFragment = WinlineDialog.newInstance(message, title, showCancelButton);
        dialogFragment.show(((AppCompatActivity)activity).getSupportFragmentManager(), "dialog");
    }

    public static void showDialog(Activity activity, String message, Boolean showCancelButton){
        if (activity == null || !(activity instanceof AppCompatActivity)) return;

        WinlineDialog dialogFragment = WinlineDialog.newInstance(message, showCancelButton);
        dialogFragment.show(((AppCompatActivity)activity).getSupportFragmentManager(), "dialog");
    }

    public static void showDialog(Activity activity, String message, Boolean showCancelButton, View.OnClickListener okListener){
        if (activity == null || !(activity instanceof AppCompatActivity)) return;

        WinlineDialog dialogFragment = WinlineDialog.newInstance(message, showCancelButton);

        dialogFragment.setOnOkListener(v -> {
            okListener.onClick(v);
            dialogFragment.dismiss();
        });

        dialogFragment.show(((AppCompatActivity)activity).getSupportFragmentManager(), "dialog");
    }

    public static void showDialog(Activity activity, String message, Boolean showCancelButton, View.OnClickListener okListener, View.OnClickListener cancelListener){
        if (activity == null || !(activity instanceof AppCompatActivity)) return;

        WinlineDialog dialogFragment = WinlineDialog.newInstance(message, showCancelButton);

        dialogFragment.setOnOkListener(v -> {
            okListener.onClick(v);
            dialogFragment.dismiss();
        });
        dialogFragment.setOnCancelListener(v -> {
            cancelListener.onClick(v);
            dialogFragment.dismiss();
        });

        dialogFragment.show(((AppCompatActivity)activity).getSupportFragmentManager(), "dialog");
    }
}
