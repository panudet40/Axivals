package com.mygdx.game.Maps;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class MoveAea {
    private Vector2[] area, ways;
    private Vector3[] ranges;
    private Board board;

    public MoveAea(Board board) {

    }

    public Vector2[] getArea(int x, int y, int n, Board board) {
        int idx = 0;
        int idx_r = 0;
        int start;
        int stop;
        for (int i = y-n; i < y-n+2*n+1; i++) {
            if (y%2==1) {
                start = (x - n + (int)Math.floor(Math.abs(y-i)/2));
                stop = start + 2*n - Math.abs(y-i)+1;
            }
            else {
                start = (x - n + (int)Math.ceil(Math.abs(y-i)/2));
                stop = start + 2*n-Math.abs(y-i)+1;
            }
            ranges[idx_r].set(start, i, stop);
            for (int j = start; j < stop; j++) {
                    if (!board.map[i][j].isObstacle() == true && i >= 0 && j >= 0) {
                        area[idx].set(i, j);
                        idx++;
                    }
            }
        }
        return area;
    }

    public Vector2[] getWays(int x, int y) {
        if (y%2 == 0) {
            ways[0] = new Vector2(x+1, y);
            ways[1] = new Vector2(x+1, y+1);
            ways[2] = new Vector2(x, y+1);
            ways[3] = new Vector2(x-1, y);
            ways[4] = new Vector2(x, y-1);
            ways[5] = new Vector2(x+1, y-1);
        }
        else {
            ways[0] = new Vector2(x+1, y);
            ways[1] = new Vector2(x, y+1);
            ways[2] = new Vector2(x-1, y+1);
            ways[3] = new Vector2(x-1, y);
            ways[4] = new Vector2(x-1, y-1);
            ways[5] = new Vector2(x, y-1);
        }
        return  ways;
    }

    public Vector3[] getRanges() {
        return ranges;
    }
}
