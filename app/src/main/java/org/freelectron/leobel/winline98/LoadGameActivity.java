package org.freelectron.leobel.winline98;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.freelectron.leobel.winline98.models.WinLine;
import org.freelectron.winline.LogicWinLine;

public class LoadGameActivity extends AppCompatActivity implements GameLoadFragment.OnListFragmentInteractionListener {

    public static final String GAME_LOADED = "GAME_LOADED";
    Intent intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);

        intent = getIntent();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.game_list, GameLoadFragment.newInstance(1));
        transaction.commit();
    }

    @Override
    public void loadGame(LogicWinLine item) {
        WinLine game = (WinLine) item;
        intent.putExtra(GAME_LOADED, game);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
