package org.freelectron.leobel.winline98;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.freelectron.leobel.winline98.models.WinLine;
import org.freelectron.winline.LogicWinLine;

public class LoadGameActivity extends AppCompatActivity implements GameLoadFragment.OnListFragmentInteractionListener {

    public static final String GAME_LOADED = "GAME_LOADED";
    Intent intent;
    private GameLoadFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        intent = getIntent();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fragment = GameLoadFragment.newInstance(1);
        transaction.add(R.id.game_list, fragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sort_by, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id != R.id.sort_by)
            fragment.orderGameBy(id);
        return true;

        //return super.onOptionsItemSelected(item);
    }

    @Override
    public void loadGame(LogicWinLine item) {
        WinLine game = (WinLine) item;
        intent.putExtra(GAME_LOADED, game);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
