package com.mygdx.game.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Axivals;
import com.mygdx.game.Maps.Board;
import com.mygdx.game.Scenes.Hud;
import com.mygdx.game.Sprites.Hero;
import com.sun.corba.se.spi.orbutil.fsm.State;

public class PlayScreen implements Screen {
    //Vision variables
    private Axivals game;
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;

    //map variables
    private Texture map;

    //Tiled map variables
    private TmxMapLoader mapLoader;
    private TiledMap hexes;
    private HexagonalTiledMapRenderer renderer;

    // Width and Height from Map Properties variables
    MapProperties prop;
    public int mapWidth;
    public int mapHeight;
    public int tilePixelWidth;
    public int tilePixelHeight;
    public int mapPixelWidth;
    public int mapPixelHeight;

    //font variable
    private BitmapFont font;

    //Coordinates variables
    private Vector3 screenCoordinates;
    private Vector2 des;

    //Hero variables
    private Hero player;

    MoveToAction action;

    //Board variables
    public Board board;

    public PlayScreen(Axivals game) {
        this.game = game;
        //create cam used to follow hero through cam world
        gamecam = new OrthographicCamera();

        //create a FitViewport to maintain virtual aspect ratio despite
        gamePort = new FitViewport(Axivals.V_WIDTH , Axivals.V_HEIGHT, gamecam);

        //create our game HUD for scores/timers/level info
        hud = new Hud(game.batch);

        //create map
        map = new Texture("map-imgs/no-grid-map.png");

        //Load our map and setup our map renderer
        mapLoader = new TmxMapLoader();
        hexes = mapLoader.load("tiled-maps/map.tmx");
        renderer = new HexagonalTiledMapRenderer(hexes);

        // Get Width and Height from Map Properties
        prop = hexes.getProperties();
        mapWidth = prop.get("width", Integer.class);
        mapHeight = prop.get("height", Integer.class);
        tilePixelWidth = prop.get("tilewidth", Integer.class);
        tilePixelHeight = prop.get("tileheight", Integer.class);
        mapPixelWidth = mapWidth * tilePixelWidth;
        mapPixelHeight = mapHeight * tilePixelHeight;

        //create board
        board = new Board(this);

        //create coordinate
        screenCoordinates = new Vector3();
        des = new Vector2(0, 0);

        //create and set font
        font = new BitmapFont();
        font.setColor(255, 255, 255, 1);

        //create hero and set spritesheet
        player = new Hero(this, 0, 0);
        player.setAtlas("hero-imgs/spritesheets/myspritesheet.atlas");
        player.setImg("hero-imgs/spritesheets/myspritesheet.png");
        player.setCoordinates(board.map[0][0].corX , board.map[0][0].corY); // x+w , y+0.75h

        //initially set our gamcam to be centered correctly at the start of map
        gamecam.position.set(mapPixelWidth / 2 + 12 , mapPixelHeight / 2 - 77, 0);
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt) {
        //controll camera
//        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
//            gamecam.position.x += 100 * dt;
//        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
//            gamecam.position.x -= 100 * dt;
//        if (Gdx.input.isKeyPressed(Input.Keys.UP))
//            gamecam.position.y += 100 * dt;
//        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
//            gamecam.position.y -= 100 * dt;

        //Right-move control
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.getWalking() == 0) {
            if (player.facing.compareTo(Hero.State.LEFT) == 0) {
                player.setDes(board.map[player.row][player.col+1].corX, board.map[player.row][player.col].corX);
            }
            else {
                player.setDes(board.map[player.row][player.col+1].corX, board.map[player.row][player.col].corY);
            }

            player.setCurrentState(Hero.State.WALKING);
            player.setWalking(1);
        }
        else if (player.getWalking() == 1) {
            if (player.getCoordinates().x < player.getDes().x) {
                player.setFacing(Hero.State.RIGHT);
                player.setCurrentState(Hero.State.WALKING);
                player.setCoordinates(player.getCoordinates().x += 48 * Math.sqrt(3) * dt, player.getCoordinates().y);
            }
            else {
                player.setRowCol(player.row, player.col+1);
                player.setWalking(0);
                player.setCurrentState(Hero.State.STANDING);
            }
        }

