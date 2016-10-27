package org.freelectron.leobel.winline98;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import org.freelectron.leobel.winline98.models.WinLine;
import org.freelectron.leobel.winline98.utils.ActivityUtils;
import org.freelectron.winline.LogicWinLine;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class LoadGameActivity extends BaseActivity implements GameLoadFragment.OnListFragmentInteractionListener {

    public static final String GAME_LOADED = "GAME_LOADED";
    Intent intent;
    private GameLoadFragment fragment;
    private boolean selectMultipleItems;

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private AppCompatCheckBox toolbarCheckBox;
    private Integer selectedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbarCheckBox = (AppCompatCheckBox) toolbar.findViewById(R.id.toolbar_checkbox);
        setSupportActionBar(toolbar);

        toolbarTitle.setVisibility(selectMultipleItems ? View.VISIBLE : View.GONE);
        toolbarCheckBox.setVisibility(selectMultipleItems ? View.VISIBLE : View.GONE);

        selectedItems = 0;

        intent = getIntent();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fragment = GameLoadFragment.newInstance(1);
        transaction.add(R.id.game_list, fragment);
        transaction.commit();

        toolbarCheckBox.setOnClickListener(v -> {
            boolean callCreateOptionsMenu = false;
            if(toolbarCheckBox.isChecked()){
                fragment.checkAllItems();
                callCreateOptionsMenu = selectedItems == 0;
                selectedItems = fragment.getItemsCount();

            }
            else{
                fragment.unCheckAllItems();
                callCreateOptionsMenu = selectedItems > 0;
                selectedItems = 0;
            }

            toolbarTitle.setText(String.format(getString(R.string.delete_selected), selectedItems));
            if(callCreateOptionsMenu){
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_list, menu);
        MenuItem share = menu.findItem(R.id.share_item);
        MenuItem trash = menu.findItem(R.id.delete_item);
        MenuItem sort = menu.findItem(R.id.sort_by);
        share.setVisible(selectMultipleItems && selectedItems > 0);
        trash.setVisible(share.isVisible());
        sort.setVisible(!selectMultipleItems);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.delete_item){
            ActivityUtils.showDialog(this, getString(selectedItems > 1 ? R.string.delete_games : R.string.delete_game), true, () -> {
                fragment.removeItems();
                selectMultipleItems(false);
            }, () -> {

            });
        }
        else if(id == R.id.share_item){
            if(checkPermission(MY_PERMISSIONS_REQUEST_ACCESS_STORAGE, R.string.use_external_storage_explanation, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)){
                shareSavedGames(fragment.getScreenShots());
            }
        }
        else if(id != R.id.sort_by){
            fragment.orderGameBy(id);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(selectMultipleItems){
            selectMultipleItems(false);
            fragment.cancelSelectMultipleItemsMode();
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public void loadGame(LogicWinLine item) {
        WinLine game = (WinLine) item;
        intent.putExtra(GAME_LOADED, game);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void selectMultipleItems(boolean status) {
        this.selectMultipleItems = status;
        toolbarTitle.setVisibility(selectMultipleItems ? View.VISIBLE : View.GONE);
        toolbarCheckBox.setVisibility(selectMultipleItems ? View.VISIBLE : View.GONE);
        getSupportActionBar().setTitle(selectMultipleItems ? null : getString(R.string.app_name));
        selectedItems = selectMultipleItems ? 1 : 0;
        toolbarTitle.setText(String.format(getString(R.string.delete_selected), selectedItems));
        toolbarCheckBox.setChecked(selectedItems == fragment.getItemsCount());
        invalidateOptionsMenu();
    }

    @Override
    public void notifySelectMultipleItemsClicked(Integer count) {
        boolean callCreateOptionsMenu = false;
        toolbarTitle.setText(String.format(getString(R.string.delete_selected), count));
        if(count == 0 || (selectedItems == 0 && count > 0)){
            callCreateOptionsMenu = true;
        }
        selectedItems = count;
        toolbarCheckBox.setChecked(selectedItems == fragment.getItemsCount());
        if(callCreateOptionsMenu){
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // storage-related task you need to do.
                    shareSavedGames(fragment.getScreenShots());

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }

}
