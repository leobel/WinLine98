package org.freelectron.leobel.winline98;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import org.freelectron.leobel.winline98.services.PreferenceService;


import javax.inject.Inject;

public class SplashActivity extends AppCompatActivity {

    @Inject
    public PreferenceService preferenceService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WinLineApp.getInstance().getComponent().inject(this);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent;
            if(preferenceService.getShowInteractiveHelp()){
                preferenceService.setShowInteractiveHelp(false);
                intent = new Intent(SplashActivity.this, MainActivity.class);
            }
            else{
                intent = new Intent(SplashActivity.this, GameActivity.class);
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, 250);


    }
}
