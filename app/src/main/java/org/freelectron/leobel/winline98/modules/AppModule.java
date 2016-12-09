package org.freelectron.leobel.winline98.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by leobel on 10/7/16.
 */

@Module
public class AppModule {

    private Application app;

    public AppModule(Application app) {
        this.app = app;
    }

    @Singleton
    @Provides
    public Application providesApplication(){ return app; }

    @Singleton @Provides
    public SharedPreferences providesSharedPreference(Application app) {
        return app.getSharedPreferences(app.getPackageName(), Context.MODE_PRIVATE);
    }
}
