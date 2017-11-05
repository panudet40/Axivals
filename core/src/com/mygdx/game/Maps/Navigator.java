package com.mygdx.game.Maps;

import com.badlogic.gdx.math.Vector2;

import java.util.LinkedList;
import java.util.List;

public class Navigator {
    private int ptr=0;
    private Vector2 start, temp;
    private Vector2[] path;

    public Navigator(Vector2 start, List<Vector2> path) {
        this.path = (Vector2[]) path.toArray(new Vector2[path.size()]);
        this.start = start;
    }

    public int getRoute() {
        temp = new Vector2(path[ptr].x-start.x, path[ptr].y-start.y);
        if (path[ptr].y%2==0) {
            if (temp.equals(new Vector2(1, 0))) //Right
                return 1;
            if (temp.equals(new Vector2(1, 1))) //Right-Down
                return 4;
            if (temp.equals(new Vector2(0, 1))) //Left-Down
                return 6;
            if (temp.equals(new Vector2(-1, 0))) //Left
                return 2;
            if (temp.equals(new Vector2(0, -1))) //Left-Up
                return 5;
            if (temp.equals(new Vector2(1, -1))) //Right-Up
                return 3;
        }
        else {
            if (temp.equals(new Vector2(1, 0))) //Right
                return 1;
            if (temp.equals(new Vector2(0, 1))) //Right-Down
                return 4;
            if (temp.equals(new Vector2(-1, 1))) //Left-Down
                return 6;
            if (temp.equals(new Vector2(-1, 0))) //Left
                return 2;
            if (temp.equals(new Vector2(-1, -1))) //Left-Up
                return 5;
            if (temp.equals(new Vector2(0, -1))) //Right-Up
                return 3;
        }
        return 0;
    }

    public void increase() {
        this.start = path[ptr];
        this.ptr++;
    }

    public void reset() {
        this.ptr=0;
    }
}
