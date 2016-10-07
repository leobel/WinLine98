package org.freelectron.leobel.winline98;

import android.support.multidex.MultiDexApplication;

import com.squareup.leakcanary.LeakCanary;

import org.freelectron.leobel.winline98.modules.AppModule;
import org.freelectron.leobel.winline98.modules.ServicesModule;

import timber.log.Timber;

/**
 * Created by leobel on 10/7/16.
 */
public class WinLineApp extends MultiDexApplication {

    public ApplicationComponent component;

    private static WinLineApp instance;

    public WinLineApp(){instance = this;}

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Leak Canary
        LeakCanary.install(this);

        // Initialize Timber only if Debug
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        // Initialize Dagger component
        component = DaggerApplicationComponent
                .builder()
                .appModule(new AppModule(this))
                .servicesModule(new ServicesModule())
                .build();
    }

    public ApplicationComponent getComponent() {
        return component;
    }

    public static WinLineApp getInstance(){
        return instance;
    }
}
