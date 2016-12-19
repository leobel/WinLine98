package org.freelectron.leobel.winline98;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.PointTarget;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.freelectron.winline.Checker;
import org.freelectron.winline.LogicWinLine;
import org.freelectron.winline.MPoint;

import timber.log.Timber;

public class InteractiveHelpActivity extends AppCompatActivity implements View.OnClickListener {

    BoardView boardView;
    NextView nextView;
    RecordTrack scoreImage;
    TextView scoreView;
    View comboTrack;
    ImageButton newGame;
    ImageButton loadGame;
    ImageButton saveGame;

    Point ballLocation;
    Point emptyLocation;

    ShowcaseView showcaseView;
    Target[] targets;
    static final int[] targetTitle = new int[]{R.string.guide_step1, R.string.guide_step2, R.string.guide_step3, R.string.guide_step4, R.string.guide_step5,
                                  R.string.guide_step6, R.string.guide_step7, R.string.guide_step8};
    static final int[] targetDescription = new int[]{R.string.guide_step1_description, R.string.guide_step2_description, R.string.guide_step3_description, R.string.guide_step4_description, R.string.guide_step5_description,
            R.string.guide_step6_description, R.string.guide_step7_description, R.string.guide_step8_description};
    int index = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View boardContainer = findViewById(R.id.board_container);
        boardView = (BoardView) findViewById(R.id.board);
        nextView = (NextView) findViewById(R.id.next);
        ImageView timer = (ImageView) findViewById(R.id.timer);
        Chronometer chronometer = (Chronometer) findViewById(R.id.chronometer);
        scoreImage = (RecordTrack) findViewById(R.id.score_image);
        scoreView = (TextView) findViewById(R.id.score);
        comboTrack = findViewById(R.id.combo_track);
        newGame = (ImageButton)findViewById(R.id.new_game);
        loadGame = (ImageButton)findViewById(R.id.load_game);
        saveGame = (ImageButton)findViewById(R.id.save_game);
        TextView comboTime = (TextView) findViewById(R.id.combo_timer);

        scoreView.setText("217");
        comboTime.setText("10.0");
        comboTrack.setVisibility(View.VISIBLE);
        scoreImage.setMax(500);
        scoreImage.setProgress(217);


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

            setupGame();
        });

        showcaseView = new ShowcaseView.Builder(this)
                .setStyle(R.style.CustomShowcaseTheme2)
                .blockAllTouches()
                .setOnClickListener(this)
                .build();
    }

    private void setupGame() {
        Checker[][] board = new Checker[9][9];
        board[1][4] = new Checker(new MPoint(1,4), LogicWinLine.Color.BLUE);
        board[5][2] = new Checker(new MPoint(5,2), LogicWinLine.Color.BLUE);
        board[5][3] = new Checker(new MPoint(5,3), LogicWinLine.Color.BLUE);
        board[5][5] = new Checker(new MPoint(5,5), LogicWinLine.Color.BLUE);
        board[5][6] = new Checker(new MPoint(5,6), LogicWinLine.Color.BLUE);
        board[2][1] = new Checker(new MPoint(2,1), LogicWinLine.Color.BLACK);
        board[2][8] = new Checker(new MPoint(2,8), LogicWinLine.Color.YELLOW);
        board[4][2] = new Checker(new MPoint(4,2), LogicWinLine.Color.RED);
        board[7][4] = new Checker(new MPoint(7,4), LogicWinLine.Color.WHITE);
        board[8][7] = new Checker(new MPoint(8,7), LogicWinLine.Color.GREEN);
        Checker[]next = new Checker[]{new Checker(LogicWinLine.Color.RED), new Checker(LogicWinLine.Color.BLUE), new Checker(LogicWinLine.Color.GREEN)};
//        LogicWinLine game = new LogicWinLine(board, next, 0);
        boardView.setBoard(board);
        nextView.setBoard(next);

        int[] location = new int[2];
        boardView.getLocationInWindow(location);
        ballLocation = new Point(location[0] + boardView.getLeftPosition() + 4 * boardView.mTileSize + boardView.mTileSize/2, location[1] + boardView.getTopPosition() + boardView.mTileSize + boardView.mTileSize/2);
        emptyLocation = new Point(ballLocation.x, ballLocation.y + 4 * boardView.mTileSize);
        Timber.d("target ball x:" + (location[0] + boardView.getLeftPosition()));
        Timber.d("target ball y:" + (location[1] + boardView.getTopPosition()));
        Target target1 = new PointTarget(ballLocation);
        Target target2 = new PointTarget(emptyLocation);
        Target target3 = new ViewTarget(comboTrack);
        Target target4 = new ViewTarget(nextView);
        Target target5 = new ViewTarget(scoreImage);
        Target target6 = new ViewTarget(saveGame);
        Target target7 = new ViewTarget(newGame);
        Target target8 = new ViewTarget(loadGame);
        targets = new Target[]{target1, target2, target3, target4, target5, target6, target7, target8};
        index = 0;
        showcaseView.setShowcase(targets[index], true);
        showcaseView.setContentTitle(getString(targetTitle[index]));
        showcaseView.setContentText(getString(targetDescription[index++]));

    }

    @Override
    public void onClick(View view) {
        if(index < targets.length){
            showcaseView.setShowcase(targets[index], true);
            showcaseView.setContentTitle(getString(targetTitle[index]));
            showcaseView.setContentText(getString(targetDescription[index++]));
        }
        else{
            showcaseView.hide();
        }

    }
}
