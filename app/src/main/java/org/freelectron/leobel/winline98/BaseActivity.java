package org.freelectron.leobel.winline98;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import org.freelectron.leobel.winline98.utils.ActivityUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leobel on 10/24/16.
 */
public class BaseActivity extends AppCompatActivity {

    public static final int SHARE_APP = 89;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_STORAGE = 123;

    private  ShareSavedGamesAsyncTask task;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void shareSavedGames(List<Bitmap> screenShots){
        task = new ShareSavedGamesAsyncTask(this);
        task.execute(screenShots);
    }

    public void rateApp() {
        Uri uri = Uri.parse(getString(R.string.play_store_direct_url) + getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
        else{
            Toast.makeText(this, getString(R.string.share_app_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    public void shareApp(){
        shareApp(null, null);
    }

    public void shareApp(Runnable onCancelListener, Runnable onDismissListener) {

        String shareLink = getAppLink();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(onCancelListener != null){
            builder.setOnCancelListener(dialog -> {
                onCancelListener.run();
            });
        }
        if(onDismissListener != null){
            builder.setOnDismissListener(dialog -> {
               onDismissListener.run();
            });
        }
        builder.setTitle(getString(R.string.share_with));
        builder.setItems(new CharSequence[] {getString(R.string.share_facebook), getString(R.string.share_other)}, (dialog, which) -> {
            switch (which){
                case 0:
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle(getString(R.string.share_title))
                            .setContentDescription(getString(R.string.share_description))
                            .setContentUrl(Uri.parse(shareLink))
                            .build();

                    ShareDialog.show(this, linkContent);
                    break;
                case 1:
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType(getString(R.string.share_text_plain));
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_title));
                    intent.putExtra(Intent.EXTRA_TEXT, shareLink);


                    // Exclude Facebook from Apps
                    List<ResolveInfo> activities = getPackageManager().queryIntentActivities(intent, 0);

                    ArrayList<Intent> targetIntents = new ArrayList<>();
                    String facebookAppName = getString(R.string.facebokk_app_name);
                    for (ResolveInfo currentInfo : activities) {
                        String packageName = currentInfo.activityInfo.packageName;
                        if (!facebookAppName.equals(packageName)) {
                            Intent targetIntent = new Intent(Intent.ACTION_SEND);
                            targetIntent.setType(getString(R.string.share_text_plain));
                            targetIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_title));
                            targetIntent.putExtra(Intent.EXTRA_TEXT, shareLink);

                            targetIntent.setPackage(packageName);
                            targetIntents.add(targetIntent);
                        }
                    }

                    if(targetIntents.size() > 0) {
                        Intent chooserIntent = Intent.createChooser(targetIntents.remove(0), getString(R.string.share_with));
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetIntents.toArray(new Parcelable[targetIntents.size()]));
                        startActivity(chooserIntent);
                    }
                    else {
                        Toast.makeText(this, getString(R.string.share_app_not_found), Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        });

        builder.show();
    }

    @Override
    protected void onDestroy() {
        if(task != null){
            task.cancel(true);
        }
        super.onDestroy();
    }

    @NonNull
    public String getAppLink() {
        String appPackageName = getPackageName();
        return getString(R.string.play_store_url) + appPackageName;
    }

    public boolean checkPermission(int requestCode, int permissionExplanation, String... permissions) {
        boolean permissionGranted = true;
        for (String permission: permissions) {
            if(ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                permissionGranted = false;
                break;
            }
        }
        if (!permissionGranted) {
            // Should we show an explanation?
            boolean shouldShowRequestPermissionRationale = true;
            for (String permission: permissions) {
                if(!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
                    shouldShowRequestPermissionRationale = false;
                    break;
                }
            }
            if (shouldShowRequestPermissionRationale) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                ActivityUtils.showDialog(this, getString(permissionExplanation), getString(R.string.permission_title), false, () -> {
                    ActivityCompat.requestPermissions(this, permissions, requestCode);
                });

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, permissions, requestCode);
            }
            return false;
        }
        return true;
    }

    public static class ShareSavedGamesAsyncTask extends AsyncTask<List<Bitmap>, Integer, List<File>>{

        private BaseActivity mActivity;
        private ProgressDialog mProgressDialog;

        public ShareSavedGamesAsyncTask(BaseActivity activity){
            mActivity = activity;
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMessage(mActivity.getString(R.string.dialog_process_request));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        @Override
        protected void onCancelled() {
            mProgressDialog.dismiss();
        }

        public Activity getActivity() {
            return mActivity;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected List<File> doInBackground(List<Bitmap> ... params) {
            List<Bitmap> screenShots = params[0];
            List<File> files = new ArrayList<>(screenShots.size());
            for(int i = 0; i < screenShots.size(); i++){
                files.add(ActivityUtils.storeScreenShot(screenShots.get(i), String.format("share_game_%d.png", i)));
            }

            return files;
        }

        @Override
        protected void onPostExecute(List<File> files) {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                mProgressDialog.hide();
                String shareLink = mActivity.getAppLink();
                String facebookAppName = mActivity.getString(R.string.facebokk_app_name);
                if(ActivityUtils.isAppInstalled(getActivity(), facebookAppName)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(mActivity.getString(R.string.share_with));
                    builder.setItems(new CharSequence[] {mActivity.getString(R.string.share_facebook), mActivity.getString(R.string.share_other)}, (dialog, which) -> {
                        switch (which){
                            case 0:
                                List<SharePhoto> photos = new ArrayList<>(files.size());
                                for(File file : files) {
                                    SharePhoto photo = new SharePhoto.Builder()
                                            .setImageUrl(Uri.fromFile(file))
                                            .setCaption(mActivity.getString(R.string.share_title))
                                            .build();
                                    photos.add(photo);
                                }
                                SharePhotoContent content = new SharePhotoContent.Builder()
                                        .addPhotos(photos)
                                        .build();
                                ShareDialog.show(getActivity(), content);
                                break;
                            case 1:
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                                intent.putExtra(Intent.EXTRA_SUBJECT, mActivity.getString(R.string.share_title));
                                intent.putExtra(Intent.EXTRA_TEXT, shareLink);
                                intent.setType(mActivity.getString(R.string.share_image));
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                ArrayList<Uri> uris = new ArrayList<>(files.size());
                                for(File file : files) {
                                    uris.add(Uri.fromFile(file));
                                }
                                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

                                // Exclude Facebook from Apps
                                List<ResolveInfo> activities = mActivity.getPackageManager().queryIntentActivities(intent, 0);
                                ArrayList<Intent> targetIntents = new ArrayList<>();

                                for (ResolveInfo currentInfo : activities) {
                                    String packageName = currentInfo.activityInfo.packageName;
                                    if (!facebookAppName.equals(packageName)) {
                                        Intent targetIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

                                        targetIntent.putExtra(Intent.EXTRA_SUBJECT, mActivity.getString(R.string.share_title));
                                        targetIntent.putExtra(Intent.EXTRA_TEXT, shareLink);
                                        targetIntent.setType(mActivity.getString(R.string.share_image));
                                        targetIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                                        targetIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

                                        targetIntent.setPackage(packageName);
                                        targetIntents.add(targetIntent);
                                    }
                                }

                                if(targetIntents.size() > 0) {
                                    Intent chooserIntent = Intent.createChooser(targetIntents.remove(0), mActivity.getString(R.string.share_with));
                                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetIntents.toArray(new Parcelable[targetIntents.size()]));
                                    mActivity.startActivity(chooserIntent);
                                }
                                else {
                                    Toast.makeText(getActivity(), mActivity.getString(R.string.share_app_not_found), Toast.LENGTH_SHORT).show();
                                }

                                break;
                        }
                    });

                    builder.show();
                }
                else{
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    intent.putExtra(Intent.EXTRA_SUBJECT, mActivity.getString(R.string.share_title));
                    intent.putExtra(Intent.EXTRA_TEXT, shareLink);
                    intent.setType(mActivity.getString(R.string.share_image));
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    ArrayList<Uri> uris = new ArrayList<>(files.size());
                    for(File file : files) {
                        uris.add(Uri.fromFile(file));
                    }
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

                    mActivity.startActivity(Intent.createChooser(intent, mActivity.getString(R.string.share_with)));
                }
            }, 500); // 1/2 seconds delay
        }
    }
}
