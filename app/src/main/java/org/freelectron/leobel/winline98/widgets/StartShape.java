package org.freelectron.leobel.winline98.widgets;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/**
 * Created by leobel on 10/25/16.
 */
public class StartShape {

    private Paint paint;
    private Path path;

    private int minDim, topXPoint, topYPoint;
    private double bigHypot, bigA, bigB, littleHypot, littleA, littleB;

    public StartShape() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        path = new Path();
    }

    public void setStar(View v){

        path.reset();

        minDim = Math.min(v.getWidth() - v.getPaddingLeft() - v.getPaddingRight(), v.getHeight() - v.getPaddingTop() - v.getPaddingBottom());

        bigHypot = (minDim / Math.cos(Math.toRadians(18)));
        bigB = minDim;
        bigA = Math.tan(Math.toRadians(18)) * bigB;

        littleHypot = bigHypot / (2 + Math.cos(Math.toRadians(72)) + Math.cos(Math.toRadians(72)));
        littleA = Math.cos(Math.toRadians(72)) * littleHypot;
        littleB = Math.sin(Math.toRadians(72)) * littleHypot;

        topXPoint = (v.getWidth() - v.getPaddingLeft() - v.getPaddingRight()) / 2;
        topYPoint = v.getPaddingTop();

        path.moveTo(topXPoint, topYPoint);
        path.lineTo((int) (topXPoint + bigA), (int) (topYPoint + bigB));
        path.lineTo((int) (topXPoint - littleA - littleB), (int) (topYPoint + littleB));
        path.lineTo((int) (topXPoint + littleA + littleB), (int) (topYPoint + littleB));
        path.lineTo((int) (topXPoint - bigA), (int) (topYPoint + bigB));
        path.lineTo(topXPoint, topYPoint);
        path.close();


        path.close();
    }

    public void setStar(float x, float y, float radius, float innerRadius){

        double angle = 2.0 * Math.PI/5;

        path.reset();

//        path.moveTo(
//                (float)(x + radius * Math.cos(0)),
//                (float)(y + radius * Math.sin(0)));
//        path.lineTo(
//                (float)(x + innerRadius * Math.cos(0 + section/2.0)),
//                (float)(y + innerRadius * Math.sin(0 + section/2.0)));
//
//        for(int i=1; i< numOfPt; i++){
//            path.lineTo(
//                    (float)(x + radius * Math.cos(section * i)),
//                    (float)(y + radius * Math.sin(section * i)));
//            path.lineTo(
//                    (float)(x + innerRadius * Math.cos(section * i + section/2.0)),
//                    (float)(y + innerRadius * Math.sin(section * i + section/2.0)));
//        }

        path.moveTo(x, y + innerRadius); //p1

        path.lineTo((float)(x + Math.sin(angle/2) * radius), (float)(y + Math.cos(angle/2) * radius)); //o1

        path.lineTo((float)(x + Math.sin(angle) * innerRadius), (float)(y + Math.cos(angle) * innerRadius)); // p2

        path.lineTo((float)(x + Math.sin(angle) * radius), (float)(y - Math.cos(angle) * radius)); // o2

        path.lineTo((float)(x + Math.sin(angle/2) * innerRadius), (float)(y - Math.cos(angle/2) * innerRadius)); // p3

        path.lineTo((float)(x), (float)(y - radius)); // o3

        path.lineTo((float)(x - Math.sin(angle/2) * innerRadius), (float)(y - Math.cos(angle/2) * innerRadius)); // p4

        path.lineTo((float)(x - Math.sin(angle) * radius), (float)(y - Math.cos(angle) * radius)); // o4

        path.lineTo((float)(x - Math.sin(angle) * innerRadius), (float)(y + Math.cos(angle) * innerRadius)); // p5

        path.lineTo((float)(x - Math.sin(angle/2) * radius), (float)(y + Math.cos(angle/2) * radius)); // o5



        path.close();


    }

    public Path getPath(){
        return path;
    }

    public Paint getPaint(){
        return paint;
    }
}
