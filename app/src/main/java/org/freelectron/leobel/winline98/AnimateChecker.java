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

    public AnimateChecker(MPoint p, LogicWinLine.Color color) {
        super(p, color);
    }

    public AnimateChecker(LogicWinLine.Color color) {
        super(color);
    }

    public AnimateChecker(Checker checker) {
        super(checker.getPosition(), checker.Color());
        this.checker = checker;
    }

    public AnimateColor getAnimateColor(){
        return animateColor;
    }

    public Checker getChecker(){return checker;}

    public void setAnimateColor(int i){
        switch (Color()){
            case RED:
                animateColor = AnimateColor.values()[i];
                break;
            case WHITE: // pink
                animateColor = AnimateColor.values()[FRAMES_COUNT + i];
                break;
            case BLUE: // pink
                animateColor = AnimateColor.values()[2*FRAMES_COUNT + i];
                break;
            case BLACK:
                animateColor = AnimateColor.values()[3*FRAMES_COUNT + i];
                break;
            case GREEN:
                animateColor = AnimateColor.values()[4*FRAMES_COUNT + i];
                break;
            case YELLOW:
                animateColor = AnimateColor.values()[5*FRAMES_COUNT + i];
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
    }


}
