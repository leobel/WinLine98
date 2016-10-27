package org.freelectron.leobel.winline98.dialogs;


import android.app.Dialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import org.freelectron.leobel.winline98.MainActivity;
import org.freelectron.leobel.winline98.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameStatsDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameStatsDialog extends BaseDialog {
    private static final String ARG_SCORE = "ARG_SCORE";
    private static final String ARG_TIME = "ARG_TIME";
    private static final String ARG_HIGHSCORE = "ARG_HIGHSCORE";
    private static final String ARG_ISGAMEOVER = "ARG_ISGAMEOVER";

    private Integer score;
    private Long time;
    private Integer highScore;
    private Boolean isGameOver;

    private TextView scoreText;
    private Chronometer timeText;
    private TextView highScoreText;

    private Button refresGame;
    private Button rateGame;
    private Button shareGame;




    public GameStatsDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param score Parameter 1.
     * @param time Parameter 2.
     * @param highScore Parameter 3.
     * @param isGameOver Parameter 4.
     * @return A new instance of fragment GameStatsDialog.
     */
    public static GameStatsDialog newInstance(Integer score, Long time, Integer highScore, Boolean isGameOver) {
        GameStatsDialog fragment = new GameStatsDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_SCORE, score);
        args.putLong(ARG_TIME, time);
        args.putInt(ARG_HIGHSCORE, highScore);
        args.putBoolean(ARG_ISGAMEOVER, isGameOver);
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
            highScore = getArguments().getInt(ARG_HIGHSCORE);
            isGameOver = getArguments().getBoolean(ARG_ISGAMEOVER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_over_dialog, container, false);

        scoreText = (TextView) view.findViewById(R.id.score);
        timeText = (Chronometer) view.findViewById(R.id.time);
        highScoreText = (TextView) view.findViewById(R.id.high_score);

        refresGame = (Button) view.findViewById(R.id.new_game);
        rateGame = (Button) view.findViewById(R.id.rate_game);
        shareGame = (Button) view.findViewById(R.id.share_game);

        if(!isGameOver){
            if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                refresGame.setBackgroundDrawable(getResources().getDrawable(R.drawable.play));
            } else {
                refresGame.setBackground(getResources().getDrawable(R.drawable.play));
            }
        }

        refresGame.setOnClickListener( v -> {
            onCloseListener.run();
        });

        shareGame.setOnClickListener(v -> {
            MainActivity activity =  (MainActivity)getActivity();

            activity.shareApp();
        });

        scoreText.setText(score.toString());
        timeText.setBase(SystemClock.elapsedRealtime() + time);
        highScoreText.setText(highScore.toString());

        return view;
    }


}
