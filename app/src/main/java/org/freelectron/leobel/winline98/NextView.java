package org.freelectron.leobel.winline98;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;

import org.freelectron.winline.Checker;
import org.freelectron.winline.LogicWinLine;

import java.lang.reflect.Array;

/**
 * Created by leobel on 7/22/16.
 */
public class NextView extends TileView {

    public NextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTileGrid = new Checker[1][3];
        this.mXTileCount = 1;
        this.mYTileCount = 3;
    }

    public NextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mTileGrid = new Checker[1][3];
        this.mXTileCount = 1;
        this.mYTileCount = 3;
    }

    public void setBoard(Checker[] board) {
        mTileGrid[0] = board;
    }
}
