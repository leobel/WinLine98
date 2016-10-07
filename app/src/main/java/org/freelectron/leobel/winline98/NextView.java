package org.freelectron.leobel.winline98;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;

import org.freelectron.winline.Checker;
import org.freelectron.winline.LogicWinLine;

/**
 * Created by leobel on 7/22/16.
 */
public class NextView extends TileView {
    private Checker[] mTileGrid;

    public NextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTileGrid = new Checker[3];
        this.mXTileCount = 3;
        this.mYTileCount = 1;
    }

    public NextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mTileGrid = new Checker[3];
        this.mXTileCount = 3;
        this.mYTileCount = 1;
    }

    public void setBoard(Checker[] board) {
        this.mTileGrid = board;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int x = 0; x < this.mTileGrid.length; x++) {
            Checker f = this.mTileGrid[x];
            LogicWinLine.Color c = LogicWinLine.Color.Empty;
            if (f != null) {
                c = f.Color();
            }


            canvas.drawBitmap((Bitmap) this.mTileArray.get(c), (float) (this.mXOffset + (this.mTileSize * x)), (float) this.mYOffset, this.mPaint);
        }
    }

//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        //this.mYOffset = mYOffset + 1;
//    }
}
