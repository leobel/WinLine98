package org.freelectron.leobel.winline98.services;

import android.content.Context;

import org.freelectron.leobel.winline98.models.CheckerRealm;
import org.freelectron.leobel.winline98.models.GameRealm;
import org.freelectron.winline.Checker;
import org.freelectron.winline.LogicWinLine;
import org.freelectron.winline.MPoint;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.exceptions.RealmException;
import timber.log.Timber;

/**
 * Created by leobel on 10/7/16.
 */
public class GameServiceImpl implements GameService {

    Context context;


    public GameServiceImpl(Context context){
            this.context = context;
    }

    @Override
    public Boolean save(LogicWinLine game, int time) {
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder(context);
        Realm realm = Realm.getInstance(builder.build());
        boolean result = true;
        try {
            realm.beginTransaction();

            GameRealm gameRealm = realm.createObject(GameRealm.class);
            gameRealm.copyFrom(game, realm, time);

            realm.commitTransaction();
        } catch (RuntimeException e) {
            Timber.d(e.getMessage());
            result = false;
            try {
                realm.cancelTransaction();
            } catch (Exception e1) {
                Timber.d(e1, "This should never happened");
            }
        }

        try {
            realm.close();
        } catch (RealmException ex) {
            Timber.d(ex, "This should never happened");

        }
        return result;
    }

    @Override
    public Boolean remove(Long id) {
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder(context);
        Realm realm = Realm.getInstance(builder.build());
        boolean result = true;
        try {
            realm.beginTransaction();

            result = realm.where(GameRealm.class)
                    .equalTo("id", id)
                    .findAll()
                    .deleteAllFromRealm();

            realm.commitTransaction();
        } catch (RuntimeException e) {
            try {
                realm.cancelTransaction();
            } catch (Exception e1) {
                Timber.d(e1, "This should never happened");
            }

        }

        try {
            realm.close();
        } catch (RealmException ex) {
            Timber.d(ex, "This should never happened");
        }

        return result;
    }

    @Override
    public Boolean remove(List<Long> ids) {
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder(context);
        Realm realm = Realm.getInstance(builder.build());
        boolean result = true;
        try {
            realm.beginTransaction();
            for(Long id: ids){
                result = result && realm.where(GameRealm.class)
                    .equalTo("id", id)
                    .findAll()
                    .deleteAllFromRealm();
            }
            realm.commitTransaction();
        } catch (RuntimeException e) {
            try {
                realm.cancelTransaction();
            } catch (Exception e1) {
                Timber.d(e1, "This should never happened");
            }
        }

        try {
            realm.close();
        } catch (RealmException ex) {
            Timber.d(ex, "This should never happened");
        }
        return result;
    }

    @Override
    public LogicWinLine findById(Long id) {
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder(context);
        Realm realm = Realm.getInstance(builder.build());
        LogicWinLine game = null;
        try{
            GameRealm gameRealm = realm.where(GameRealm.class).equalTo("id", id).findFirst();
            if(gameRealm != null){
               game = gameRealm.toModel();
            }
        }
        catch (Exception e1) {
            Timber.d(e1, "This should never happened");
        }

        try{
            realm.close();
        }
        catch (RealmException ex) {
            Timber.d(ex, "This should never happened");
        }
        return game;
    }

    @Override
    public List<LogicWinLine> findAll() {
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder(context);
        Realm realm = Realm.getInstance(builder.build());
        List<LogicWinLine> games = new ArrayList<>();

        try{
            for(GameRealm gameRealm: realm.where(GameRealm.class).findAll()){
                games.add(gameRealm.toModel());
            }
        }
        catch (Exception e1) {
            Timber.d(e1, "This should never happened");
        }

        try{
            realm.close();
        }
        catch (RealmException ex) {
            Timber.d(ex, "This should never happened");
        }
        return games;
    }
}
