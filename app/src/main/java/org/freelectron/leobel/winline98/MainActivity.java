package org.freelectron.leobel.winline98;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.freelectron.leobel.winline98.dialogs.GameStatsDialog;
import org.freelectron.leobel.winline98.models.WinLine;
import org.freelectron.leobel.winline98.services.GameService;
import org.freelectron.leobel.winline98.services.PreferenceService;
import org.freelectron.leobel.winline98.utils.ActivityUtils;
import org.freelectron.winline.Checker;
import org.freelectron.winline.LogicWinLine;
import org.freelectron.winline.MPoint;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import javax.inject.Inject;

//import timber.log.Timber;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int LOAD_GAME = 1;
    private Thread timerMoveTile;
    private Thread timerAnimateTile;
    private Thread timerAnimateInsertTile;
    private AnimateSelectedTail animateSelectedTail;

    private View boardContainer;
    private BoardView boardView;
    private TextView scoreView;
    private NextView nextView;
    private Button newGame;
    private Button loadGame;
    private Button saveGame;
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
    private boolean shouldStartChronometer;


    private MediaPlayer mp;
    private boolean breakRecordAlert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WinLineApp.getInstance().getComponent().inject(this);

        shouldStartChronometer = true;

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMessage("Your request is being processes");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        mp = MediaPlayer.create(this, R.raw.buton_click);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        boardContainer = findViewById(R.id.boardContainer);
        boardView = (BoardView) findViewById(R.id.board);
        nextView = (NextView) findViewById(R.id.next);
        scoreView = (TextView) findViewById(R.id.score);
        newGame = (Button)findViewById(R.id.new_game);
        loadGame = (Button)findViewById(R.id.load_game);
        saveGame = (Button)findViewById(R.id.save_game);
        timer = (ImageView) findViewById(R.id.timer);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        scoreImage = (RecordTrack) findViewById(R.id.score_image);

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
        });




        //timer.setLeft(timer.getLeft() + 300);

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
            if(highScore > 0){
                if(game.getScore() > highScore){
                    preferenceService.setHighRecord(game.getScore());
                    scoreImage.setMax(game.getScore());
                    if(breakRecordAlert){
                        ActivityUtils.showDialog(this, getString(R.string.new_record_message), getString(R.string.new_record_title));
                        breakRecordAlert = false; // only once during a game

                    }
                }
                scoreImage.setProgress(game.getScore());
                scoreImage.invalidate();
            }
            else{
                preferenceService.setHighRecord(game.getScore());
            }

            scoreView.setText(game.getScore().toString());
            return false;
        });

        endAlertHandler = new Handler(msg -> {
            pauseChronometer();
            scoreView.setText(game.getScore().toString());
            setLoadGameOnStart(true);
            setShouldStartChronometer(false);
            GameStatsDialog gameOver = GameStatsDialog.newInstance(game.getScore(), timeWhenStopped, preferenceService.getHighRecord(), true);
            gameOver.setOnCloseListener(() -> {
                setShouldStartChronometer(true);
                stopChronometer();
                createNewGame();
            });
            gameOver.show(getSupportFragmentManager(), "game over");
            return false;
        });

        dimension = boardView.getDimension();

        scoreImage.setOnClickListener( v -> {
            pauseChronometer();
            setLoadGameOnStart(true);
            setShouldStartChronometer(false);
            GameStatsDialog gameInfo = GameStatsDialog.newInstance(game.getScore(), timeWhenStopped, preferenceService.getHighRecord(), false);
            gameInfo.setOnCloseListener(() -> {
                setShouldStartChronometer(true);
                startChronometer();
            });
            gameInfo.show(getSupportFragmentManager(), "game info");

        });

        newGame.setOnClickListener(v -> {
            newGame.startAnimation(ActivityUtils.buttonClick);
            if(preferenceService.getAllowTouchSoundPreference()){
                mp.start();
            }
            if(savedCurrentState){
                stopChronometer();
                createNewGame();
                savedCurrentState = false;
                breakRecordAlert = true;
            }
            else{
                pauseChronometer();
                ActivityUtils.showDialog(this, getString(R.string.unsaved_current_state), true, v1 -> {
                    stopChronometer();
                    createNewGame();
                    breakRecordAlert = true;
                }, v2 -> {
                    startChronometer();
                });
            }

        });

        loadGame.setOnClickListener(v -> {
            pauseChronometer();
            loadGameOnStart = true;
            loadGame.startAnimation(ActivityUtils.buttonClick);
            if(preferenceService.getAllowTouchSoundPreference()){
                mp.start();
            }
            startActivityForResult(new Intent(this, LoadGameActivity.class), LOAD_GAME);
        });

        saveGame.setOnClickListener(v -> {
            saveGame.startAnimation(ActivityUtils.buttonClick);
            if(preferenceService.getAllowTouchSoundPreference()){
                mp.start();
            }
            mProgressDialog.show();
            if(gameService.save(game, (int)(SystemClock.elapsedRealtime() - chronometer.getBase()))){
                savedCurrentState = true;
                mProgressDialog.hide();
                Toast.makeText(this, "Your game was saved!", Toast.LENGTH_SHORT).show();

            }
            else {
                mProgressDialog.hide();
                ActivityUtils.showDialog(this, "Your game can\'t be saved.");
            }
        });

        //createNewGame();
        boardView.setOnTouchListener((v, event) -> {
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
            BoardView board = (BoardView) v;
            if (game.getBoard()[i][j] == null) {
                emptyPlace = true;
            }
            if (orig != null && emptyPlace) { // try to move to specific location
                MPoint dest = new MPoint(i, j);
                path = game.getPath(orig, dest);
                if (path == null) { // can't move there!!!
                    return true;
                }
                savedCurrentState = false;
                stopAnimateTail(() -> {
                    MoveTile(board);
                });
                return true;
            } else if (emptyPlace) {
                return true;
            } else { // select ball
                orig = new MPoint(i, j);
                if(animateSelectedTail != null && animateSelectedTail.isRunning()){ // there is one ball selected already
                    stopAnimateTail(()-> {
                        animateTail(orig);
                    });
                }
                else {
                    animateTail(orig);
                }
                return true;
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                pauseChronometer();

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                startChronometer();
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LOAD_GAME && resultCode == Activity.RESULT_OK){
            loadGameOnStart = true;
            savedCurrentState = true;
            WinLine loadedGame = (WinLine) data.getSerializableExtra(LoadGameActivity.GAME_LOADED);
            timeWhenStopped = -1L * loadedGame.getTime();
            game = new LogicWinLine(loadedGame.getBoard(), loadedGame.getNext(), loadedGame.getScore());
            breakRecordAlert = true;
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

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void createNewGame(){
        if(!loadGameOnStart){
            try {
                game = new LogicWinLine(dimension);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            loadGameOnStart = false;
        }

        boardView.setBoard(game.getBoard());
        nextView.setBoard(this.game.getNext());
        this.scoreView.setText(game.getScore().toString());
        scoreImage.setProgress(game.getScore());
        boardView.invalidate();
        nextView.invalidate();
        scoreImage.invalidate();
        if(shouldStartChronometer)
            startChronometer();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.share) {
            pauseChronometer();
            loadGameOnStart = true;
            shareApp();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void MoveTile(BoardView board) {
        timerMoveTile = new Thread(new AnimateMoveTails(boardHandler, nextHandler, scoreHandler, endAlertHandler));
        timerMoveTile.start();
    }

    private void animateTail(MPoint point){
        Checker tail = game.getBoard()[point.getX()][point.getY()];
        animateSelectedTail = new AnimateSelectedTail(new AnimateChecker(tail), boardHandler);
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

    public void setLoadGameOnStart(boolean loadGameOnStart) {
        this.loadGameOnStart = loadGameOnStart;
    }

    public void setShouldStartChronometer(boolean shouldStartChronometer) {
        this.shouldStartChronometer = shouldStartChronometer;
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
                    Thread.sleep(30);
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
                for (int k = 0; k < score.length; k++) {
                    if (score[k] >= 5) {
                        game.deleteSequence(limit[k * 2], limit[(k * 2) + 1], (k * 2) + 1);
                        game.addScore(score[k]);
                    }
                }
                boardView.setBoard(game.getBoard());
                boardHandler.sendMessage(new Message());
                scoreHandler.sendMessage(new Message());
            } else {
                try {
                    List<Integer> positions = game.addCheckers();
                    Checker[][]board = game.getBoard();
                    List<AnimateChecker> tails = new ArrayList<>(3);
                    for(Integer pos: positions){
                        int x = pos / dimension;
                        int y = pos % dimension;
                        tails.add(new AnimateInsertChecker(board[x][y]));
                    }
                    game.buildNext(3);
                    animateInsertTile(tails, () -> {
                        boardView.setBoard(game.getBoard());
                        nextView.setBoard(game.getNext());
                        this.boardHandler.sendMessage(new Message());
                        this.nextHandler.sendMessage(new Message());
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
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
            for(int i = 0; i< AnimateChecker.FRAMES_COUNT; i++){
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



}
