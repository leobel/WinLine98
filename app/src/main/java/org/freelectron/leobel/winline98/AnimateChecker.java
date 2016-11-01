package org.freelectron.leobel.winline98;

import org.freelectron.winline.Checker;
import org.freelectron.winline.LogicWinLine;
import org.freelectron.winline.MPoint;

/**
 * Created by leobel on 9/22/16.
 */
public class AnimateChecker extends Checker {
    AnimateColor animateColor;
    public static int FRAMES_COUNT = 3;
    public static int CHECKER_COUNT = 6;
    Checker checker;
    protected int index;

    public AnimateChecker(MPoint p, LogicWinLine.Color color, int index) {
        super(p, color);
        this.index = index;
    }

    public AnimateChecker(LogicWinLine.Color color, int index) {
        super(color);
        this.index = index;
    }

    public AnimateChecker(Checker checker , int index) {
        super(checker.getPosition(), checker.Color());
        this.checker = checker;
        this.index = index;
    }

    public static int getInsertIndex() {
        return FRAMES_COUNT*CHECKER_COUNT;
    }

    public static int getScoreIndex() {
        return 2 * getInsertIndex();
    }

    public AnimateColor getAnimateColor(){
        return animateColor;
    }

    public Checker getChecker(){return checker;}

    public void setAnimateColor(int i){
        switch (Color()){
            case RED:
                animateColor = AnimateColor.values()[index + i];
                break;
            case WHITE: // pink
                animateColor = AnimateColor.values()[index + FRAMES_COUNT + i];
                break;
            case BLUE:
                animateColor = AnimateColor.values()[index + 2*FRAMES_COUNT + i];
                break;
            case BLACK: // brown
                animateColor = AnimateColor.values()[index + 3*FRAMES_COUNT + i];
                break;
            case GREEN:
                animateColor = AnimateColor.values()[index + 4*FRAMES_COUNT + i];
                break;
            case YELLOW:
                animateColor = AnimateColor.values()[index + 5*FRAMES_COUNT + i];
                break;
        }

    }



    public enum AnimateColor {
        RED1,
        RED2,
        RED3,
        PINK1,
        PINK2,
        PINK3,
        BLUE1,
        BLUE2,
        BLUE3,
        BROWN1,
        BROWN2,
        BROWN3,
        GREEN1,
        GREEN2,
        GREEN3,
        YELLOW1,
        YELLOW2,
        YELLOW3,
        RED_INSERT1,
        RED_INSERT2,
        RED_INSERT3,
        PINK_INSERT1,
        PINK_INSERT2,
        PINK_INSERT3,
        BLUE_INSERT1,
        BLUE_INSERT2,
        BLUE_INSERT3,
        BROWN_INSERT1,
        BROWN_INSERT2,
        BROWN_INSERT3,
        GREEN_INSERT1,
        GREEN_INSERT2,
        GREEN_INSERT3,
        YELLOW_INSERT1,
        YELLOW_INSERT2,
        YELLOW_INSERT3,
        RED_SCORE1,
        RED_SCORE2,
        RED_SCORE3,
        PINK_SCORE1,
        PINK_SCORE2,
        PINK_SCORE3,
        BLUE_SCORE1,
        BLUE_SCORE2,
        BLUE_SCORE3,
        BROWN_SCORE1,
        BROWN_SCORE2,
        BROWN_SCORE3,
        GREEN_SCORE1,
        GREEN_SCORE2,
        GREEN_SCORE3,
        YELLOW_SCORE1,
        YELLOW_SCORE2,
        YELLOW_SCORE3

    }


}
