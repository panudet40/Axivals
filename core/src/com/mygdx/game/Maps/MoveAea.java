package com.mygdx.game.Maps;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.util.List;

public class MoveAea {
    private List<Vector2> area, ways;
    private List<Vector3> ranges;
    private Board board;

    public MoveAea(Board board) {

    }

    public List<Vector2> getArea(int x, int y, int n, Board board) {
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
            ranges.add(idx_r, new Vector3(start, i, stop));
            for (int j = start; j < stop; j++) {
                    if (!board.map[i][j].isObstacle() == true && i >= 0 && j >= 0) {
                        area.add(idx, new Vector2(i, j));
                        idx++;
                    }
            }
        }
        return area;
    }

    public List<Vector2> getWays(int x, int y, Board board) {
        if (y%2 == 0) {
            ways.add(new Vector2(x+1, y));
            ways.add(new Vector2(x+1, y+1));
            ways.add(new Vector2(x, y+1));
            ways.add(new Vector2(x-1, y));
            ways.add(new Vector2(x, y-1));
            ways.add(new Vector2(x+1, y-1));
        }
        else {
            ways.add(new Vector2(x+1, y));
            ways.add(new Vector2(x, y+1));
            ways.add(new Vector2(x-1, y+1));
            ways.add(new Vector2(x-1, y));
            ways.add(new Vector2(x-1, y-1));
            ways.add(new Vector2(x, y-1));
        }
        area = this.getArea(x, y, 1, board);
        ways.retainAll(area);
        for (Vector2 node: ways) {
            board.map[(int)node.y][(int)node.x].setVisit(1);
            board.map[(int)node.y][(int)node.x].setParent(x, y);
        }
        return  ways;
    }

    public List<Vector3> getRanges() {
        return ranges;
    }
}
