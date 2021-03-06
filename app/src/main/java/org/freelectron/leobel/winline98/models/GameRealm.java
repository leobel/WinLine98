package org.freelectron.leobel.winline98.models;

import org.freelectron.winline.Checker;
import org.freelectron.winline.LogicWinLine;
import org.freelectron.winline.MPoint;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by leobel on 10/7/16.
 */
public class GameRealm extends RealmObject{

    @PrimaryKey
    private Long id;
    private int score;
    private int time;
    private String createdOn;
    private RealmList<CheckerRealm> next;
    private RealmList<CheckerRealm> board;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public void copyFrom(LogicWinLine game, Realm realm, int time) {
        RealmList<CheckerRealm> nextChecker = new RealmList<>();

        for(Checker checker : game.getNext()){
            CheckerRealm checkerRealm = realm.createObject(CheckerRealm.class);
            checkerRealm.setColor(checker.Color().ordinal());
            nextChecker.add(checkerRealm);
        }
        setNext(nextChecker);

        RealmList<CheckerRealm> boardChecker = new RealmList<>();
        Checker[][] board = game.getBoard();
        for (int i=0; i< board.length; i++){
            for(int j = 0; j < board.length; j++){
                Checker checker = board[i][j];
                CheckerRealm checkerRealm = realm.createObject(CheckerRealm.class);
                if(checker != null){
                    MPoint point = checker.getPosition();
                    checkerRealm.setX(point.getX());
                    checkerRealm.setY(point.getY());
                    checkerRealm.setColor(checker.Color().ordinal());
                }
                else{
                    checkerRealm.setX(-1);
                    checkerRealm.setY(-1);
                    checkerRealm.setColor(-1);
                }
                boardChecker.add(checkerRealm);
            }
        }
        setBoard(boardChecker);

        Date current = new Date();
        setId(current.getTime());
        setScore(game.getScore());
        setTime(time);
        setCreatedOn(current.toString());
    }

    public RealmList<CheckerRealm> getNext() {
        return next;
    }

    public void setNext(RealmList<CheckerRealm> next) {
        this.next = next;
    }

    public RealmList<CheckerRealm> getBoard() {
        return board;
    }

    public void setBoard(RealmList<CheckerRealm> board) {
        this.board = board;
    }

    public LogicWinLine toModel() {
        Checker[] nextChecker = new Checker[next.size()];
        for(int i = 0; i < nextChecker.length; i++){
            CheckerRealm checker = next.get(i);
            nextChecker[i] = new Checker(LogicWinLine.Color.values()[checker.getColor()]);
        }
        Checker[][] boardChecker = new Checker[9][9];
        for (int i=0; i< boardChecker.length; i++){
            for(int j = 0; j < boardChecker.length; j++){
                CheckerRealm checker = board.get(boardChecker.length * i + j);
                if(checker.getColor() >= 0)
                    boardChecker[i][j] = new Checker(new MPoint(checker.getX(), checker.getY()), LogicWinLine.Color.values()[checker.getColor()]);
            }
        }

        WinLine game = new WinLine(getId(), boardChecker, nextChecker, getScore(), getTime());
        return game;
    }
}
