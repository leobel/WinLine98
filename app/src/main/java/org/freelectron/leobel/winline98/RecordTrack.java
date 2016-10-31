package org.freelectron.leobel.winline98;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import org.freelectron.leobel.winline98.widgets.StartShape;

/**
 * Created by leobel on 10/25/16.
 */
public class RecordTrack extends View {
    private StartShape myShape;
    private float ratioRadius;
    private float ratioInnerRadius;
    private int numberOfPoint;
    private Bitmap starBitmap;
    private Paint starPaint;
    private Bitmap backBitmap;
    private Paint q;
    private int max;
    private int progress;
    private Canvas starCanvas;
    private Canvas backCanvas;
    private RectF recordProgress;
    private RectF backRect;
    private PorterDuffXfermode porterDuffXfermode;
    private Paint backPaint;

    public RecordTrack(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMyView(context, attrs);
    }



    public RecordTrack(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMyView(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float min = Math.min(getWidth(), getHeight());

        float x = getWidth()/2f;
        float y = getHeight()/2f;

        float radius = min * ratioRadius;
        float innerRadius = min * ratioInnerRadius;

        myShape.setStar(x, y, radius, innerRadius);

        float startHeight = myShape.getHeight();

        // Draw the STAR mask in a bitmap
        Path path = myShape.getPath();

        starCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        starCanvas.drawPath(path, starPaint);

        // Draw the background rectangle in a bitmap
        backCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        if(max > 0 && progress > 0){
            float divider = backBitmap.getHeight() - (progress * backBitmap.getHeight()/ max) - (backBitmap.getHeight() - startHeight);

            recordProgress.set(0, divider, backBitmap.getWidth(), backBitmap.getHeight());
            backRect.set(0, 0, backBitmap.getWidth(), divider);

            backCanvas.drawRect(recordProgress, starPaint);
        }
        else{
            backRect.set(0, 0, backBitmap.getWidth(), backBitmap.getHeight());
        }
        backCanvas.drawRect(backRect, backPaint);

        canvas.drawBitmap(backBitmap, 0, 0, q);
        q.setXfermode(porterDuffXfermode);
        canvas.drawBitmap(starBitmap, 0, 0, q);
        q.setXfermode(null);
    }

    private void initMyView(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecordTrack);
        ratioRadius = a.getFloat(R.styleable.RecordTrack_radius, 0.5f);
        ratioInnerRadius = a.getFloat(R.styleable.RecordTrack_inner_radius, 0.25f);
        numberOfPoint = a.getInt(R.styleable.RecordTrack_inner_radius, 5);
        max = a.getInt(R.styleable.RecordTrack_max, 0);
        progress = a.getInt(R.styleable.RecordTrack_progress, 0);
        a.recycle();

        myShape = new StartShape();

        q = new Paint(Paint.ANTI_ALIAS_FLAG);
        q.setFilterBitmap(true);
        setLayerType(LAYER_TYPE_HARDWARE, q);
        porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);

        starPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        starPaint.setColor(getResources().getColor(R.color.star_color));
        starPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        backPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backPaint.setColor(getResources().getColor(R.color.black));
        backPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        starBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        backBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        starCanvas = new Canvas(starBitmap);
        backCanvas = new Canvas(backBitmap);

        recordProgress = new RectF(0, 0, backBitmap.getWidth(), backBitmap.getHeight());
        backRect = new RectF(0, 0, backBitmap.getWidth(), backBitmap.getHeight());
    }

    public void setMax(int max){this.max = max;}

    public void setProgress(int progress){
        this.progress = progress < 0 ? 0 : progress > max ? max : progress;

    }
}
