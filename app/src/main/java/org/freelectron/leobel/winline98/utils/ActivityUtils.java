package org.freelectron.leobel.winline98.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.freelectron.leobel.winline98.dialogs.WinlineDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

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

    public static String formatFullDate(Long date){
        return DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(new Date(date));
    }

    public static File storeScreenShot(Bitmap bm, String fileName){
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File(dir, fileName);
        try {
            dir.mkdirs();
            Timber.d("File folder path: " + dir.getAbsolutePath());
            Timber.d("File path: " + file.getAbsolutePath());
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    public static boolean isAppInstalled(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }
}
