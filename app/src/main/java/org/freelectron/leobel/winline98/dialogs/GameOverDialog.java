package org.freelectron.leobel.winline98.dialogs;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.freelectron.leobel.winline98.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameOverDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameOverDialog extends DialogFragment {
    private static final String ARG_SCORE = "ARG_SCORE";
    private static final String ARG_TIME = "ARG_TIME";

    private Integer score;
    private Long time;


    public GameOverDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GameOverDialog.
     */
    public static GameOverDialog newInstance(Integer param1, Long param2) {
        GameOverDialog fragment = new GameOverDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_SCORE, param1);
        args.putLong(ARG_TIME, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_AlertDialog);
        if (getArguments() != null) {
            score = getArguments().getInt(ARG_SCORE);
            time = getArguments().getLong(ARG_TIME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_over_dialog, container, false);

        return view;
    }

}
