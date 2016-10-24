package org.freelectron.leobel.winline98;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import org.freelectron.leobel.winline98.models.WinLine;
import org.freelectron.leobel.winline98.utils.ActivityUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by leobel on 10/24/16.
 */
public class BaseActivity extends AppCompatActivity {

    public void shareSavedGames(List<Bitmap> screenShots){
        ShareSavedGamesAsyncTask task = new ShareSavedGamesAsyncTask();
        task.execute(screenShots);
    }

    public class ShareSavedGamesAsyncTask extends AsyncTask<List<Bitmap>, Integer, List<File>>{

        private final ProgressDialog mProgressDialog;

        public ShareSavedGamesAsyncTask(){

            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMessage("Your request is being processes");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        public Activity getActivity() {
            return BaseActivity.this;
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
                String facebookAppName = getString(R.string.facebokk_app_name);
                if(ActivityUtils.isAppInstalled(getActivity(), facebookAppName)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Share with");
                    builder.setItems(new CharSequence[] {"Facebook", "Other"}, (dialog, which) -> {
                        switch (which){
                            case 0:
                                List<SharePhoto> photos = new ArrayList<>(files.size());
                                for(File file : files) {
                                    SharePhoto photo = new SharePhoto.Builder()
                                            .setImageUrl(Uri.fromFile(file))
                                            .setCaption("WinLine")
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
                                intent.putExtra(Intent.EXTRA_SUBJECT, "Play WinLine");
                                //intent.putExtra(Intent.EXTRA_TEXT, "http://winline.org");
                                intent.setType("image/*");
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                ArrayList<Uri> uris = new ArrayList<>(files.size());
                                for(File file : files) {
                                    uris.add(Uri.fromFile(file));
                                }
                                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

                                // Exclude Facebook from Apps
                                List<ResolveInfo> activities = getPackageManager().queryIntentActivities(intent, 0);
                                ArrayList<Intent> targetIntents = new ArrayList<>();

                                for (ResolveInfo currentInfo : activities) {
                                    String packageName = currentInfo.activityInfo.packageName;
                                    if (!facebookAppName.equals(packageName)) {
                                        Intent targetIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

                                        targetIntent.putExtra(Intent.EXTRA_SUBJECT, "Play WinLine");
                                        targetIntent.setType("image/*");
                                        targetIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                                        targetIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

                                        targetIntent.setPackage(packageName);
                                        targetIntents.add(targetIntent);
                                    }
                                }

                                if(targetIntents.size() > 0) {
                                    Intent chooserIntent = Intent.createChooser(targetIntents.remove(0), "Share with");
                                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetIntents.toArray(new Parcelable[targetIntents.size()]));
                                    startActivity(chooserIntent);
                                }
                                else {
                                    Toast.makeText(getActivity(), "No app found", Toast.LENGTH_SHORT).show();
                                }

                                break;
                        }
                    });

                    builder.show();
                }
                else{
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Play WinLine");
                    //intent.putExtra(Intent.EXTRA_TEXT, "http://winline.org");
                    intent.setType("image/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    ArrayList<Uri> uris = new ArrayList<>(files.size());
                    for(File file : files) {
                        uris.add(Uri.fromFile(file));
                    }
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

                    startActivity(Intent.createChooser(intent, "Share with"));
                }
            }, 500); // 1/2 seconds delay
        }
    }
}