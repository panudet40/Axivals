package com.mygdx.game.Maps;

import com.badlogic.gdx.math.Vector2;

public class Node extends Vector2 {
    public int row;
    public int col;
    public int corX;
    public int corY;
    public int parentX;
    public int parentY;
    private int visit=0;
    private int obstacle;
    public Node(int row, int col, int corX, int corY, int obstacle) {
        this.row = row;
        this.col = col;
        this.corX = corX;
        this.corY = corY;
        this.obstacle = obstacle;
    }

    public void setParent(int parentX, int parentY) {
        this.parentX = parentX;
        this.parentY = parentY;
    }

    public void setObstacle(int obstacle) {
        this.obstacle = obstacle;
    }

    public boolean isObstacle() {
        if (obstacle == 1) {
            return true;
        }
        return false;
    }

    public void setVisit(int visit) {
        this.visit = visit;
    }

    public int getVisit() {
        return visit;
    }
}
