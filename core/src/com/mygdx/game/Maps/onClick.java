package com.mygdx.game.Maps;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Screens.PlayScreen;

public class onClick {
    private Board board;
    private PlayScreen screen;
    private float tileW;
    private float tileH;
    private int rowY;
    private int colX;

    public onClick(PlayScreen screen, Board board) {
        this.screen = screen;
        this.board = board;
        this.tileW = screen.tilePixelWidth;
        this.tileH = screen.tilePixelHeight;
    }

    public Vector2 getRowCol(float x, float y) {
        float a, b;
        boolean topleft = false, topright = false, downleft = false, downright = false, rangeX = false;
        for (int row = 0; row <= 12; row++) {
            for (int col = 0; col <= 23; col++) {
                a = board.map[row][col].corX + 27;
                b = board.map[row][col].corY - 30;
                topleft = y - (b + tileH / 2) <= (tileH / (2 * tileW)) * (x - a);
                downright = y - (b - tileH / 2) >= (tileH / (2 * tileW)) * (x - a);
                downleft = y - (b + tileH / 2) <= (-tileH / (2 * tileW)) * (x - a);
                topright = y - (b - tileH / 2) >= (-tileH / (2 * tileW)) * (x - a);
                rangeX = (a - (tileW / 2)) <= x && x <= (a + (tileW / 2));
                if (topleft && topright && downleft && downright && rangeX) {
                    colX = col;
                    rowY = row;
                    break;
                }
            }
        }
        return new Vector2(colX, rowY);
    }
}
