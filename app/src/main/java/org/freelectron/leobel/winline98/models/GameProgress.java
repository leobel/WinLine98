package org.freelectron.leobel.winline98.models;

import java.io.Serializable;

/**
 * Created by leobel on 11/28/16.
 */

public class GameProgress implements Serializable {

    private int score;
    private int oneShotScore;
    private int consecutiveScores;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setOneShotScore(int oneShotScore) {
        this.oneShotScore = oneShotScore;
    }

    public int getOneShotScore() {
        return oneShotScore;
    }

    public void setConsecutiveScores(int consecutiveScores) {
        this.consecutiveScores = consecutiveScores;
    }

    public int getConsecutiveScores() {
        return consecutiveScores;
    }
}
