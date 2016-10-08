package org.freelectron.leobel.winline98.dialogs;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.freelectron.leobel.winline98.R;


public class WinlineDialog extends DialogFragment {
    private static String SHOW_CANCEL_BUTTON = "SHOW_CANCEL_BUTTON";
    private static String MESSAGE = "MESSAGE";
    private static String TITLE = "TITLE";

    private String title;
    private String message;
    private boolean showCancelButton;

    private TextView titleView;
    private Button buttonOk;
    private Button buttonCancel;


    private View.OnClickListener onOkListener = (view) -> dismiss();
    private View.OnClickListener onCancelListener = (view) -> dismiss();

    public WinlineDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param message Parameter 1.
     * @return A new instance of fragment WinlineDialog.
     */
    public static WinlineDialog newInstance(String message) {
        return  WinlineDialog.newInstance(message, "", false);
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param message Parameter 1.
     * @param showCancelButton Parameter 2.
     * @return A new instance of fragment WinlineDialog.
     */
    public static WinlineDialog newInstance(String message, Boolean showCancelButton) {
        return  WinlineDialog.newInstance(message, "", showCancelButton);
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param message Parameter 1.
     * @param title Parameter 2.
     * @return A new instance of fragment WinlineDialog.
     */
    public static WinlineDialog newInstance(String message, String title) {
        return  WinlineDialog.newInstance(message, title, false);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param message Parameter 1.
     * @param title Parameter 2.
     * @param showCancelButton Parameter 3.
     * @return A new instance of fragment WinlineDialog.
     */
    public static WinlineDialog newInstance(String message, String title, Boolean showCancelButton) {
        WinlineDialog fragment = new WinlineDialog();
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        args.putString(TITLE, title);
        args.putBoolean(SHOW_CANCEL_BUTTON, showCancelButton);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_AlertDialog);

        if (getArguments() != null) {
            message = getArguments().getString(MESSAGE);
            title = getArguments().getString(TITLE);
            showCancelButton = getArguments().getBoolean(SHOW_CANCEL_BUTTON, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup topContainer,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup container = (ViewGroup)inflater.inflate(R.layout.fragment_winline_dialog, topContainer, false);

        titleView = (TextView) container.findViewById(R.id.title);
        if(!title.isEmpty()) {
            titleView.setText(title);
        }

        buttonOk = (Button) container.findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(onOkListener);

        buttonCancel = (Button) container.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(onCancelListener);

        if(showCancelButton){
            buttonCancel.setVisibility(View.VISIBLE);
        }
        else{
            buttonCancel.setVisibility(View.GONE);
        }

        TextView textViewMessage = (TextView) container.findViewById(R.id.textViewMessage);
        textViewMessage.setText(message);
        textViewMessage.setMovementMethod(new ScrollingMovementMethod());

        return container;
    }

    public void setOnOkListener(View.OnClickListener listener) {
        this.onOkListener = listener;
    }

    public void setOnCancelListener(View.OnClickListener listener) {
        this.onCancelListener = listener;
    }

}
