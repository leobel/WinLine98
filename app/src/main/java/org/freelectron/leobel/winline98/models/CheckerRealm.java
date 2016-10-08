package org.freelectron.leobel.winline98.models;

import io.realm.RealmModel;
import io.realm.RealmObject;

/**
 * Created by leobel on 10/7/16.
 */
public class CheckerRealm extends RealmObject {
    private int x;
    private int y;
    private int color;

    public CheckerRealm(){
    }

    public CheckerRealm(int color){
        this.color = color;
    }

    public CheckerRealm(int x, int y, int color){
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
