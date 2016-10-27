package org.freelectron.leobel.winline98.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Created by leobel on 10/27/16.
 */
public class BaseDialog extends DialogFragment {

    protected Runnable onCloseListener = () -> dismiss();

    public void setOnCloseListener(Runnable listener) {
        this.onCloseListener = () -> {
            listener.run();
            dismiss();
        };
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                onCloseListener.run();
            }
        };
    }
}
