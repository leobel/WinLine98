package org.freelectron.leobel.winline98;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.freelectron.leobel.winline98.utils.OnTouchBallListener;
import org.freelectron.leobel.winline98.utils.OnTouchEmptyListener;
import org.freelectron.winline.Checker;

import java.lang.reflect.Array;

import timber.log.Timber;

/**
 * Created by leobel on 7/22/16.
 */
public class BoardView extends TileView implements TileView.OnTileViewListener {

    Runnable doAfterSetPosition;
    private OnTouchEmptyListener touchEmptyListener = (x, y) -> {};
    private OnTouchBallListener touchBallListener = (x, y) -> {};

    public BoardView(Context context){
        super(context);
        this.dimension = 9;
        setListener(this);
        initBoardView();
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.dimension = 9;
        setListener(this);
        initBoardView();
    }

    public BoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.dimension = 9;
        setListener(this);
        initBoardView();
    }

    public int getDimension(){return dimension;}

    public void setBoard(Checker[][] board) {
        this.mTileGrid = board;
    }

    private void initBoardView() {
        this.mTileGrid = (Checker[][]) Array.newInstance(Checker.class, this.dimension, this.dimension);
    }

    public void onSetPosition(Runnable after){
        doAfterSetPosition = after;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int i = (int) ((event.getY() - mYOffset) / mTileSize);
        int j = (int) ((event.getX() - mXOffset) / mTileSize);
        if(i >= 0 && i < dimension && j >= 0 && j < dimension){
            if(mTileGrid[i][j] == null){
                touchEmptyListener.onTouchEmpty(i, j);
            }
            else{
                touchBallListener.onTouchBall(i, j);
            }
        }
        return super.onTouchEvent(event);
    }


    @Override
    public void onSetPosition() {
        if(doAfterSetPosition != null)
            doAfterSetPosition.run();
    }

    public void setOnTouchBallListener(OnTouchBallListener l){
        touchBallListener = l;
    }

    public void setOnTouchEmptyListener(OnTouchEmptyListener l){
        touchEmptyListener = l;
    }
}
