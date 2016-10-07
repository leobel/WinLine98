package org.freelectron.leobel.winline98.models;

import org.freelectron.winline.Checker;
import org.freelectron.winline.LogicWinLine;

/**
 * Created by leobel on 10/7/16.
 */
public class WinLine extends LogicWinLine {

    private int time;

    public WinLine(int dimesion) throws Exception {
        super(dimesion);
    }

    public WinLine(Checker[][] board, Checker[] next, int score) {
        super(board, next, score);
    }

    public WinLine(Checker[][] board, Checker[] next, int score, int time){
        super(board, next, score);
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
