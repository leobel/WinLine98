package org.freelectron.leobel.winline98.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.freelectron.leobel.winline98.R;

/**
 * Created by leobel on 12/13/16.
 */

public class HelpDialog extends BaseDialog {

    public HelpDialog(){

    }

    public static HelpDialog newInstance(){
        return new HelpDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_AlertDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup topContainer,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup container =  (ViewGroup)inflater.inflate(R.layout.fragment_winline_help, topContainer, false);

        Button buttonOk = (Button) container.findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(v -> {
            dismiss();
        });

        return container;
    }
}
