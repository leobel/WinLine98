package org.freelectron.leobel.winline98.models;

import org.freelectron.winline.Checker;
import org.freelectron.winline.LogicWinLine;

import java.io.Serializable;

/**
 * Created by leobel on 10/7/16.
 */
public class WinLine extends LogicWinLine implements Serializable {

    private long id;
    private int time;

    public WinLine(int dimesion) throws Exception {
        super(dimesion);
    }

    public WinLine(Checker[][] board, Checker[] next, int score) {
        super(board, next, score);
    }

    public WinLine(long id, Checker[][] board, Checker[] next, int score, int time){
        super(board, next, score);
        this.time = time;
        this.id = id;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Long getId() {
        return id;
    }
}
