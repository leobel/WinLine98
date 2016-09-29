package org.freelectron.leobel.winline98;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.freelectron.winline.Checker;
import org.freelectron.winline.LogicWinLine;
import org.freelectron.winline.MPoint;

import java.util.concurrent.atomic.AtomicBoolean;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Thread timerMoveTile;
    private Thread timerAnimateTile;
    AnimateSelectedTail animateSelectedTail;

    private BoardView boardView;
    private TextView scoreView;
    private NextView nextView;
    private LogicWinLine game;
    private int dimension;
    MPoint orig;
    String path;

    private Handler boardHandler;
    private Handler nextHandler;
    private Handler scoreHandler;
    private Handler endAlertHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        boardView = (BoardView) findViewById(R.id.board);
        nextView = (NextView) findViewById(R.id.next);
        scoreView = (TextView) findViewById(R.id.score);

        boardHandler = new Handler(msg -> {
            boardView.invalidate();
            return false;
        });

        nextHandler = new Handler(msg -> {
            nextView.invalidate();
            return false;
        });

        scoreHandler = new Handler(msg -> {
            scoreView.setText(game.getScore().toString());
            return false;
        });

        endAlertHandler = new Handler(msg -> {
            scoreView.setText(game.getScore().toString());
            return false;
        });

        dimension = boardView.getDimension();
        try {
            game = new LogicWinLine(9);
            boardView.setBoard(game.getBoard());
            nextView.setBoard(this.game.getNext());
            this.scoreView.setText(game.getScore().toString());
            boardView.invalidate();
            nextView.invalidate();
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
                if (orig != null && emptyPlace) {
                    MPoint dest = new MPoint(i, j);
                    path = game.getPath(orig, dest);
                    if (path == null) {
                        return true;
                    }
                    stopAnimateTail(() -> {
                        MoveTile(board);
                    });
                    return true;
                } else if (emptyPlace) {
                    return true;
                } else {
                    orig = new MPoint(i, j);
                    if(animateSelectedTail != null && animateSelectedTail.isRunning()){
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
        } catch (Exception e) {
            e.printStackTrace();
        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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
        if (id == R.id.action_settings) {
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
            Timber.d("Animation: Start moving animation");
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
            Timber.d("Animation: End moving animation");
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
                this.boardHandler.sendMessage(new Message());
                MainActivity.this.scoreHandler.sendMessage(new Message());
            } else {
                try {
                    game.addCheckers();
                    game.buildNext(3);
                    boardView.setBoard(game.getBoard());
                    nextView.setBoard(game.getNext());
                    this.boardHandler.sendMessage(new Message());
                    this.nextHandler.sendMessage(new Message());
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
                    Timber.d("Animation: Start animate selected tail");
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
