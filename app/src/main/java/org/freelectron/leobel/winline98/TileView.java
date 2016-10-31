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
    private OnTileViewListener listener;

    public TileView(Context context){
        super(context);
        this.context = context;
        this.mPaint = new Paint();
        this.mTileSize = 60;
        this.mTileArray = new HashMap();
        this.mGifArray = new HashMap();
        this.mYTileCount = 9;
        this.mXTileCount = 9;
    }

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

        loadTile(AnimateChecker.AnimateColor.RED_INSERT1,  ContextCompat.getDrawable(context, R.drawable.redanimateinsert1));
        loadTile(AnimateChecker.AnimateColor.RED_INSERT2,  ContextCompat.getDrawable(context, R.drawable.redanimateinsert2));
        loadTile(AnimateChecker.AnimateColor.RED_INSERT3,  ContextCompat.getDrawable(context, R.drawable.redanimateinsert3));

        loadTile(AnimateChecker.AnimateColor.PINK_INSERT1,  ContextCompat.getDrawable(context, R.drawable.pinkanimateinsert1));
        loadTile(AnimateChecker.AnimateColor.PINK_INSERT2,  ContextCompat.getDrawable(context, R.drawable.pinkanimateinsert2));
        loadTile(AnimateChecker.AnimateColor.PINK_INSERT3,  ContextCompat.getDrawable(context, R.drawable.pinkanimateinsert3));

        loadTile(AnimateChecker.AnimateColor.BLUE_INSERT1,  ContextCompat.getDrawable(context, R.drawable.blueanimateinsert1));
        loadTile(AnimateChecker.AnimateColor.BLUE_INSERT2,  ContextCompat.getDrawable(context, R.drawable.blueanimateinsert2));
        loadTile(AnimateChecker.AnimateColor.BLUE_INSERT3,  ContextCompat.getDrawable(context, R.drawable.blueanimateinsert3));

        loadTile(AnimateChecker.AnimateColor.BROWN_INSERT1,  ContextCompat.getDrawable(context, R.drawable.brownanimateinsert1));
        loadTile(AnimateChecker.AnimateColor.BROWN_INSERT2,  ContextCompat.getDrawable(context, R.drawable.brownanimateinsert2));
        loadTile(AnimateChecker.AnimateColor.BROWN_INSERT3,  ContextCompat.getDrawable(context, R.drawable.brownanimateinsert3));

        loadTile(AnimateChecker.AnimateColor.GREEN_INSERT1,  ContextCompat.getDrawable(context, R.drawable.greenanimateinsert1));
        loadTile(AnimateChecker.AnimateColor.GREEN_INSERT2,  ContextCompat.getDrawable(context, R.drawable.greenanimateinsert2));
        loadTile(AnimateChecker.AnimateColor.GREEN_INSERT3,  ContextCompat.getDrawable(context, R.drawable.greenanimateinsert3));

        loadTile(AnimateChecker.AnimateColor.YELLOW_INSERT1,  ContextCompat.getDrawable(context, R.drawable.yellowanimateinsert1));
        loadTile(AnimateChecker.AnimateColor.YELLOW_INSERT2,  ContextCompat.getDrawable(context, R.drawable.yellowanimateinsert2));
        loadTile(AnimateChecker.AnimateColor.YELLOW_INSERT3,  ContextCompat.getDrawable(context, R.drawable.yellowanimateinsert3));

    }

    public int getTitleSize(){return mTileSize;}

    public int getLeftPosition(){return mXOffset;}

    public int getRightPosition(){return getLeft() + mXOffset + (mTileSize * mXTileCount);}



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
        load();
        this.mXOffset = Math.abs(w - measuredWidth) / 2;
        this.mYOffset = Math.abs(h - measuredHeight) / 2;
        if(listener != null)
            listener.onSetPosition();
    }
    
    public void setListener(OnTileViewListener listener){
        this.listener = listener;
    }
    
    public interface OnTileViewListener{
        void onSetPosition();
    }

}
