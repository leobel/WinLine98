package org.freelectron.leobel.winline98;

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

}
