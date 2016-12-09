package org.freelectron.leobel.winline98;

import android.content.Context;
import android.provider.Settings;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.SmallTest;

import org.freelectron.leobel.winline98.models.GameRealm;
import org.freelectron.leobel.winline98.services.GameService;
import org.freelectron.leobel.winline98.services.GameServiceImpl;
import org.freelectron.winline.LogicWinLine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.CoreMatchers.*;

/**
 * Created by leobel on 12/2/16.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class GameServiceTest {

    Context mockContext;
    GameService gameService;

    @Before
    public void setUp(){
        mockContext = new RenamingDelegatingContext(InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_");
        gameService = new GameServiceImpl(mockContext);
    }

    @Test
    public void save_Ok(){
        clearDatabase();

        LogicWinLine game = getTestLogicWinline();

        boolean saveResult = gameService.save(game, (int)System.currentTimeMillis());

        RealmConfiguration.Builder builder = new RealmConfiguration.Builder(mockContext);
        Realm realm = Realm.getInstance(builder.build());

        GameRealm gameRealm = realm.where(GameRealm.class).findFirst();

        assertThat(saveResult, is(true));
        assertThat(gameRealm, not(nullValue()));

    }

    @Test
    public void findById_Ok(){
        clearDatabase();

        RealmConfiguration.Builder builder = new RealmConfiguration.Builder(mockContext);
        Realm realm = Realm.getInstance(builder.build());

        realm.beginTransaction();

        GameRealm gameRealm = realm.createObject(GameRealm.class);
        LogicWinLine game = getTestLogicWinline();

        gameRealm.copyFrom(game, realm, (int)System.currentTimeMillis());

        realm.commitTransaction();

        LogicWinLine resultGame = gameService.findById(gameRealm.getId());

        assertThat(resultGame, not(nullValue()));
    }

    @Test
    public void findAll_Ok(){
        clearDatabase();

        RealmConfiguration.Builder builder = new RealmConfiguration.Builder(mockContext);
        Realm realm = Realm.getInstance(builder.build());

        realm.beginTransaction();

        LogicWinLine game = getTestLogicWinline();

        GameRealm gameRealm1 = realm.createObject(GameRealm.class);
        gameRealm1.copyFrom(game, realm, (int)System.currentTimeMillis());

        GameRealm gameRealm2 = realm.createObject(GameRealm.class);
        gameRealm2.copyFrom(game, realm, (int)System.currentTimeMillis());

        realm.commitTransaction();

        List<LogicWinLine> result = gameService.findAll();

        assertThat(result, not(nullValue()));
        assertThat(result.size(), is(2));
    }

    @Test
    public void remove_Ok(){
        clearDatabase();

        RealmConfiguration.Builder builder = new RealmConfiguration.Builder(mockContext);
        Realm realm = Realm.getInstance(builder.build());

        realm.beginTransaction();

        LogicWinLine game = getTestLogicWinline();

        GameRealm gameRealm1 = realm.createObject(GameRealm.class);
        gameRealm1.copyFrom(game, realm, (int)System.currentTimeMillis());

        GameRealm gameRealm2 = realm.createObject(GameRealm.class);
        gameRealm2.copyFrom(game, realm, (int)System.currentTimeMillis() + 100);

        realm.commitTransaction();

        Boolean result = gameService.remove(gameRealm1.getId());

        List<LogicWinLine> games = gameService.findAll();

        assertThat(result, is(true));
        assertThat(games, not(nullValue()));
        assertThat(games.size(), is(1));
        assertThat(games.get(0).getScore(), is(gameRealm2.getScore()));
    }

    @Test
    public void removeList_Ok(){
        clearDatabase();

        RealmConfiguration.Builder builder = new RealmConfiguration.Builder(mockContext);
        Realm realm = Realm.getInstance(builder.build());

        realm.beginTransaction();

        LogicWinLine game = getTestLogicWinline();

        GameRealm gameRealm1 = realm.createObject(GameRealm.class);
        gameRealm1.copyFrom(game, realm, (int)System.currentTimeMillis());

        GameRealm gameRealm2 = realm.createObject(GameRealm.class);
        int score = 100;
        game.addScore(score);
        gameRealm2.copyFrom(game, realm, (int)System.currentTimeMillis() + 100);

        GameRealm gameRealm3 = realm.createObject(GameRealm.class);
        game.addScore(-score);
        gameRealm3.copyFrom(game, realm, (int)System.currentTimeMillis() + 150);

        realm.commitTransaction();

        List<Long> ids = new ArrayList<>(2);
        ids.add(gameRealm3.getId());
        ids.add(gameRealm1.getId());

        Boolean result = gameService.remove(ids);

        List<LogicWinLine> games = gameService.findAll();

        assertThat(result, is(true));
        assertThat(games, not(nullValue()));
        assertThat(games.size(), is(1));
        assertThat(games.get(0).getScore(), is(score));
    }

    private void clearDatabase(){
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder(mockContext);
        Realm realm = Realm.getInstance(builder.build());

        realm.beginTransaction();
        realm.where(GameRealm.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    private LogicWinLine getTestLogicWinline() {
        LogicWinLine testLogicWinline = null;
        try {
            testLogicWinline = new LogicWinLine(9);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return testLogicWinline;
    }
}
