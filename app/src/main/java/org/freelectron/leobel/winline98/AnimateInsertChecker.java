package org.freelectron.leobel.winline98;

import org.freelectron.winline.Checker;
import org.freelectron.winline.LogicWinLine;
import org.freelectron.winline.MPoint;

/**
 * Created by leobel on 10/3/16.
 */
public class AnimateInsertChecker extends AnimateChecker {

    private int index = FRAMES_COUNT*CHECKER_COUNT;


    public AnimateInsertChecker(MPoint p, LogicWinLine.Color color) {
        super(p, color);
    }

    public AnimateInsertChecker(LogicWinLine.Color color) {
        super(color);
    }

    public AnimateInsertChecker(Checker checker) {
        super(checker);
    }

    @Override
    public void setAnimateColor(int i) {
        switch (Color()){
            case RED:
                animateColor = AnimateColor.values()[index + i];
                break;
            case WHITE: // pink
                animateColor = AnimateColor.values()[index + FRAMES_COUNT + i];
                break;
            case BLUE: // pink
                animateColor = AnimateColor.values()[index + 2*FRAMES_COUNT + i];
                break;
            case BLACK:
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
}
