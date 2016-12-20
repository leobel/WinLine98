package org.freelectron.leobel.winline98;

import org.freelectron.leobel.winline98.dialogs.GameStatsDialog;
import org.freelectron.leobel.winline98.modules.AppModule;
import org.freelectron.leobel.winline98.modules.ServicesModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by leobel on 10/7/16.
 */

@Singleton
@Component(modules = {AppModule.class, ServicesModule.class})
public interface ApplicationComponent {
    void inject(WinLineApp app);

    void inject(GameActivity activity);

    void inject(GameLoadFragment fragment);

    void inject(GameStatsDialog dialogFragment);

    void inject(InteractiveHelpActivity interactiveHelpActivity);

    void inject(SplashActivity splashActivity);
}
