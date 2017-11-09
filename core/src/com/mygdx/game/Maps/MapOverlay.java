package com.mygdx.game.Maps;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.LinkedList;
import java.util.List;

public class MapOverlay {
    private Board board;
    private SpriteBatch batch;
    private Texture tile;
    private List<Vector2> area;
    private Vector2 temp;
    public MapOverlay(Board board, SpriteBatch batch) {
        this.board = board;
        this.batch = batch;
        this.tile = new Texture("map-imgs/hexagon.png");
    }

    public void showOverlay(int col, int row, int radius) {
        area = new LinkedList<Vector2>();
        area.addAll(board.getOverlay(col, row, radius));
//        area.addAll(board.getArea(col, row, radius));
        for (Vector2 vec: area) {
            if (!board.map[(int)vec.y][(int)vec.x].isObstacle()) {
                batch.draw(tile, board.map[(int)vec.y][(int)vec.x].corX + 8,
                        board.map[(int)vec.y][(int)vec.x].corY - 4,
                        tile.getWidth() * 0.9f, tile.getHeight() * 0.9f);
            }
        }
    }
}