        //Left-move control
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.getWalking() == 0) {
            if (player.facing.compareTo(Hero.State.LEFT) == 0) {
                player.setDes(board.map[player.row][Math.max(0, player.col-1)].corX, player.getCoordinates().y); //corX + 10
            }
            else {
                player.setDes(board.map[player.row][Math.max(0, player.col-1)].corX, player.getCoordinates().y); //corX - 10
            }
            player.setCurrentState(Hero.State.WALKING);
            player.setWalking(2);
        }
        else if (player.getWalking() == 2) {
            if (player.getCoordinates().x > player.getDes().x) {
                player.setFacing(Hero.State.LEFT);
                player.setCurrentState(Hero.State.WALKING);
                player.setCoordinates(player.getCoordinates().x -= 48 * Math.sqrt(3) * dt, player.getCoordinates().y);

            }
            else {
                player.setRowCol(player.row, Math.max(0, player.col-1));
                player.setWalking(0);
                player.setCurrentState(Hero.State.STANDING);
            }
        }

        //Up-Right combination move


        //Up-Left combination move
        //Down-Right combination move
        //Down-Left combination move

        if (player.currentState.compareTo(Hero.State.STANDING) == 0) {
            player.setCurrentState(Hero.State.STANDING);
        }
    }

    public void update(float dt) {
        //handle user input first
        handleInput(dt);

        //update player
        player.update(dt);

        //update our gamecam with correct coordinates after changes
        gamecam.update();

        //tell our renderer to draw only what our camera can see in our game world
        renderer.setView(gamecam);
    }

    @Override
    public void render(float delta) {
        //separate our update logic from render
        update(delta);

        //Clear the game screen with Black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // get coordinates
        screenCoordinates.set(Gdx.input.getX(), Gdx.input.getY(), 0);

        game.batch.begin();

        //render map
        game.batch.draw(map, 0, 0, Axivals.V_WIDTH, Axivals.V_HEIGHT );

        //render screen coordinates
        font.draw(game.batch, "Screen Coordinates", 155, 660);
        font.draw(game.batch, (int) screenCoordinates.x + " , " + (int) screenCoordinates.y, 190, 635);

        //render hero coordinates
        font.draw(game.batch, "Hero Coordinates", 400, 660);
        font.draw(game.batch, (int) player.getCoordinates().x + " , " + (int) player.getCoordinates().y, 435, 635);

        //render destination coordinates
        font.draw(game.batch, "Destination Coordinates", 600, 660);
        font.draw(game.batch, (int) player.getDes().x + " , " + (int) player.getDes().y, 635, 635);

        //render walk coordinates
        font.draw(game.batch, "Status", 800, 660);
        font.draw(game.batch, Integer.toString(player.getWalking()), 820, 635);

        //row-col coordinates
        font.draw(game.batch, "Row-Column", 1000, 660);
        font.draw(game.batch, player.row + " , " + player.col, 1020, 635);

        //animation
//        game.batch.draw(player.action().getKeyFrame(player.getElapsedTime(), true),
//                player.getCoordinates().x, player.getCoordinates().y);

        game.batch.end();

        //render our game map
        renderer.render();

        //Set our batch to now draw what the Hud camera sees.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        //render hero

        game.batch.begin();
        if (player.facing.compareTo(Hero.State.RIGHT) == 0) {
            game.batch.draw(player.action().getKeyFrame(player.getElapsedTime(), true),
                    player.getCoordinates().x + (player.action().getKeyFrame(player.getElapsedTime(), true).getRegionWidth())
                    , player.getCoordinates().y,
                    -(player.action().getKeyFrame(player.getElapsedTime(), true).getRegionWidth()),
                    player.action().getKeyFrame(player.getElapsedTime(), true).getRegionHeight());
        }
        else {
            game.batch.draw(player.action().getKeyFrame(player.getElapsedTime(), true),
                    player.getCoordinates().x - 10, player.getCoordinates().y);
        }

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
