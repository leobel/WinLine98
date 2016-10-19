package org.freelectron.leobel.winline98;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.freelectron.leobel.winline98.models.WinLine;
import org.freelectron.winline.LogicWinLine;

public class LoadGameActivity extends AppCompatActivity implements GameLoadFragment.OnListFragmentInteractionListener {

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
        getMenuInflater().inflate(R.menu.sort_by, menu);
        MenuItem trash = menu.findItem(R.id.delete_item);
        MenuItem sort = menu.findItem(R.id.sort_by);
        trash.setVisible(selectMultipleItems && selectedItems > 0);
        sort.setVisible(!selectMultipleItems);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.delete_item){
            fragment.removeItems();
            selectMultipleItems(false);
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
}
