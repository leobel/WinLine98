package org.freelectron.leobel.winline98;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import org.freelectron.winline.Checker;
import org.freelectron.winline.LogicWinLine;
import org.freelectron.winline.MPoint;

import java.lang.reflect.Array;

/**
 * Created by leobel on 7/22/16.
 */
public class BoardView extends TileView implements TileView.OnTileViewListener {

    Runnable doAfterSetPosition;

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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mYOffset = 0;
    }

    private void initBoardView() {
        this.mTileGrid = (Checker[][]) Array.newInstance(Checker.class, this.dimension, this.dimension);
    }

    public void onSetPosition(Runnable after){
        doAfterSetPosition = after;
    }


    @Override
    public void onSetPosition() {
        if(doAfterSetPosition != null)
            doAfterSetPosition.run();
    }
}
