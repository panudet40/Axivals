package com.mygdx.game.Maps;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedList;

public class Board {
    //0 is no obstacle 1 is a obstacle 2 is a Hero
    private final static int[][] detail =  {
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0},
            {0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };

    public Node[][] map;
    private LinkedList<Vector2> list;
    private Vector2[] path, area;
    private  Vector3[] ranges;
    private MoveAea areaChecker;

    public Board() {
        for (int y=0; y < 13; y++) {
            for (int x=0; x < 24; x++) {
                map[y][x] = new Node(x, y, detail[y][x]);
            }
        }
    }

    public Vector2[] getPath(int scrX, int scrY, int desX, int desY, int walk) {
        Vector2 temp;
        list = new LinkedList();
        area = areaChecker.getArea(scrX, scrY, walk, this);
        ranges = areaChecker.getRanges();
            list.add(new Vector2(scrX, scrY));
        do {
            temp = list.pop();
            list.addAll(Arrays.asList(areaChecker.getWays((int)temp.x, (int)temp.y)));
        }while (!temp.equals(new Vector2(desX, desY)));

        return path;
    }

    public void setObstacle(int x, int y, int n) {
        if (0 <= x && x <= 23 && 0 <= y && y <= 12 && !this.map[x][y].isObstacle()) {
            this.map[x][y].setObstacle(n);
        }
    }
}
