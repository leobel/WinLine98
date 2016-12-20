package org.freelectron.leobel.winline98;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by leobel on 12/13/16.
 */

public class HelpActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winline_help);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView description = (TextView) findViewById(R.id.winline_description);
        description.setText(Html.fromHtml(String.format("<div>" +
                "<b>&#8226; %s</b><br/><br/>" +
                "<b>&#8226; %s</b><br/><br/>" +
                "<b>&#8226; %s</b><br/><br/>" +
                "<b>&#8226; %s</b><br/><br/>" +
                "<b>&#8226; %s</b><br/><br/>" +
                "<b>&#8226; %s</b><br/><br/>" +
                "<b>&#8226; %s</b><br/><br/>" +
                "<b>&#8226; %s</b><br/><br/>" +
                "<b><font color=#FF0000> &#8226; %s</font></b></div>",
                getString(R.string.about_winline_description),
                getString(R.string.about_winline_description1),
                getString(R.string.about_winline_description2),
                getString(R.string.about_winline_description3),
                getString(R.string.about_winline_description4),
                getString(R.string.about_winline_description5),
                getString(R.string.about_winline_description6),
                getString(R.string.about_winline_description7),
                getString(R.string.about_winline_description8))));

        Button demo = (Button) findViewById(R.id.winline_help_demo);
        demo.setOnClickListener(view -> {
            startActivity(new Intent(this, InteractiveHelpActivity.class));
        });
    }
}
