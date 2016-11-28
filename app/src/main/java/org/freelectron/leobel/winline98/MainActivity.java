package org.freelectron.leobel.winline98;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
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
import android.view.MotionEvent;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import org.freelectron.leobel.winline98.dialogs.GameStatsDialog;
import org.freelectron.leobel.winline98.models.GameProgress;
import org.freelectron.leobel.winline98.models.WinLine;
import org.freelectron.leobel.winline98.services.GameService;
import org.freelectron.leobel.winline98.services.PreferenceService;
import org.freelectron.leobel.winline98.utils.ActivityUtils;
import org.freelectron.winline.Checker;
import org.freelectron.winline.LogicWinLine;
import org.freelectron.winline.MPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import timber.log.Timber;

//import timber.log.Timber;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_RESOLVE_ERROR = 101;
    private static final int LOAD_GAME = 1;
    private static final String DIALOG_ERROR = "DIALOG_ERROR";
    private static final String STATE_RESOLVING_ERROR = "STATE_RESOLVING_ERROR";
    private static final int REQUEST_LEADER_BOARD = 102;

    private String HIGHLIGHT_COMBO_SCORE = "HIGHLIGHT_COMBO_SCORE";
    private String PREVIOUS_GAME_SCORE = "PREVIOUS_GAME_SCORE";


    private Thread timerMoveTile;
    private Thread timerAnimateTile;
    private Thread timerAnimateInsertTile;
    private Thread timerAnimateScoreTile;
    private AnimateSelectedTail animateSelectedTail;

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
    private String path;
    private Long timeWhenStopped = 0L;


    private Handler boardHandler;
    private Handler nextHandler;
    private Handler scoreHandler;
    private Handler endAlertHandler;

    @Inject
    public GameService gameService;

    @Inject
    public PreferenceService preferenceService;

    private ProgressDialog mProgressDialog;

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
    private boolean handleBoardTouch;

    private GoogleApiClient googleApiClient;
    private boolean mResolvingError;

    private GameServiceRequest gameServiceRequest;
    private GameProgress gameProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WinLineApp.getInstance().getComponent().inject(this);

        mResolvingError = savedInstanceState != null && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

        gameServiceRequest = GameServiceRequest.NONE;
        gameProgress = new GameProgress();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .build();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMessage("Your request is being processes");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        mp = MediaPlayer.create(this, R.raw.buton_click);
        ballSelect = MediaPlayer.create(this, R.raw.ball_select);
        ballMoving = MediaPlayer.create(this, R.raw.ball_moving);
        ballMoveFailure = MediaPlayer.create(this, R.raw.ball_move_failure);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        comboCount = 10000;
        combo = 2;

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


        loadGameOnStart = false;
        setCanPlay(true);
        breakRecordAlert = true;

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
                        pauseChronometer();
                        setCanPlay(false);
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
            if(gameServiceIsConnected(GameServiceRequest.NOTIFY_SCORE)){
                Games.Leaderboards.submitScore(googleApiClient, getString(R.string.leaderboard_high_score_id), gameProgress.getScore());
            }

            scoreView.setText(game.getScore().toString());
            Bundle data = msg.getData();
            boolean highlightScore = data.getBoolean(HIGHLIGHT_COMBO_SCORE);
            if(highlightScore){
                Toast toast = Toast.makeText(this, getString(R.string.combo_score, game.getScore() - data.getInt(PREVIOUS_GAME_SCORE)), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
            return false;
        });

        endAlertHandler = new Handler(msg -> {
            pauseChronometer();
            setCanPlay(false);
            pauseCombo();
            scoreView.setText(game.getScore().toString());
            gameProgress.setScore(game.getScore());
            if(gameServiceIsConnected(GameServiceRequest.NOTIFY_SCORE)){
                Games.Leaderboards.submitScore(googleApiClient, getString(R.string.leaderboard_high_score_id), gameProgress.getScore());
            }
            GameStatsDialog gameOver = GameStatsDialog.newInstance(game.getScore(), chronometer.getText().toString(), preferenceService.getHighRecord(), true);
            gameOver.setOnCloseListener(() -> {
                setCanPlay(true);
                loadGameOnStart = false;
                stopChronometer();
                stopCombo();
                createNewGame();
            });
            gameOver.show(getSupportFragmentManager(), "game over");
            return false;
        });

        dimension = boardView.getDimension();

        scoreImage.setOnClickListener( v -> {
            pauseChronometer();
            setCanPlay(false);
            pauseCombo();
            GameStatsDialog gameInfo = GameStatsDialog.newInstance(game.getScore(), chronometer.getText().toString(), preferenceService.getHighRecord(), false);
            gameInfo.setOnCloseListener(() -> {
                setCanPlay(true);
                startChronometer();
                startCombo();
            });
            gameInfo.show(getSupportFragmentManager(), "game info");

        });

        newGame.setOnClickListener(v -> {
            if(preferenceService.getAllowTouchSoundPreference()){
                mp.start();
            }
            if(savedCurrentState){
                stopChronometer();
                stopCombo();
                loadGameOnStart = false;
                createNewGame();
                savedCurrentState = false;
                breakRecordAlert = true;
            }
            else{
                pauseChronometer();
                setCanPlay(false);
                pauseCombo();
                ActivityUtils.showDialog(this, getString(R.string.unsaved_current_state), true, () -> {
                    setCanPlay(true);
                    stopChronometer();
                    stopCombo();
                    loadGameOnStart = false;
                    createNewGame();
                    breakRecordAlert = true;
                }, () -> {
                    setCanPlay(true);
                    startChronometer();
                    startCombo();
                });
            }

        });

        loadGame.setOnClickListener(v -> {
            pauseChronometer();
            setCanPlay(false);
            pauseCombo();
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
                mProgressDialog.hide();
                Toast.makeText(this, "Your game was saved!", Toast.LENGTH_SHORT).show();

            }
            else {
                Toast.makeText(this, "Your game can\'t be saved.", Toast.LENGTH_SHORT).show();
            }
        });

        handleBoardTouch = true;
        boardView.setOnTouchListener((v, event) -> {
            if(!handleBoardTouch) return false;
            boolean emptyPlace = false;
            if (v.getId() != R.id.board) {
                return false;
            }
            if (event.getAction() != 1) {
                return true;
            }
            int i = (int) ((event.getY() - ((float) boardView.mYOffset)) / ((float) boardView.mTileSize));
            int j = (int) ((event.getX() - ((float) boardView.mXOffset)) / ((float) boardView.mTileSize));
            if (i < 0 || i >= dimension || j < 0 || j >= dimension) {
                return true;
            }
            handleBoardTouch = false;
            BoardView board = (BoardView) v;
            if (game.getBoard()[i][j] == null) {
                emptyPlace = true;
            }
            if (orig != null && emptyPlace) { // try to move to specific location
                MPoint dest = new MPoint(i, j);
                path = game.getPath(orig, dest);
                if (path == null) { // can't move there!!!
                    if(preferenceService.getAllowTouchSoundPreference()){
                        ballMoveFailure.start();
                    }
                    handleBoardTouch = true;
                    return true;
                }
                savedCurrentState = false;
                stopAnimateTail(() -> {
                    MoveTile(board);
                    if(preferenceService.getAllowTouchSoundPreference()){
                        ballMoving.start();
                    }
                });
                return true;
            } else if (emptyPlace) {
                handleBoardTouch = true;
                return true;
            } else { // select ball
                if(orig != null && orig.getX() == i && orig.getY() == j){
                    orig = null;
                    stopAnimateTail(() -> {
                        boardHandler.sendMessage(new Message()); // refresh the board state
                    });
                    handleBoardTouch = true;
                    return true;
                }
                if(preferenceService.getAllowTouchSoundPreference()){
                    ballSelect.start();
                }
                orig = new MPoint(i, j);
                if(animateSelectedTail != null && animateSelectedTail.isRunning()){ // there is one ball selected already
                    stopAnimateTail(()-> {
                        animateTail(orig);
                    });
                }
                else {
                    animateTail(orig);
                }
                handleBoardTouch = true;
                return true;
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
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                setCanPlay(true);
                startChronometer();
                startCombo();
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

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_UP) {
//            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//            if (isDrawerOpen(drawer)) { //Your code here to check whether drawer is open or not.
//
//                View content = findViewById(R.id.nav_view); //drawer view id
//                int[] contentLocation = new int[2];
//                content.getLocationOnScreen(contentLocation);
//                Rect rect = new Rect(contentLocation[0],
//                        contentLocation[1],
//                        contentLocation[0] + content.getWidth(),
//                        contentLocation[1] + content.getHeight());
//
//                if (!(rect.contains((int) event.getX(), (int) event.getY()))) {
//                    setCanPlay(true);
//                }
//
//            }
//        }
//        return super.dispatchTouchEvent(event);
//    }

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
                breakRecordAlert = true;
                stopCombo();
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
                ActivityUtils.showDialog(this, getString(R.string.game_service_not_available), getString(R.string.error_title));
            }
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
    protected void onResume() {
        super.onResume();
        createNewGame();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(canPlay){
            pauseChronometer();
            pauseCombo();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
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

        pauseChronometer();
        setCanPlay(false);
        pauseCombo();

        //noinspection SimplifiableIfStatement
        if (id == R.id.share) {
            if(checkPermission(MY_PERMISSIONS_REQUEST_ACCESS_STORAGE, R.string.use_external_storage_explanation, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                shareApp(() -> {
                    setCanPlay(true);
                    startChronometer();
                    startCombo();
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
        boolean closeDrawer = true;
        if (id == R.id.toggle_sound) {
            boolean useSound = preferenceService.getAllowTouchSoundPreference();
            preferenceService.setAllowTouchSoundPreference(!useSound);
            toggleMenuSound(item);
            if(preferenceService.getAllowTouchSoundPreference()){
                mp.start();
            }
            closeDrawer = false;
        } else if (id == R.id.rate_game) {

        } else if (id == R.id.ranking_game) {
            closeDrawer = false;
            if(gameServiceIsConnected(GameServiceRequest.OPEN_LEADER_BOARD)){
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(googleApiClient, getString(R.string.leaderboard_high_score_id)), REQUEST_LEADER_BOARD);
            }
        }
        if(closeDrawer){
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
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

    private void MoveTile(BoardView board) {
        timerMoveTile = new Thread(new AnimateMoveTails(boardHandler, nextHandler, scoreHandler, endAlertHandler));
        timerMoveTile.start();
    }

    private void animateTail(MPoint point){
        Checker tail = game.getBoard()[point.getX()][point.getY()];
        animateSelectedTail = new AnimateSelectedTail(new AnimateChecker(tail, 0), boardHandler);
        timerAnimateTile = new Thread(animateSelectedTail);
        timerAnimateTile.start();
    }


    private void stopAnimateTail(Runnable actionAfter){
        animateSelectedTail.stopAnimation(actionAfter);
    }

    private void animateInsertTile(List<AnimateChecker> tails, Runnable doAfter) {
        timerAnimateInsertTile = new Thread(new AnimateInsertTails(tails, boardHandler, doAfter));
        timerAnimateInsertTile.start();
    }

    private void animateScoreTile(List<AnimateChecker> tiles, Runnable doAfter){
        timerAnimateScoreTile = new Thread(new AnimateScore(tiles, boardHandler, doAfter));
        timerAnimateScoreTile.start();
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
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        switch (gameServiceRequest){
            case NOTIFY_SCORE:
                Games.Leaderboards.submitScore(googleApiClient, getString(R.string.leaderboard_high_score_id), gameProgress.getScore());
                break;
            case OPEN_LEADER_BOARD:
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(googleApiClient, getString(R.string.leaderboard_high_score_id)), REQUEST_LEADER_BOARD);
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
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((MainActivity) getActivity()).onDialogDismissed();
        }
    }

    class AnimateMoveTails implements Runnable {
        Handler alertHandler;
        Handler boardHandler;
        Handler nextHandler;
        Handler scoreHandler;

        public AnimateMoveTails(Handler boardHandler, Handler nextHandler, Handler scoreHandler, Handler alertHandler) {
            this.boardHandler = boardHandler;
            this.nextHandler = nextHandler;
            this.scoreHandler = scoreHandler;
            this.alertHandler = alertHandler;
        }

        public void run() {
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
                boardView.setBoard(t);
                this.boardHandler.sendMessage(new Message());
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

        public void ContinueGameLogic(MPoint dest) {
            MPoint[] limit = new MPoint[8];
            int[] score = new int[4];
            if (game.canDelete(dest, limit, score)) {
                List<AnimateChecker> tiles = new ArrayList<>();
                Checker[][]board = game.getBoard(true);
                int index = AnimateChecker.getScoreIndex();
                int comboMultiple = comboIsRunning ? combo : 1;
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

                animateScoreTile(tiles, () -> {
                    boardView.setBoard(game.getBoard());
                    boardHandler.sendMessage(new Message());
                    Message m = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(HIGHLIGHT_COMBO_SCORE, comboMultiple > 1);
                    bundle.putInt(PREVIOUS_GAME_SCORE, previousScore);
                    m.setData(bundle);
                    scoreHandler.sendMessage(m);
                    handleBoardTouch = true;
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
                    animateInsertTile(tails, () -> {
                        boardView.setBoard(game.getBoard());
                        nextView.setBoard(game.getNext());
                        this.boardHandler.sendMessage(new Message());
                        this.nextHandler.sendMessage(new Message());
                        handleBoardTouch = true;
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    handleBoardTouch = true;
                }
            }
            orig = null;
            if (game.gameOver()) {
                endAlertHandler.sendMessage(new Message());
            }
        }


    }

    class AnimateInsertTails implements Runnable{
        Handler boardHandler;
        List<AnimateChecker> tails;
        Runnable doAfter;

        public AnimateInsertTails(List<AnimateChecker> tails, Handler boardHandler, Runnable doAfter){
            this.tails = tails;
            this.boardHandler = boardHandler;
            this.doAfter = doAfter;
        }

        @Override
        public void run() {
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
                this.boardHandler.sendMessage(new Message());
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

    class AnimateSelectedTail implements Runnable {
        Handler boardHandler;
        AnimateChecker tail;
        private AtomicBoolean canRun;
        Runnable actionAfter;


        public AnimateSelectedTail(AnimateChecker tail, Handler boardHandler){
            this.boardHandler = boardHandler;
            this.tail = tail;
            canRun = new AtomicBoolean();
            canRun.set(true);
        }

        public void stopAnimation(Runnable after){
            actionAfter = after;
            canRun.set(false);

        }

        public boolean isRunning(){
            return canRun.get();
        }

        @Override
        public void run() {
            MPoint point = tail.getPosition();
            Checker[][] board = game.getBoard();
            board[point.getX()][point.getY()] = tail;
            while (canRun.get()){
                for(int i=0; i < AnimateChecker.FRAMES_COUNT; i++){
                    tail.setAnimateColor(i);
                    this.boardHandler.sendMessage(new Message());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            board[point.getX()][point.getY()] = tail.getChecker();
            if(actionAfter != null)
                actionAfter.run();
        }
    }

    class AnimateScore implements Runnable{

        private final List<AnimateChecker> tiles;
        private final Handler boardHandler;
        private final Runnable doAfter;

        public AnimateScore(List<AnimateChecker> tiles, Handler boardHandler, Runnable doAfter){
            this.tiles = tiles;
            this.boardHandler = boardHandler;
            this.doAfter = doAfter;
        }

        @Override
        public void run() {
            Checker[][] board = game.getBoard();
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
                this.boardHandler.sendMessage(new Message());
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

    public enum GameServiceRequest{
        NONE,
        NOTIFY_SCORE,
        OPEN_LEADER_BOARD;
    }

}
