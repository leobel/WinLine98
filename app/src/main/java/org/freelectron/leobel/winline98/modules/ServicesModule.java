package org.freelectron.leobel.winline98.modules;

import android.app.Application;

import org.freelectron.leobel.winline98.services.GameService;
import org.freelectron.leobel.winline98.services.GameServiceImpl;

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

}
