package org.freelectron.leobel.winline98.utils;

import android.os.Message;

import org.freelectron.leobel.winline98.AnimateChecker;
import org.freelectron.winline.Checker;
import org.freelectron.winline.LogicWinLine;

import java.util.List;

/**
 * Created by leobel on 12/29/16.
 */

public interface GameAnimation extends GameNotification {

    LogicWinLine getGame();

    int getDimension();
    int getCombo();

    boolean getComboIsRunning();

    void animateTail(int x, int y);
    void stopAnimateTail(Runnable doAfter);
    void MoveTile(int x, int y, String path, Runnable doAfter);
    void animateInsertTile(List<AnimateChecker> tails, Runnable doAfter);
    void animateScoreTile(List<AnimateChecker> tiles, Runnable doAfter);

    void setBoardGame(Checker[][] boardGame);
    void setNextBoardGame(Checker[] nextBoardGame);


}
