package org.freelectron.leobel.winline98.modules;

import android.app.Application;
import android.content.SharedPreferences;

import org.freelectron.leobel.winline98.services.GameService;
import org.freelectron.leobel.winline98.services.GameServiceImpl;
import org.freelectron.leobel.winline98.services.PreferenceService;
import org.freelectron.leobel.winline98.services.PreferenceServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by leobel on 10/7/16.
 */

@Module
public class ServicesModule {

    @Singleton @Provides
    public GameService providesGameService(Application app){
        return new GameServiceImpl(app);
    }

    @Singleton @Provides
    public PreferenceService providesPreferenceService(SharedPreferences sharedPreferences){
        return new PreferenceServiceImpl(sharedPreferences);
    }

}
