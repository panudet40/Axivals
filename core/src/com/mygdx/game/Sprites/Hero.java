package com.mygdx.game.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Screens.PlayScreen;

public class Hero extends TextureAtlas {

    public enum State {STANDING, WALKING, ATTACKING1, ATTACKING2, ALERT, HIT, DEAD, LEFT, RIGHT};
    public String action;
    public State facing;
    public State currentState;
    public State previousState;
    private Texture img;
    private Animation<TextureRegion> animation;
    private Vector2 coordinates;
    private float coX;
    private float coY;
    private TextureAtlas atlas;
    private float frameDuration;
    private float elapsedTime = 1f;

    public Hero(PlayScreen screen) {
        coordinates = new Vector2();
        facing = State.RIGHT;
        currentState = State.STANDING;
        previousState = State.STANDING;
    }

    public void update(float dt) {
        this.elapsedTime += dt;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }

    public void setImg(String path) {
        this.img = new Texture(path);
    }

    public void setCoordinates(float x, float y) {
        this.coordinates = coordinates.set(x, y);
        this.coX = x;
        this.coY = y;
    }

    public Vector2 getCoordinates() {
        return this.coordinates;
    }

    public void setAtlas(String path) {
        this.atlas = new TextureAtlas(path);
    }

    public void setElapsedTime(float elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public void setX(float x) {
        this.coX = x;
        this.coordinates = coordinates.set(x, coordinates.y);
    }

    public void setY(float y) {
        this.coY = y;
        this.coordinates = coordinates.set( coordinates.x, y);
    }

    public Animation<TextureRegion> action() {
        action = "stand";
        float frameDuration=0.3f;
        if (this.currentState.compareTo(State.WALKING) == 0) {
            action = "walk"; }
        if (this.currentState.compareTo(State.ATTACKING1) == 0) {
            action = "swingO3";
            frameDuration = 0.2f;
        }
        if (this.currentState.compareTo(State.ATTACKING2) == 0) {
            action = "swingOF";
            frameDuration = 0.15f;
        }
        if (this.currentState.compareTo(State.ALERT) == 0) {
            action = "alert";
        }
        if (this.currentState.compareTo(State.HIT) == 0) {
            action = "hit";
        }
        if (this.currentState.compareTo(State.DEAD) == 0) {
            action = "dead";
        }
        return new Animation<TextureRegion>(frameDuration, this.atlas.findRegions(action));

    }

    public void setFacing(State facing) {
        this.facing = facing;
    }

    public void setCurrentState(State currentState) {
        this.previousState = this.currentState;
        this.currentState = currentState;
    }

}
