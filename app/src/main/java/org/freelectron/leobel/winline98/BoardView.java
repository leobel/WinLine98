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

    int dimension;
    LogicWinLine game;
    Checker[][] mTileGrid;
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

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int x = 0; x < this.mXTileCount; x++) {
            for (int y = 0; y < this.mYTileCount; y++) {
                Checker f = this.mTileGrid[x][y];
                LogicWinLine.Color c = LogicWinLine.Color.Empty;
                if (f != null) {
                    c = f.Color();
                }
//                AnimationDrawable drawable = (AnimationDrawable) mGifArray.get(c);
//                drawable.start();
                Bitmap bitmap;
                if(f instanceof AnimateChecker){
                    bitmap = this.mGifArray.get(((AnimateChecker) f).getAnimateColor());
                }
                else{
                    bitmap = this.mTileArray.get(c);
                }

                canvas.drawBitmap(bitmap, (float) (this.mXOffset + (this.mTileSize * y)), (float) (this.mYOffset + (this.mTileSize * x)), this.mPaint);
            }
        }
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
