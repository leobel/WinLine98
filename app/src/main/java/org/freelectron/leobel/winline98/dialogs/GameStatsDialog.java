package org.freelectron.leobel.winline98.dialogs;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import org.freelectron.leobel.winline98.MainActivity;
import org.freelectron.leobel.winline98.R;
import org.freelectron.leobel.winline98.WinLineApp;
import org.freelectron.leobel.winline98.services.PreferenceService;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameStatsDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameStatsDialog extends BaseDialog {
    private static final String ARG_SCORE = "ARG_SCORE";
    private static final String ARG_TIME = "ARG_TIME";
    private static final String ARG_HIGH_SCORE = "ARG_HIGH_SCORE";
    private static final String ARG_IS_GAME_OVER = "ARG_IS_GAME_OVER";
    private static final String ARG_SHOW_RECORD_INFO = "ARG_SHOW_RECORD_INFO";

    private Integer score;
    private Long time;
    private Integer highScore;
    private Boolean isGameOver;
    private Boolean showRecordInfo;

    private TextView scoreText;
    private Chronometer timeText;
    private TextView highScoreText;
    private ViewGroup newRecordView;

    private ImageButton refresGame;
    private ImageButton rateGame;
    private ImageButton shareGame;


    @Inject
    public PreferenceService preferenceService;

    private MediaPlayer mp;


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
        return newInstance(score, time, highScore, isGameOver, false);
    }

    public static GameStatsDialog newInstance(Integer score, Long time, Integer highScore, Boolean isGameOver, Boolean showRecordInfo) {
        GameStatsDialog fragment = new GameStatsDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_SCORE, score);
        args.putLong(ARG_TIME, time);
        args.putInt(ARG_HIGH_SCORE, highScore);
        args.putBoolean(ARG_IS_GAME_OVER, isGameOver);
        args.putBoolean(ARG_SHOW_RECORD_INFO, showRecordInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WinLineApp.getInstance().getComponent().inject(this);

        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_AlertDialog);

        mp = MediaPlayer.create(getActivity(), R.raw.buton_click);

        if (getArguments() != null) {
            score = getArguments().getInt(ARG_SCORE);
            time = getArguments().getLong(ARG_TIME);
            highScore = getArguments().getInt(ARG_HIGH_SCORE);
            isGameOver = getArguments().getBoolean(ARG_IS_GAME_OVER);
            showRecordInfo = getArguments().getBoolean(ARG_SHOW_RECORD_INFO, false);
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
        newRecordView = (ViewGroup) view.findViewById(R.id.new_record_view);

        refresGame = (ImageButton) view.findViewById(R.id.new_game);
        rateGame = (ImageButton) view.findViewById(R.id.rate_game);
        shareGame = (ImageButton) view.findViewById(R.id.share_game);

        if(showRecordInfo){
            newRecordView.setVisibility(View.VISIBLE);
        }

        if(!isGameOver){
            refresGame.setImageResource(R.drawable.ic_play);
        }

        rateGame.setOnClickListener( v -> {
            if(preferenceService.getAllowTouchSoundPreference()){
                mp.start();
            }
        });

        refresGame.setOnClickListener( v -> {
            if(preferenceService.getAllowTouchSoundPreference()){
                mp.start();
            }
            onCloseListener.run();
        });

        shareGame.setOnClickListener(v -> {
            if(preferenceService.getAllowTouchSoundPreference()){
                mp.start();
            }
            MainActivity activity =  (MainActivity)getActivity();
            activity.shareApp();
        });

        scoreText.setText(score.toString());
        timeText.setBase(SystemClock.elapsedRealtime() + time);
        highScoreText.setText(highScore.toString());

        return view;
    }


}
