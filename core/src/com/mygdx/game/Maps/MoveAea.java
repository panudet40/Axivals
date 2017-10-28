package com.mygdx.game.Maps;

import com.badlogic.gdx.math.Vector2;

import java.lang.reflect.Array;

public class MoveAea {
    private Vector2[] area, ways;

    public Vector2[] getArea(int x, int y, int n) {
        int idx = 0;
        long start;
        long stop;
        for (int i = y-n; i < y-n+2*n+1; i++) {
            if (y%2==1) {
                start = (x-n+Math.round(Math.floor(Math.abs(y-i)/2)));
                stop = start + 2*n-Math.abs(y-i)+1;
            }
            else {
                start = (x-n+Math.round(Math.ceil(Math.abs(y-i)/2)));
                stop = start + 2*n-Math.abs(y-i)+1;
            }
            for (long j = start; j < stop; j++) {
                area[idx].set(i, j);
                idx++;
            }
        }
        return area;
    }

    public Vector2[] getWays(int x, int y) {
        ways[0] = new Vector2(x+1, y);
        ways[1] = new Vector2(x, y+1);
        ways[2] = new Vector2(x-1, y+1);
        ways[3] = new Vector2(x-1, y);
        ways[4] = new Vector2(x, y-1);
        ways[5] = new Vector2(x+1, y-1);
        return  ways;
    }
}
