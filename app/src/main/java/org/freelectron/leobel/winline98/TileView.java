package org.freelectron.leobel.winline98;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import org.freelectron.winline.LogicWinLine;

import java.util.HashMap;

/**
 * Created by leobel on 7/22/16.
 */
public class TileView extends View {
    protected final Paint mPaint;
    protected HashMap<LogicWinLine.Color, Bitmap> mTileArray;
    protected HashMap<AnimateChecker.AnimateColor, Bitmap> mGifArray;
    protected int mTileSize;
    protected int mXOffset;
    protected int mXTileCount;
    protected int mYOffset;
    protected int mYTileCount;
    private Context context;

    public TileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.mPaint = new Paint();
        this.mTileSize = 60;
        this.mTileArray = new HashMap();
        this.mGifArray = new HashMap();
        this.mYTileCount = 9;
        this.mXTileCount = 9;
    }

    public TileView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.mPaint = new Paint();
        this.mTileSize = 60;
        this.mTileArray = new HashMap();
        this.mGifArray = new HashMap();
        this.mYTileCount = 9;
        this.mXTileCount = 9;
    }

    private void load() {
        loadTile(LogicWinLine.Color.Empty,  ContextCompat.getDrawable(context, R.drawable.empty));
        loadTile(LogicWinLine.Color.YELLOW, ContextCompat.getDrawable(context, R.drawable.yellow));
        loadTile(LogicWinLine.Color.BLUE,   ContextCompat.getDrawable(context, R.drawable.blue));
        loadTile(LogicWinLine.Color.WHITE,  ContextCompat.getDrawable(context, R.drawable.pink));
        loadTile(LogicWinLine.Color.BLACK,  ContextCompat.getDrawable(context, R.drawable.brown));
        loadTile(LogicWinLine.Color.RED,    ContextCompat.getDrawable(context, R.drawable.red));
        loadTile(LogicWinLine.Color.GREEN,  ContextCompat.getDrawable(context, R.drawable.green));

        loadTile(AnimateChecker.AnimateColor.RED1,  ContextCompat.getDrawable(context, R.drawable.redanimate1));
        loadTile(AnimateChecker.AnimateColor.RED2,  ContextCompat.getDrawable(context, R.drawable.redanimate2));
        loadTile(AnimateChecker.AnimateColor.RED3,  ContextCompat.getDrawable(context, R.drawable.redanimate3));

        loadTile(AnimateChecker.AnimateColor.PINK1,  ContextCompat.getDrawable(context, R.drawable.pinkanimate1));
        loadTile(AnimateChecker.AnimateColor.PINK2,  ContextCompat.getDrawable(context, R.drawable.pinkanimate2));
        loadTile(AnimateChecker.AnimateColor.PINK3,  ContextCompat.getDrawable(context, R.drawable.pinkanimate3));

        loadTile(AnimateChecker.AnimateColor.BLUE1,  ContextCompat.getDrawable(context, R.drawable.blueanimate1));
        loadTile(AnimateChecker.AnimateColor.BLUE2,  ContextCompat.getDrawable(context, R.drawable.blueanimate2));
        loadTile(AnimateChecker.AnimateColor.BLUE3,  ContextCompat.getDrawable(context, R.drawable.blueanimate3));

        loadTile(AnimateChecker.AnimateColor.BROWN1,  ContextCompat.getDrawable(context, R.drawable.brownanimate1));
        loadTile(AnimateChecker.AnimateColor.BROWN2,  ContextCompat.getDrawable(context, R.drawable.brownanimate2));
        loadTile(AnimateChecker.AnimateColor.BROWN3,  ContextCompat.getDrawable(context, R.drawable.brownanimate3));

        loadTile(AnimateChecker.AnimateColor.GREEN1,  ContextCompat.getDrawable(context, R.drawable.greenanimate1));
        loadTile(AnimateChecker.AnimateColor.GREEN2,  ContextCompat.getDrawable(context, R.drawable.greenanimate2));
        loadTile(AnimateChecker.AnimateColor.GREEN3,  ContextCompat.getDrawable(context, R.drawable.greenanimate3));

        loadTile(AnimateChecker.AnimateColor.YELLOW1,  ContextCompat.getDrawable(context, R.drawable.yellowanimate1));
        loadTile(AnimateChecker.AnimateColor.YELLOW2,  ContextCompat.getDrawable(context, R.drawable.yellowanimate2));
        loadTile(AnimateChecker.AnimateColor.YELLOW3,  ContextCompat.getDrawable(context, R.drawable.yellowanimate3));

    }

    public void loadTile(LogicWinLine.Color key, Drawable tile) {
        Bitmap bitmap = Bitmap.createBitmap(this.mTileSize, this.mTileSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        tile.setBounds(0, 0, this.mTileSize, this.mTileSize);
        tile.draw(canvas);
        this.mTileArray.put(key, bitmap);
    }

    public void loadTile(AnimateChecker.AnimateColor key, Drawable tile) {
        Bitmap bitmap = Bitmap.createBitmap(this.mTileSize, this.mTileSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        tile.setBounds(0, 0, this.mTileSize, this.mTileSize);
        tile.draw(canvas);
        this.mGifArray.put(key, bitmap);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.mTileSize = Math.min(w / this.mXTileCount, h / this.mYTileCount);
        int measuredWidth = this.mTileSize * this.mXTileCount;
        int measuredHeight = this.mTileSize * this.mYTileCount;
//        int measuredWidth = this.mTileSize * this.mXTileCount;
//        int measuredHeight = this.mTileSize * this.mYTileCount;
//        if (w < measuredWidth || h < measuredHeight) {
//            this.mTileSize = Math.min(w / this.mXTileCount, h / this.mYTileCount);
//            measuredWidth = this.mTileSize * this.mXTileCount;
//            measuredHeight = this.mTileSize * this.mYTileCount;
//        }
        load();
        this.mXOffset = (w - measuredWidth) / 2;
        this.mYOffset = (h - measuredHeight) - 1;
//        this.mXOffset = getLeft();
//        this.mYOffset = getTop();
    }

}
