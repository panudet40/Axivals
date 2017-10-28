package com.mygdx.game.Maps;

import com.badlogic.gdx.math.Vector2;

public class Node extends Vector2 {
    public int x;
    public int y;
    public int parentX;
    public int parentY;
    private int visit;
    private int obstacle;
    public Node(int x, int y, int obstacle) {
        this.x = x;
        this.y = y;
        this.obstacle = obstacle;
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
}
