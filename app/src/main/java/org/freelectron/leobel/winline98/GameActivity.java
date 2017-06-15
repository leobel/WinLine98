package org.freelectron.leobel.winline98;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;

import org.freelectron.leobel.winline98.dialogs.GameStatsDialog;
import org.freelectron.leobel.winline98.models.GameProgress;
import org.freelectron.leobel.winline98.models.WinLine;
import org.freelectron.leobel.winline98.services.GameService;
import org.freelectron.leobel.winline98.services.PreferenceService;
import org.freelectron.leobel.winline98.utils.ActivityUtils;
import org.freelectron.leobel.winline98.utils.GameAnimation;
import org.freelectron.winline.Checker;
import org.freelectron.winline.LogicWinLine;
import org.freelectron.winline.MPoint;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

public class GameActivity extends BaseActivity
        implements GameAnimation, NavigationView.OnNavigationItemSelectedListener,  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int LOAD_GAME = 1;
    private static final String DIALOG_ERROR = "DIALOG_ERROR";
    private static final String STATE_RESOLVING_ERROR = "STATE_RESOLVING_ERROR";
    private static final int REQUEST_RESOLVE_ERROR = 101;
    private static final int REQUEST_LEADER_BOARD = 102;
    private static final int REQUEST_ACHIEVEMENTS = 103;
    private static final int ACHIEVEMENT_BEGINNER_SCORER_THRESHOLD = 500;
    private static final int ACHIEVEMENT_GOOD_SCORER_THRESHOLD = 1000;
    private static final int ACHIEVEMENT_GREAT_SCORER_THRESHOLD = 5000;
    private static final int ACHIEVEMENT_AWESOME_SCORER_THRESHOLD = 50000;
    private static final int ACHIEVEMENT_NINJA_SCORER_THRESHOLD = 100000;
    private static final int ACHIEVEMENT_GREEDY_MOVE_THRESHOLD = 7;
    private static final int ACHIEVEMENT_STRATEGIC_MOVE_THRESHOLD = 12;
    private static final int ACHIEVEMENT_AMAZING_MOVE_THRESHOLD = 18;
    private static final int ACHIEVEMENT_COMBO_BEGINNER_THRESHOLD = 3;
    private static final int ACHIEVEMENT_COMBO_FLUENCY_THRESHOLD = 12;
    private static final int ACHIEVEMENT_COMBONATOR_THRESHOLD = 20;

    private static final String STATE_GAME_PROGRESS = "STATE_GAME_PROGRESS";
    private static final String STATE_GAME = "STATE_GAME";
    private static final String STATE_LOAD_GAME_ON_START = "STATE_LOAD_GAME_ON_START";
    private static final String STATE_CAN_PLAY = "STATE_CAN_PLAY";
    private static final String STATE_TIME_WHEN_STOPPED = "STATE_TIME_WHEN_STOPPED";
    private static final String STATE_COMBO_IS_RUNNING = "STATE_COMBO_IS_RUNNING";
    private static final String STATE_COMBO_TIME = "STATE_COMBO_TIME";
    private static final String STATE_COMBO_VALUE = "STATE_COMBO_VALUE";
    private static final String STATE_ANIMATE_SELECTED_BALL_IS_RUNNING = "STATE_ANIMATE_SELECTED_BALL_IS_RUNNING";
    private static final String STATE_ORIGIN_POINT = "STATE_ORIGIN_POINT";
    private static final String STATE_BREAK_RECORD_ALERT = "STATE_BREAK_RECORD_ALERT";
    private static final String STATE_HANDLE_BOARD_TOUCH = "STATE_HANDLE_BOARD_TOUCH";

    private static String HIGHLIGHT_COMBO_SCORE = "HIGHLIGHT_COMBO_SCORE";
    private static String PREVIOUS_GAME_SCORE = "PREVIOUS_GAME_SCORE";
    private static String BALLS_SCORE = "BALLS_SCORE";


    private AnimateMoveTails animateMoveTails;
    private AnimateInsertTails animateInsertTails;
    private AnimateScore animateScore;
    private AnimateSelectedTail animateSelectedTail;
    private boolean animateSelectedBallIsRunning;

    private View boardContainer;
    private BoardView boardView;
    private TextView scoreView;
    private NextView nextView;
    private ImageButton newGame;
    private ImageButton loadGame;
    private ImageButton saveGame;
    private ImageView timer;
    private Chronometer chronometer;
    private RecordTrack scoreImage;

    private LogicWinLine game;
    private int dimension;
    private boolean savedCurrentState;
    private MPoint orig;
    private Long timeWhenStopped;


    private Handler boardHandler;
    private Handler nextHandler;
    private Handler scoreHandler;
    private Handler endAlertHandler;

    private boolean pendingShowGameServicePopup;

    @Inject
    public GameService gameService;

    @Inject
    public PreferenceService preferenceService;

    private boolean loadGameOnStart;
    private boolean canPlay;
    private boolean breakRecordAlert;

    private MediaPlayer mp;
    private MediaPlayer ballSelect;
    private MediaPlayer ballMoving;
    private MediaPlayer ballMoveFailure;

    private CountDownTimer countDownTimer;
    private long comboCount;
    private int combo;
    private boolean comboIsRunning;

    private Animation scaleAnimation;
    private Animation fadeInAnimation;
    private Animation fadeOutAnimation;
    private View comboTrack;
    private TextView comboSize;
    private TextView chrono;
    private AdView bottomAdView;
    private InterstitialAd newGameAdView;
    private boolean handleBoardTouch;

    private GoogleApiClient googleApiClient;
    private ErrorDialogFragment dialogFragment;
    private boolean mResolvingError;

    private GameServiceRequest gameServiceRequest;
    private GameProgress gameProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        WinLineApp.getInstance().getComponent().inject(this);

        gameServiceRequest = GameServiceRequest.NONE;

        if(savedInstanceState != null){
            mResolvingError = savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
            gameProgress = (GameProgress) savedInstanceState.getSerializable(STATE_GAME_PROGRESS);
            game = (LogicWinLine) savedInstanceState.getSerializable(STATE_GAME);
            loadGameOnStart = savedInstanceState.getBoolean(STATE_LOAD_GAME_ON_START);
            canPlay = savedInstanceState.getBoolean(STATE_CAN_PLAY);
            breakRecordAlert = savedInstanceState.getBoolean(STATE_BREAK_RECORD_ALERT);
            timeWhenStopped = savedInstanceState.getLong(STATE_TIME_WHEN_STOPPED);
            comboIsRunning = savedInstanceState.getBoolean(STATE_COMBO_IS_RUNNING);
            comboCount = savedInstanceState.getLong(STATE_COMBO_TIME);
            combo = savedInstanceState.getInt(STATE_COMBO_VALUE);
            animateSelectedBallIsRunning = savedInstanceState.getBoolean(STATE_ANIMATE_SELECTED_BALL_IS_RUNNING);
            orig = (MPoint) savedInstanceState.getSerializable(STATE_ORIGIN_POINT);
            handleBoardTouch = savedInstanceState.getBoolean(STATE_HANDLE_BOARD_TOUCH);
        }
        else{
            mResolvingError = false;
            gameProgress = new GameProgress();
            loadGameOnStart = false;
            canPlay = true;
            breakRecordAlert = true;
            timeWhenStopped = 0L;
            comboIsRunning = false;
            comboCount = 10000;
            combo = 2;
            animateSelectedBallIsRunning = false;
            orig = null;
            handleBoardTouch = true;
        }

        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .build();

        MobileAds.initialize(getApplicationContext(), getString(R.string.admob_app_id));

        if(gameServiceIsConnected(GameServiceRequest.NOTIFY_ACHIEVEMENT_SUPPORTER_PLAYER)){
            Games.Achievements.increment(googleApiClient, getString(R.string.achievement_enthusiast_player), 1);
            Games.Achievements.increment(googleApiClient, getString(R.string.achievement_fanatic_player), 1);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomAdView = (AdView) findViewById(R.id.adView);
        boardContainer = findViewById(R.id.board_container);
        boardView = (BoardView) findViewById(R.id.board);
        nextView = (NextView) findViewById(R.id.next);
        scoreView = (TextView) findViewById(R.id.score);
        newGame = (ImageButton)findViewById(R.id.new_game);
        loadGame = (ImageButton)findViewById(R.id.load_game);
        saveGame = (ImageButton)findViewById(R.id.save_game);
        timer = (ImageView) findViewById(R.id.timer);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        scoreImage = (RecordTrack) findViewById(R.id.score_image);

        comboTrack = findViewById(R.id.combo_track);
        comboSize = (TextView) findViewById(R.id.combo_size);
        chrono = (TextView) findViewById(R.id.combo_timer);


        bottomAdView.loadAd(requestNewAdRequest());

        newGameAdView = new InterstitialAd(getApplicationContext());

        newGameAdView.setAdUnitId(getString(R.string.new_game_interstitial_ad_unit_id));

        newGameAdView.setAdListener(new MyAdListener(new WeakReference<>(this)));

        newGameAdView.loadAd(requestNewAdRequest());

        scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale);
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        scaleAnimation.setRepeatCount(Animation.INFINITE);

        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                comboTrack.setVisibility(View.VISIBLE);
                comboTrack.startAnimation(scaleAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                comboTrack.setVisibility(View.INVISIBLE);
                comboTrack.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        scoreImage.setMax(preferenceService.getHighRecord());

        boardView.onSetPosition(() -> {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.height = chronometer.getHeight();
            params.width = chronometer.getHeight();
            params.setMargins(boardView.getLeft() + boardView.getLeftPosition(), 0, 0, 0);
            timer.setLayoutParams(params);
            chronometer.setWidth(nextView.getLeft() - (boardView.getLeft() + boardView.getLeftPosition() + params.width));

            RelativeLayout.LayoutParams scoreLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            scoreLayout.height = chronometer.getHeight();
            scoreLayout.width = chronometer.getHeight();
            scoreLayout.setMargins(boardView.getRightPosition() - scoreLayout.width, 0, 0, 0);
            scoreImage.setLayoutParams(scoreLayout);
            scoreView.setWidth(boardView.getRightPosition() - scoreLayout.width - nextView.getRight());

            LinearLayout.LayoutParams containerParams = (LinearLayout.LayoutParams) boardContainer.getLayoutParams();
            containerParams.height = boardView.getWidth();
            boardContainer.setLayoutParams(containerParams);
        });

        boardHandler = new Handler(msg -> {
            boardView.invalidate();
            return false;
        });

        nextHandler = new Handler(msg -> {
            nextView.invalidate();
            return false;
        });

        scoreHandler = new Handler(msg -> {
            int highScore = preferenceService.getHighRecord();
            if(comboIsRunning){
                incrementCombo();
            }
            else{
                startComboAnimation(true);
            }
            if(highScore > 0){
                if(game.getScore() > highScore){
                    preferenceService.setHighRecord(game.getScore());


                    scoreImage.setMax(game.getScore());
                    if(breakRecordAlert){
                        setCanPlay(false);
                        pauseChronometer();
                        pauseCombo();
                        breakRecordAlert = false; // only once during a game
                        GameStatsDialog gameRecord = GameStatsDialog.newInstance(game.getScore(), chronometer.getText().toString(), preferenceService.getHighRecord(), false, true);
                        gameRecord.setOnCloseListener(() -> {
                            setCanPlay(true);
                            startChronometer();
                            startCombo();
                        });
                        gameRecord.show(getSupportFragmentManager(), "new record");
                    }
                }
                scoreImage.setProgress(game.getScore());
                scoreImage.invalidate();
            }
            else{
                preferenceService.setHighRecord(game.getScore());
            }
            gameProgress.setScore(game.getScore());
            scoreView.setText(game.getScore().toString());
            Bundle data = msg.getData();
            boolean highlightScore = data.getBoolean(HIGHLIGHT_COMBO_SCORE);
            int scoreBalls = data.getInt(BALLS_SCORE);
            if(highlightScore){
                gameProgress.setConsecutiveScores(gameProgress.getConsecutiveScores() + 1);
                Toast toast = Toast.makeText(this, getString(R.string.combo_score, game.getScore() - data.getInt(PREVIOUS_GAME_SCORE)), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
            else{
                gameProgress.setConsecutiveScores(1);
            }

            gameProgress.setOneShotScore(scoreBalls);
            if(gameServiceIsConnected(GameServiceRequest.NOTIFY_SCORE)){
                notifyGameServiceScore();
            }
            return false;
        });

        endAlertHandler = new Handler(msg -> {
            setCanPlay(false);
            pauseChronometer();
            pauseCombo();
            pauseSelectedBallAnimation();
            scoreView.setText(game.getScore().toString());
            gameProgress.setScore(game.getScore());
            if(gameServiceIsConnected(GameServiceRequest.NOTIFY_SCORE)){
                notifyGameServiceScore();
            }
            GameStatsDialog gameOver = GameStatsDialog.newInstance(game.getScore(), chronometer.getText().toString(), preferenceService.getHighRecord(), true);
            gameOver.setOnCloseListener(() -> {
                setCanPlay(true);
                loadGameOnStart = false;
                stopChronometer();
                stopCombo();
                stopSelectedBallAnimation();
                createNewGame();
            });
            gameOver.show(getSupportFragmentManager(), "game over");
            return false;
        });

        dimension = boardView.getDimension();

        scoreImage.setOnClickListener( v -> {
            setCanPlay(false);
            pauseChronometer();
            pauseCombo();
            pauseSelectedBallAnimation();

            GameStatsDialog gameInfo = GameStatsDialog.newInstance(game.getScore(), chronometer.getText().toString(), preferenceService.getHighRecord(), false);
            gameInfo.setOnCloseListener(() -> {
                setCanPlay(true);
                startChronometer();
                startCombo();
                startSelectedBallAnimation();
            });
            gameInfo.show(getSupportFragmentManager(), "game info");

        });

        newGame.setOnClickListener(v -> {
            if(preferenceService.getAllowTouchSoundPreference()){
                mp.start();
            }
            if(savedCurrentState){
                if(newGameAdView.isLoaded()){
                    newGameAdView.show();
                }
                else{
                    setUpNewGame();
                }
            }
            else{
                setCanPlay(false);
                pauseChronometer();
                pauseCombo();
                pauseSelectedBallAnimation();
                ActivityUtils.showDialog(this, getString(R.string.unsaved_current_state), true, () -> {
                    if(newGameAdView.isLoaded()){
                        newGameAdView.show();
                    }
                    else{
                        setUpNewGame();
                    }
                }, () -> {
                    setCanPlay(true);
                    startChronometer();
                    startCombo();
                    startSelectedBallAnimation();
                });
            }

        });

        loadGame.setOnClickListener(v -> {
            setCanPlay(false);
            pauseChronometer();
            pauseCombo();
            pauseSelectedBallAnimation();
            if(preferenceService.getAllowTouchSoundPreference()){
                mp.start();
            }
            startActivityForResult(new Intent(this, LoadGameActivity.class), LOAD_GAME);
        });

        saveGame.setOnClickListener(v -> {
            if(preferenceService.getAllowTouchSoundPreference()){
                mp.start();
            }
            if(gameService.save(game, (int)(SystemClock.elapsedRealtime() - chronometer.getBase()))){
                savedCurrentState = true;
                Toast.makeText(this, getString(R.string.save_game), Toast.LENGTH_SHORT).show();

            }
            else {
                Toast.makeText(this, getString(R.string.cannot_save_game), Toast.LENGTH_SHORT).show();
            }
        });

        boardView.setOnTouchEmptyListener((i, j) -> {
            if(handleBoardTouch){
                if (orig != null) { // try to move to specific location
                    handleBoardTouch = false;
                    MPoint dest = new MPoint(i, j);
                    String path = game.getPath(orig, dest);
                    if (path != null) { // can move there!!!
                        savedCurrentState = false;
                        int x = orig.getX();
                        int y = orig.getY();
                        stopAnimateTail(() -> {
                            MoveTile(x, y, path, () ->{
                                handleBoardTouch = true;
                            });
                            if(preferenceService.getAllowTouchSoundPreference()){
                                ballMoving.start();
                            }
                        });

                        if(preferenceService.getAllowTouchSoundPreference()){
                            ballMoveFailure.start();
                        }
                    }
                    else{
                        handleBoardTouch = true;
                    }
                }
            }

        });

        boardView.setOnTouchBallListener((i, j) -> {
            if(handleBoardTouch) {
                handleBoardTouch = false;
                if(orig != null){
                    if(orig.getX() == i && orig.getY() == j) { // stop selected ball animation
                        stopAnimateTail(() -> {
                            boardHandler.sendMessage(new Message()); // refresh the board state
                            handleBoardTouch = true;
                        });
                    }
                    else{ // switch the selected ball animation
                        stopAnimateTail(()-> {
                            orig = new MPoint(i, j);
                            animateTail(orig.getX(), orig.getY());
                            handleBoardTouch = true;
                        });
                    }
                }
                else {
                    orig = new MPoint(i, j);
                    animateTail(orig.getX(), orig.getY());
                    handleBoardTouch = true;
                }
                if(preferenceService.getAllowTouchSoundPreference()){
                    ballSelect.start();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                setCanPlay(false);
                pauseChronometer();
                pauseCombo();
                pauseSelectedBallAnimation();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                setCanPlay(true);
                startChronometer();
                startCombo();
                startSelectedBallAnimation();
            }



        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        TextView appVersion = (TextView) navigationView.getHeaderView(0).findViewById(R.id.version);
        TextView appContact = (TextView) navigationView.getHeaderView(0).findViewById(R.id.contact);
        appVersion.setText(String.format(appVersion.getText().toString(), BuildConfig.VERSION_NAME));
        appContact.setText(String.format(BuildConfig.EMAIL_CONTACT));
        MenuItem toggleSound = navigationView.getMenu().findItem(R.id.toggle_sound);
        toggleMenuSound(toggleSound);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void setUpNewGame() {
        setCanPlay(true);
        stopChronometer();
        stopCombo();
        stopSelectedBallAnimation();
        loadGameOnStart = false;
        createNewGame();
        breakRecordAlert = true;
        savedCurrentState = false;
    }

    private AdRequest requestNewAdRequest() {
        return new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("8DDE4A81B682B0D4E89B7D11A7B893FF")
                .addTestDevice("AD276AD2D1CB10BE5142B2786D4C3817")
                .addTestDevice("E03390F6D86E80375CCF82C18576611C")
                .build();

    }

    private void notifyGameServiceScore() {
        Games.Leaderboards.submitScore(googleApiClient, getString(R.string.leaderboard_high_score_id), gameProgress.getScore());
        int score = gameProgress.getScore();
        if(ACHIEVEMENT_BEGINNER_SCORER_THRESHOLD <= score){
            Games.Achievements.unlock(googleApiClient, getString(R.string.achievement_beginner_scorer));
        }
        if(ACHIEVEMENT_GOOD_SCORER_THRESHOLD <= score){
            Games.Achievements.unlock(googleApiClient, getString(R.string.achievement_good_scorer));
        }
        if(ACHIEVEMENT_GREAT_SCORER_THRESHOLD <= score){
            Games.Achievements.unlock(googleApiClient, getString(R.string.achievement_great_scorer));
        }
        if(ACHIEVEMENT_AWESOME_SCORER_THRESHOLD <= score){
            Games.Achievements.unlock(googleApiClient, getString(R.string.achievement_awesome_scorer));
        }
        if(ACHIEVEMENT_NINJA_SCORER_THRESHOLD <= score){
            Games.Achievements.unlock(googleApiClient, getString(R.string.achievement_ninja_scorer));
        }

        // check one move achievements
        int oneShotScore = gameProgress.getOneShotScore();
        if(ACHIEVEMENT_GREEDY_MOVE_THRESHOLD <= oneShotScore){
            Games.Achievements.unlock(googleApiClient, getString(R.string.achievement_greedy_move));
        }
        if(ACHIEVEMENT_STRATEGIC_MOVE_THRESHOLD <= oneShotScore){
            Games.Achievements.unlock(googleApiClient, getString(R.string.achievement_strategic_move));
        }
        if(ACHIEVEMENT_AMAZING_MOVE_THRESHOLD <= oneShotScore){
            Games.Achievements.unlock(googleApiClient, getString(R.string.achievement_amazing_move));
        }

        //check combo achievements
        int consecutiveScores = gameProgress.getConsecutiveScores();
        if(ACHIEVEMENT_COMBO_BEGINNER_THRESHOLD <= consecutiveScores){
            Games.Achievements.unlock(googleApiClient, getString(R.string.achievement_beginner_combo_player));
        }
        if(ACHIEVEMENT_COMBO_FLUENCY_THRESHOLD <= consecutiveScores){
            Games.Achievements.unlock(googleApiClient, getString(R.string.achievement_fluency_combo_player));
        }
        if(ACHIEVEMENT_COMBONATOR_THRESHOLD <= consecutiveScores){
            Games.Achievements.unlock(googleApiClient, getString(R.string.achievement_combonator_combo_player));
        }
    }

    private void startComboAnimation(boolean startVisualAnimation) {
        chrono.setText(String.format(Locale.US, "%.1f", comboCount/1000f));
        countDownTimer = new CountDownTimer(comboCount, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                comboCount = millisUntilFinished;
                chrono.setText(String.format(Locale.US, "%.1f", comboCount/1000f));
            }

            @Override
            public void onFinish() {
                chrono.setText(String.format(Locale.US, "%.1f", 0f));
                stopCombo();
            }
        };
        comboSize.setText(String.format("%dx", combo));
        countDownTimer.start();
        if(startVisualAnimation){
            comboTrack.startAnimation(comboIsRunning ? scaleAnimation : fadeInAnimation);
        }
        comboIsRunning = true;
    }

    private void startCombo(){
        if(comboIsRunning){
            startComboAnimation(true);
        }
    }

    private void stopCombo(){
        if(comboIsRunning){
            comboTrack.startAnimation(fadeOutAnimation);
        }
        comboIsRunning = false;
        comboCount = 10000;
        combo = 2;
    }

    private void pauseCombo(){
        if(comboIsRunning){
            if(countDownTimer != null)
                countDownTimer.cancel();
            comboTrack.clearAnimation();
            comboTrack.setVisibility(View.VISIBLE);
        }
    }

    private void incrementCombo(){
        countDownTimer.cancel();
        combo++;
        comboCount = 10000;
        startComboAnimation(false);
    }

    private void toggleMenuSound(MenuItem toggleSound) {
        toggleSound.setIcon(preferenceService.getAllowTouchSoundPreference() ? R.drawable.ic_sound_on : R.drawable.ic_sound_off);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LOAD_GAME){
            setCanPlay(true);
            if(resultCode == RESULT_OK){
                savedCurrentState = true;
                WinLine loadedGame = (WinLine) data.getSerializableExtra(LoadGameActivity.GAME_LOADED);
                timeWhenStopped = -1L * loadedGame.getTime();
                game = new LogicWinLine(loadedGame.getBoard(), loadedGame.getNext(), loadedGame.getScore());
                gameProgress.setScore(game.getScore());
                gameProgress.setOneShotScore(0);
                breakRecordAlert = true;
                stopCombo();
                stopSelectedBallAnimation();
            }
        }
        else if(requestCode == REQUEST_RESOLVE_ERROR){
            mResolvingError = false;
            if(resultCode == RESULT_OK){
                // Make sure the app is not already connected or attempting to connect
                if (!googleApiClient.isConnecting() && !googleApiClient.isConnected()) {
                    googleApiClient.connect();
                }
            }
            else{
                pendingShowGameServicePopup = true;
            }
        }
        else if(resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED && (requestCode == REQUEST_LEADER_BOARD || requestCode == REQUEST_ACHIEVEMENTS)){
            googleApiClient.disconnect();
        }
    }

    private void stopChronometer(){
        timeWhenStopped = 0L;
        chronometer.stop();
    }

    private void pauseChronometer() {
        timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
        chronometer.stop();
    }

    private void startChronometer() {
        chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        chronometer.start();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasNavigationBar() && hasFocus){
            hideSystemUI();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomAdView.resume();
        prepareSound();
        createNewGame();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if(pendingShowGameServicePopup){
            pendingShowGameServicePopup = false;
            ActivityUtils.showDialog(this, getString(R.string.game_service_not_available), getString(R.string.error_title));
        }
    }

    @Override
    protected void onPause() {
        bottomAdView.pause();
        if(canPlay){
            pauseChronometer();
            pauseCombo();
            pauseSelectedBallAnimation();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        releaseSound();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        googleApiClient.unregisterConnectionCallbacks(this);
        googleApiClient.unregisterConnectionFailedListener(this);
        bottomAdView.destroy();
        boardHandler.removeCallbacksAndMessages(null);
        nextHandler.removeCallbacksAndMessages(null);
        scoreHandler.removeCallbacksAndMessages(null);
        endAlertHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void prepareSound() {
        mp = MediaPlayer.create(this, R.raw.buton_click);
        ballSelect = MediaPlayer.create(this, R.raw.ball_select);
        ballMoving = MediaPlayer.create(this, R.raw.ball_moving);
        ballMoveFailure = MediaPlayer.create(this, R.raw.ball_move_failure);
    }

    private void releaseSound() {
        mp.release();
        ballSelect.release();
        ballMoving.release();
        ballMoveFailure.release();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
        outState.putSerializable(STATE_GAME_PROGRESS, gameProgress);
        outState.putSerializable(STATE_GAME, game);
        outState.putBoolean(STATE_LOAD_GAME_ON_START, loadGameOnStart);
        outState.putBoolean(STATE_CAN_PLAY, canPlay);
        outState.putBoolean(STATE_BREAK_RECORD_ALERT, breakRecordAlert);
        outState.putLong(STATE_TIME_WHEN_STOPPED, timeWhenStopped);
        outState.putBoolean(STATE_COMBO_IS_RUNNING, comboIsRunning);
        outState.putLong(STATE_COMBO_TIME, comboCount);
        outState.putInt(STATE_COMBO_VALUE, combo);
        outState.putBoolean(STATE_ANIMATE_SELECTED_BALL_IS_RUNNING, animateSelectedBallIsRunning);
        outState.putSerializable(STATE_ORIGIN_POINT, orig);
        outState.putBoolean(STATE_HANDLE_BOARD_TOUCH, handleBoardTouch);
    }

    private void createNewGame(){
        if(!loadGameOnStart){
            try {
                game = new LogicWinLine(dimension);
                loadGameOnStart = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        else {
//            loadGameOnStart = false;
//        }

        boardView.setBoard(game.getBoard());
        nextView.setBoard(game.getNext());
        scoreView.setText(game.getScore().toString());
        scoreImage.setProgress(game.getScore());
        boardView.invalidate();
        nextView.invalidate();
        scoreImage.invalidate();
        if(canPlay){
            startChronometer();
            startCombo();
            startSelectedBallAnimation();
        }
    }

    private void startSelectedBallAnimation() {
        if(animateSelectedBallIsRunning){
            animateTail(orig.getX(), orig.getY());
        }
    }

    private void pauseSelectedBallAnimation(){
        if(animateSelectedBallIsRunning){
            animateSelectedTail.stopAnimation(null);
        }
    }

    private void stopSelectedBallAnimation(){
        if(animateSelectedBallIsRunning){
            stopAnimateTail(null);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (isDrawerOpen(drawer)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        setCanPlay(false);
        pauseChronometer();
        pauseCombo();
        pauseSelectedBallAnimation();

        //noinspection SimplifiableIfStatement
        if (id == R.id.share) {
            if(checkPermission(MY_PERMISSIONS_REQUEST_ACCESS_STORAGE, R.string.use_external_storage_explanation, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                shareApp(() -> {
                    setCanPlay(true);
                    startChronometer();
                    startCombo();
                    startSelectedBallAnimation();
                },() -> {
                    setCanPlay(true);
                });
            }
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // storage-related task you need to do.
                    shareApp(() -> {
                        setCanPlay(true);
                        startChronometer();
                        startCombo();
                    },() -> {
                        setCanPlay(true);
                    });

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    setCanPlay(true);
                    startChronometer();
                    startCombo();
                }
                return;
            }

        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.toggle_sound) {
            boolean useSound = preferenceService.getAllowTouchSoundPreference();
            preferenceService.setAllowTouchSoundPreference(!useSound);
            toggleMenuSound(item);
            if(preferenceService.getAllowTouchSoundPreference()){
                mp.start();
            }
        } else if (id == R.id.rate_game) {
            rateApp();

        } else if (id == R.id.ranking_game) {
            if(gameServiceIsConnected(GameServiceRequest.OPEN_LEADER_BOARD)){
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(googleApiClient, getString(R.string.leaderboard_high_score_id)), REQUEST_LEADER_BOARD);
            }
        } else if (id == R.id.achievement_game){
            if(gameServiceIsConnected(GameServiceRequest.OPEN_ACHIEVEMENT)){
                startActivityForResult(Games.Achievements.getAchievementsIntent(googleApiClient), REQUEST_ACHIEVEMENTS);
            }
        }
        else if(id == R.id.help_game){
            startActivity(new Intent(this, HelpActivity.class));
        }
//        if(closeDrawer){
//            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//            drawer.closeDrawer(GravityCompat.START);
//        }
        return true;
    }

    private boolean isDrawerOpen(DrawerLayout drawer){
        return drawer.isDrawerOpen(GravityCompat.START);
    }

    private boolean gameServiceIsConnected(GameServiceRequest request) {
        if(googleApiClient.isConnected())
            return true;
        gameServiceRequest = request;
        googleApiClient.connect();
        return false;
    }

    @Override
    public void MoveTile(int x, int y, String path, Runnable actionAfter) {
        animateMoveTails = new AnimateMoveTails(new WeakReference<>(this), x, y, path, actionAfter);
        animateMoveTails.start();
    }

    @Override
    public void animateTail(int x, int y){
        animateSelectedTail = new AnimateSelectedTail(new WeakReference<>(this), x, y);
        animateSelectedTail.start();
        animateSelectedBallIsRunning = true;
    }

    @Override
    public void stopAnimateTail(Runnable actionAfter){
        orig = null;
        animateSelectedBallIsRunning = false;
        animateSelectedTail.stopAnimation(actionAfter);
    }

    @Override
    public void animateInsertTile(List<AnimateChecker> tails, Runnable doAfter) {
        animateInsertTails = new AnimateInsertTails(new WeakReference<>(this), tails, doAfter);
        animateInsertTails.start();
    }

    @Override
    public void animateScoreTile(List<AnimateChecker> tiles, Runnable doAfter){
        animateScore = new AnimateScore(new WeakReference<>(this), tiles, doAfter);
        animateScore.start();
    }

    public void setLoadGameOnStart(boolean loadGameOnStart) {
        this.loadGameOnStart = loadGameOnStart;
    }

    public void setCanPlay(boolean canPlay) {
        this.canPlay = canPlay;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                googleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(connectionResult.getErrorCode());
            mResolvingError = true;
        }
    }

    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        dialogFragment.dismiss();
        mResolvingError = false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        switch (gameServiceRequest){
            case NOTIFY_SCORE:
                notifyGameServiceScore();
                break;
            case OPEN_LEADER_BOARD:
                notifyGameServiceScore();
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(googleApiClient, getString(R.string.leaderboard_high_score_id)), REQUEST_LEADER_BOARD);
                break;
            case OPEN_ACHIEVEMENT:
                notifyGameServiceScore();
                startActivityForResult(Games.Achievements.getAchievementsIntent(googleApiClient), REQUEST_ACHIEVEMENTS);
                break;
            case NOTIFY_ACHIEVEMENT_SUPPORTER_PLAYER:
                Games.Achievements.increment(googleApiClient, getString(R.string.achievement_enthusiast_player), 1);
                Games.Achievements.increment(googleApiClient, getString(R.string.achievement_fanatic_player), 1);
                break;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mResolvingError = false;
        gameServiceRequest = GameServiceRequest.NONE;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((GameActivity) getActivity()).onDialogDismissed();
        }
    }

    static class AnimateMoveTails extends Thread {
        WeakReference<GameAnimation> container;
        MPoint orig;
        String path;
        Runnable actionAfter;

        public AnimateMoveTails(WeakReference<GameAnimation> container, int x, int y, String path, Runnable actionAfter) {
            this.container = container;
            this.orig = new MPoint(x, y);
            this.path = path;
            this.actionAfter = actionAfter;
        }

        public void run() {
            GameAnimation activity = container.get();
            LogicWinLine game = activity.getGame();
            Checker[][] t = game.getBoard();
            int x = orig.getX();
            int y = orig.getY();
            Checker f = t[x][y];
            for (int i = 0; i < path.length(); i++) {
                t[x][y] = null;
                switch (path.charAt(i)) {
                    case 'A':
                        x--;
                        break;
                    case 'B':
                        x++;
                        break;
                    case 'D':
                        y++;
                        break;
                    case 'I':
                        y--;
                        break;
                }
                t[x][y] = f;
                activity.setBoardGame(t);
                activity.sendBoardAlertHandler(new Message());
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            MPoint dest = new MPoint(x, y);
            game.moveChecker(orig, dest, f);
            ContinueGameLogic(dest);
        }

        private void ContinueGameLogic(MPoint dest) {
            GameAnimation activity = container.get();
            LogicWinLine game = activity.getGame();
            int dimension = activity.getDimension();
            MPoint[] limit = new MPoint[8];
            int[] score = new int[4];
            if (game.canDelete(dest, limit, score)) {
                List<AnimateChecker> tiles = new ArrayList<>();
                Checker[][]board = game.getBoard(true);
                int index = AnimateChecker.getScoreIndex();
                int comboMultiple = activity.getComboIsRunning() ? activity.getCombo() : 1;
                int previousScore = game.getScore();

                for (int k = 0; k < score.length; k++) {
                    if (score[k] >= 5) {
                        game.addScore(comboMultiple * score[k]);
                        List<Integer> positions = game.deleteSequence(limit[k * 2], limit[(k * 2) + 1], (k * 2) + 1);
                        for(Integer pos: positions){
                            int x = pos / dimension;
                            int y = pos % dimension;
                            tiles.add(new AnimateChecker(board[x][y], index));
                        }
                    }
                }

                activity.animateScoreTile(tiles, () -> {
                    activity.setBoardGame(game.getBoard());
                    activity.sendBoardAlertHandler(new Message());
                    Message m = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(HIGHLIGHT_COMBO_SCORE, comboMultiple > 1);
                    bundle.putInt(PREVIOUS_GAME_SCORE, previousScore);
                    bundle.putInt(BALLS_SCORE, tiles.size());
                    m.setData(bundle);
                    activity.sendScoreAlertHandler(m);
                    if(actionAfter != null){
                        actionAfter.run();
                    }
                });
            } else {
                try {
                    List<Integer> positions = game.addCheckers();
                    Checker[][]board = game.getBoard();
                    int index = AnimateChecker.getInsertIndex();
                    List<AnimateChecker> tails = new ArrayList<>(3);
                    for(Integer pos: positions){
                        int x = pos / dimension;
                        int y = pos % dimension;
                        tails.add(new AnimateChecker(board[x][y], index));
                    }
                    game.buildNext(3);
                    activity.animateInsertTile(tails, () -> {
                        activity.setBoardGame(game.getBoard());
                        activity.setNextBoardGame(game.getNext());
                        activity.sendBoardAlertHandler(new Message());
                        activity.sendNextAlertHandler(new Message());
                        if(actionAfter != null){
                            actionAfter.run();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if(actionAfter != null){
                        actionAfter.run();
                    }
                }
            }
            if (game.gameOver()) {
                activity.sendEndAlertHandler(new Message());
            }
        }
    }

    static class AnimateInsertTails extends Thread {
        WeakReference<GameAnimation> container;
        List<AnimateChecker> tails;
        Runnable doAfter;

        public AnimateInsertTails(WeakReference<GameAnimation> container, List<AnimateChecker> tails, Runnable doAfter){
            this.container = container;
            this.tails = tails;
            this.doAfter = doAfter;
        }

        @Override
        public void run() {
            GameAnimation activity = container.get();
            LogicWinLine game = activity.getGame();
            Checker[][] board = game.getBoard();
            List<Checker> originals = new ArrayList<>(3);
            for (AnimateChecker checker: tails){
                originals.add(checker.getChecker());
            }
            for(int i = 0; i < AnimateChecker.FRAMES_COUNT; i++){
                for (AnimateChecker checker: tails){
                    checker.setAnimateColor(i);
                    MPoint position = checker.getPosition();
                    board[position.getX()][position.getY()] = checker;
                }
                activity.sendBoardAlertHandler(new Message());
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (Checker orig: originals){
                MPoint position = orig.getPosition();
                board[position.getX()][position.getY()] = orig;
            }
            if(doAfter != null){
                doAfter.run();
            }
        }
    }

    static class AnimateSelectedTail extends Thread {
        WeakReference<GameAnimation> container;
        int x;
        int y;
        private AtomicBoolean canRun;
        Runnable actionAfter;


        public AnimateSelectedTail(WeakReference<GameAnimation> container, int x, int y){
            this.container = container;
            this.x = x;
            this.y = y;
            canRun = new AtomicBoolean();
            canRun.set(true);
        }

        public void stopAnimation(Runnable after){
            actionAfter = after;
            canRun.set(false);

        }

        @Override
        public void run() {
            GameAnimation activity = container.get();
            Checker[][] board = container.get().getGame().getBoard();
            AnimateChecker tail = new AnimateChecker(board[x][y], 0);
            board[x][y] = tail;

            while (canRun.get()){
                for(int i=0; i < AnimateChecker.FRAMES_COUNT; i++){
                    tail.setAnimateColor(i);
                    activity.sendBoardAlertHandler(new Message());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            board[x][y] = tail.getChecker();
            if(actionAfter != null)
                actionAfter.run();
        }
    }

    static class AnimateScore extends Thread {
        WeakReference<GameAnimation> container;
        private final List<AnimateChecker> tiles;
        private final Runnable doAfter;

        public AnimateScore(WeakReference<GameAnimation> container, List<AnimateChecker> tiles, Runnable doAfter){
            this.container = container;
            this.tiles = tiles;
            this.doAfter = doAfter;
        }

        @Override
        public void run() {
            GameAnimation activity = container.get();
            Checker[][] board = activity.getGame().getBoard();
            List<Checker> originals = new ArrayList<>(3);
            for (AnimateChecker checker: tiles){
                originals.add(checker.getChecker());
            }
            for(int i=0; i < AnimateChecker.FRAMES_COUNT; i++){
                for(AnimateChecker checker: tiles){
                    checker.setAnimateColor(i);
                    MPoint position = checker.getPosition();
                    board[position.getX()][position.getY()] = checker;
                }
                activity.sendBoardAlertHandler(new Message());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (Checker orig: originals){
                MPoint position = orig.getPosition();
                board[position.getX()][position.getY()] = null;
            }
            if(doAfter != null){
                doAfter.run();
            }
        }
    }

    @Override
    public LogicWinLine getGame() {
        return game;
    }

    @Override
    public void setBoardGame(Checker[][] boardGame){
        boardView.setBoard(boardGame);
    }

    @Override
    public void setNextBoardGame(Checker[] nextGame){
        nextView.setBoard(nextGame);
    }

    @Override
    public boolean getComboIsRunning() {
        return comboIsRunning;
    }

    @Override
    public int getCombo() {
        return combo;
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public void sendBoardAlertHandler(Message m){
        boardHandler.sendMessage(m);
    }

    @Override
    public void sendNextAlertHandler(Message m){
        nextHandler.sendMessage(m);
    }

    @Override
    public void sendScoreAlertHandler(Message m){
        scoreHandler.sendMessage(m);
    }

    @Override
    public void sendEndAlertHandler(Message m) {
        endAlertHandler.sendMessage(m);
    }

    public enum GameServiceRequest{
        NONE,
        NOTIFY_SCORE,
        OPEN_LEADER_BOARD,
        OPEN_ACHIEVEMENT,
        NOTIFY_ACHIEVEMENT_BEGINNER_SCORER,
        NOTIFY_ACHIEVEMENT_INTERMEDIATE_SCORER,
        NOTIFY_ACHIEVEMENT_GOOD_SCORER,
        NOTIFY_ACHIEVEMENT_GREAT_SCORER,
        NOTIFY_ACHIEVEMENT_SUPPORTER_PLAYER
    }

    public static class MyAdListener extends AdListener{

        private final WeakReference<GameActivity> container;

        public MyAdListener(WeakReference<GameActivity> container){
            this.container = container;
        }

        @Override
        public void onAdClosed() {
            GameActivity activity = container.get();
            activity.newGameAdView.loadAd(activity.requestNewAdRequest());
            activity.setUpNewGame();
        }
    }

}
