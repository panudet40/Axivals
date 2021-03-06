package com.mygdx.game.Maps;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Screens.PlayScreen;

import java.util.List;

public class Navigator {
    private int ptr=0, route=0;
    private Vector2 start, temp;
    private Vector2[] path;
    private int routing=0;
    private PlayScreen screen;

    public Navigator(PlayScreen screen) {
        this.screen = screen;
    }

    public void setPath(Vector2 start, List<Vector2> path) {
        this.path = (Vector2[]) path.toArray(new Vector2[path.size()]);
        this.start = start;
        this.routing = 0;
    }

    public void routing() {
        if (!(ptr == path.length)) {
            temp = new Vector2(path[ptr].x-start.x, path[ptr].y-start.y);
            if (start.y%2==0) {
                if (temp.equals(new Vector2(1, 0))) //Right
                    this.route = 1;
                if (temp.equals(new Vector2(1, 1))) //Right-Down
                    this.route = 4;
                if (temp.equals(new Vector2(0, 1))) //Left-Down
                    this.route = 6;
                if (temp.equals(new Vector2(-1, 0))) //Left
                    this.route = 2;
                if (temp.equals(new Vector2(0, -1))) //Left-Up
                    this.route = 5;
                if (temp.equals(new Vector2(1, -1))) //Right-Up
                    this.route = 3;
            }
            else {
                if (temp.equals(new Vector2(1, 0))) //Right
                    this.route = 1;
                if (temp.equals(new Vector2(0, 1))) //Right-Down
                    this.route = 4;
                if (temp.equals(new Vector2(-1, 1))) //Left-Down
                    this.route = 6;
                if (temp.equals(new Vector2(-1, 0))) //Left
                    this.route = 2;
                if (temp.equals(new Vector2(-1, -1))) //Left-Up
                    this.route = 5;
                if (temp.equals(new Vector2(0, -1))) //Right-Up
                    this.route = 3;
            }
            this.start = path[ptr];
            this.ptr++;
        }
        else {
            this.ptr = 0;
            this.route = 0;
            System.out.println("In Turn of player " + (screen.idx+1));
            if (screen.idx  == 3) {
                screen.idx = 0;
            }
            else {
                screen.idx += 1;
            }

        }

    }

    public int getRoute() {
        return this.route;
    }

    public void setRouting(int routing) {
        this.routing = 1;
    }

    public int isRouting() {
        return this.routing;
    }
}
