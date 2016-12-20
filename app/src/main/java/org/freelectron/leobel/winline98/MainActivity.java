package org.freelectron.leobel.winline98;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.freelectron.leobel.winline98.services.PreferenceService;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button demo = (Button) findViewById(R.id.demo);

        demo.setOnClickListener(view -> {
            Intent intent = new Intent(this, InteractiveHelpActivity.class);
            intent.putExtra(InteractiveHelpActivity.LAUNCH_GAME_ACTIVITY, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        ShowcaseView showcaseView = new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(demo))
                .setOnClickListener(this)
                .build();
        showcaseView.setButtonText("Skip");
        showcaseView.setContentTitle(getString(R.string.winline_help));
        showcaseView.setContentText(getString(R.string.winline_help_description));


    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
