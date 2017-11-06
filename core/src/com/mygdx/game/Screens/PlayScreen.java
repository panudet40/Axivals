package com.mygdx.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Axivals;
import com.mygdx.game.Maps.Board;
import com.mygdx.game.Maps.Navigator;
import com.mygdx.game.Maps.onClick;
import com.mygdx.game.Sprites.Hero;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.sqrt;

public class PlayScreen implements Screen, InputProcessor {
    //Vision variables
    private Axivals game;
    private OrthographicCamera gamecam;
    private Viewport gamePort;

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

    //Path for walking
    private List<Vector2> path;

    //Test Clicking
    private onClick click;

    //Navigator variable
    private Navigator walker;

    int ipctrl=0;

    public PlayScreen(Axivals game) {

        this.game = game;
        //create cam used to follow hero through cam world
        gamecam = new OrthographicCamera();

        //create a FitViewport to maintain virtual aspect ratio despite
        gamePort = new FitViewport(Axivals.V_WIDTH , Axivals.V_HEIGHT, gamecam);

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

        //create onClick
        click = new onClick(this, board);

        //create navigator
        walker = new Navigator();

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

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt) {
//        //controll camera
//        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
//            gamecam.position.x += 100 * dt;
//        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
//            gamecam.position.x -= 100 * dt;
//        if (Gdx.input.isKeyPressed(Input.Keys.UP))
//            gamecam.position.y += 100 * dt;
//        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
//            gamecam.position.y -= 100 * dt;
        if (Gdx.input.isKeyPressed(Input.Keys.X) && ipctrl == 0) {
            ipctrl = 1;
            path = new LinkedList<Vector2>();
            float x = Gdx.input.getX();
            float y =  Math.abs(mapPixelHeight-Gdx.input.getY());
            Vector2 goal = click.getRowCol(x, y);
            path.addAll(board.getPath(player.getRowCol(), goal));
            System.out.println("GET PATH!! Path size = " + path.size());
            for (Vector2 v : path) {
                if (!(path.indexOf(v) == path.size() - 1)) {
                    System.out.print((int) v.x + "," + (int) v.y + " -> ");
                } else {
                    System.out.print((int) v.x + "," + (int) v.y);
                }
            }
//            ipctrl = 0;
        }

//        Right-move control
        if (walker.getRoute() == 1 && player.getWalking() == 0) {
            player.setWalking(1);
            player.setCurrentState(Hero.State.WALKING);
            player.setDes(board.map[player.row][player.col+1].corX,
                    board.map[player.row][player.col+1].corY);
        }
        else if (player.getWalking() == 1 && walker.getRoute() == 1) {
            if (player.getCoordinates().x < player.getDes().x) {
                player.setFacing(Hero.State.RIGHT);
                player.setCurrentState(Hero.State.WALKING);
                player.setCoordinates(player.getCoordinates().x += tilePixelWidth * dt,
                        player.getCoordinates().y);
            }
            else {
                player.setRowCol(player.row, player.col+1);
                player.setWalking(0);
                player.setCurrentState(Hero.State.STANDING);
                walker.routing();
            }
        }

        //Left-move control
        if (walker.getRoute() == 2 && player.getWalking() == 0) {
            player.setWalking(2);
            player.setCurrentState(Hero.State.WALKING);
            player.setDes(board.map[player.row][Math.max(0, player.col-1)].corX,
                    board.map[player.row][Math.max(0, player.col-1)].corY);
        }
        else if (player.getWalking() == 2 && walker.getRoute() == 2) {
            if (player.getCoordinates().x > player.getDes().x) {
                player.setFacing(Hero.State.LEFT);
                player.setCurrentState(Hero.State.WALKING);
                player.setCoordinates(player.getCoordinates().x -= tilePixelWidth * dt,
                        player.getCoordinates().y);
            }
            else {
                player.setRowCol(player.row, Math.max(0, player.col-1));
                player.setWalking(0);
                player.setCurrentState(Hero.State.STANDING);
                walker.routing();
            }
        }

        //Up-Right combination move
        if (walker.getRoute() == 3 && player.getWalking() == 0) {
            player.setWalking(3);
            player.setCurrentState(Hero.State.WALKING);
            if (player.row%2 == 0) {
                player.setDes(board.map[Math.max(0, player.row-1)][player.col+1].corX,
                        board.map[Math.max(0, player.row-1)][player.col+1].corY);
            }
            else {
                player.setDes(board.map[Math.max(0, player.row-1)][player.col].corX,
                        board.map[Math.max(0, player.row-1)][player.col].corY);
            }
        }
        else if (player.getWalking() == 3 && walker.getRoute() == 3) {
            if (player.getCoordinates().x < player.getDes().x || player.getCoordinates().y < player.getDes().y) {
                player.setFacing(Hero.State.RIGHT);
                player.setCurrentState(Hero.State.WALKING);
                if (player.getCoordinates().x < player.getDes().x) {
                    player.setCoordinates(player.getCoordinates().x += (1/sqrt(3)) * tilePixelWidth * dt,
                            player.getCoordinates().y);
                }
                if (player.getCoordinates().y < player.getDes().y) {
                    player.setCoordinates(player.getCoordinates().x,
                            player.getCoordinates().y += (sqrt(3)/2) * tilePixelHeight * dt);
                }
            }
            else if (player.getCoordinates().x >= player.getDes().x && player.getCoordinates().y >= player.getDes().y){
                if (player.row%2 == 0) {
                    player.setRowCol(Math.max(0, player.row-1), player.col+1);
                }
                else {
                    player.setRowCol(Math.max(0, player.row-1), player.col);
                }
                player.setWalking(0);
                player.setCurrentState(Hero.State.STANDING);
                walker.routing();
            }
        }

        //Down-Right combination move
        if (walker.getRoute() == 4 && player.getWalking() == 0) {
            player.setWalking(4);
            player.setCurrentState(Hero.State.WALKING);
            if (player.row%2 == 0) {
                player.setDes(board.map[player.row+1][player.col+1].corX, board.map[player.row+1][player.col+1].corY);
            }
            else {
                player.setDes(board.map[player.row+1][player.col].corX, board.map[player.row+1][player.col].corY);
            }
        }
        else if (player.getWalking() == 4 && walker.getRoute() == 4) {
            if (player.getCoordinates().x < player.getDes().x || player.getCoordinates().y > player.getDes().y) {
                player.setFacing(Hero.State.RIGHT);
                player.setCurrentState(Hero.State.WALKING);
                if (player.getCoordinates().x < player.getDes().x) {
                    player.setCoordinates(player.getCoordinates().x += (1/sqrt(3)) * tilePixelWidth * dt,
                            player.getCoordinates().y);
                }
                if (player.getCoordinates().y > player.getDes().y) {
                    player.setCoordinates(player.getCoordinates().x,
                            player.getCoordinates().y -= (sqrt(3)/2) * tilePixelHeight * dt);
                }
            }
            else if (player.getCoordinates().x >= player.getDes().x && player.getCoordinates().y <= player.getDes().y){
                if (player.row%2 == 0) {
                    player.setRowCol(player.row+1, player.col+1);
                }
                else {
                    player.setRowCol( player.row+1, player.col);
                }
                player.setWalking(0);
                player.setCurrentState(Hero.State.STANDING);
                walker.routing();
            }
        }

        //Up-Left combination move
        if (walker.getRoute() == 5 && player.getWalking() == 0) {
            player.setWalking(5);
            player.setCurrentState(Hero.State.WALKING);
            if (player.row%2 == 0) {
                player.setDes(board.map[player.row-1][player.col].corX, board.map[player.row-1][player.col].corY);
            }
            else {
                player.setDes(board.map[player.row-1][player.col-1].corX, board.map[player.row-1][player.col-1].corY);
            }
        }
        else if (player.getWalking() == 5 && walker.getRoute() == 5) {
            if (player.getCoordinates().x > player.getDes().x || player.getCoordinates().y < player.getDes().y) {
                player.setFacing(Hero.State.LEFT);
                player.setCurrentState(Hero.State.WALKING);
                if (player.getCoordinates().x > player.getDes().x) {
                    player.setCoordinates(player.getCoordinates().x -= (1/sqrt(3)) * tilePixelWidth * dt,
                            player.getCoordinates().y);
                }
                if (player.getCoordinates().y < player.getDes().y) {
                    player.setCoordinates(player.getCoordinates().x,
                            player.getCoordinates().y += (sqrt(3)/2) * tilePixelHeight * dt);
                }
            }
            else if (player.getCoordinates().x <= player.getDes().x && player.getCoordinates().y >= player.getDes().y){
                if (player.row%2 == 0) {
                    player.setRowCol(player.row-1, player.col);
                }
                else {
                    player.setRowCol( player.row-1, player.col-1);
                }
                player.setWalking(0);
                player.setCurrentState(Hero.State.STANDING);
                walker.routing();
            }
        }

        //Down-Left combination move
        if (walker.getRoute() == 6 && player.getWalking() == 0) {
            player.setWalking(6);
            player.setCurrentState(Hero.State.WALKING);
            if (player.row%2 == 0) {
                player.setDes(board.map[player.row+1][player.col].corX, board.map[player.row+1][player.col].corY);
            }
            else {
                player.setDes(board.map[player.row+1][player.col-1].corX, board.map[player.row+1][player.col-1].corY);
            }
        }
        else if (player.getWalking() == 6 && walker.getRoute() == 6) {
            if (player.getCoordinates().x > player.getDes().x || player.getCoordinates().y > player.getDes().y) {
                player.setFacing(Hero.State.LEFT);
                player.setCurrentState(Hero.State.WALKING);
                if (player.getCoordinates().x > player.getDes().x) {
                    player.setCoordinates(player.getCoordinates().x -= (1/sqrt(3)) * tilePixelWidth * dt,
                            player.getCoordinates().y);
                }
                if (player.getCoordinates().y > player.getDes().y) {
                    player.setCoordinates(player.getCoordinates().x,
                            player.getCoordinates().y -= (sqrt(3)/2) * tilePixelHeight * dt);
                }
            } else if (player.getCoordinates().x <= player.getDes().x && player.getCoordinates().y <= player.getDes().y) {
                if (player.row % 2 == 0) {
                    player.setRowCol(player.row + 1, player.col);
                } else {
                    player.setRowCol(player.row + 1, player.col - 1);
                }
                player.setWalking(0);
                player.setCurrentState(Hero.State.STANDING);
                walker.routing();
            }
            // No input go to STANDING state
//        if (player.currentState.compareTo(Hero.State.STANDING) == 0) {
//            player.setCurrentState(Hero.State.STANDING);
//        }
//        int check = 0;
//        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
//            player.setCoordinates(player.getCoordinates().x,
//                            player.getCoordinates().y += 40 * dt);
//                    check = 1;
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
//            player.setCoordinates(player.getCoordinates().x,
//                            player.getCoordinates().y -= 40 * dt);
//                    check = 1;
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
//            player.setCoordinates(player.getCoordinates().x -= 40 * dt,
//                            player.getCoordinates().y);
//                    check = 1;
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
//            player.setCoordinates(player.getCoordinates().x += 40 * dt,
//                            player.getCoordinates().y);
//                    check = 1;
//        }
//        if (check == 0) {
//            player.setCurrentState(Hero.State.STANDING);
//        }
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
        font.draw(game.batch, (int) screenCoordinates.x + " , "
                + (int) Math.abs(mapPixelHeight-screenCoordinates.y), 190, 635);

        //render hero coordinates
        font.draw(game.batch, "Hero Coordinates (changed)", 400, 660);
        font.draw(game.batch, (int) player.getCoordinates().x + " , " + (int) player.getCoordinates().y, 435, 635);

        //render destination coordinates
        font.draw(game.batch, "Destination Coordinates", 600, 660);
        font.draw(game.batch, (int) player.getDes().x + " , " + (int) player.getDes().y, 635, 635);

        //render walk coordinates
        font.draw(game.batch, "Status", 800, 660);
        font.draw(game.batch, Integer.toString(player.getWalking()), 820, 635);

        //row-col coordinates
        font.draw(game.batch, "Row-Column", 900, 660);
        font.draw(game.batch, player.row + " , " + player.col, 920, 635);

        //render walk coordinates
        font.draw(game.batch, "Routing", 1000, 660);
        font.draw(game.batch, Integer.toString(player.getWalking()), 1000, 635);

        game.batch.end();

        //render our game map
        renderer.render();

        //render hero

        game.batch.begin();
        if (player.facing.compareTo(Hero.State.RIGHT) == 0) {
            game.batch.draw(player.action().getKeyFrame(player.getElapsedTime(), true),
                    player.getCoordinates().x + (player.action().getKeyFrame(player.getElapsedTime(),
                            true).getRegionWidth()), player.getCoordinates().y,
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

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector2 rowcol = click.getRowCol(screenX, Math.abs(mapPixelHeight - screenY));
        if (!board.map[(int)rowcol.y][(int)rowcol.x].isObstacle()) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && walker.isRouting() == 0) {
                walker.setRouting(1);
                System.out.println("Mouse clicked!");
                float x = Gdx.input.getX();
                float y =  Math.abs(mapPixelHeight-Gdx.input.getY());
                path = new LinkedList<Vector2>();
                Vector2 goal = click.getRowCol(x, y);
                System.out.println("Column-Row = " + goal.x + "," + goal.y);
                path.addAll(board.getPath(player.getRowCol(), goal));
                walker.setPath(player.getRowCol(), path);
                walker.routing();
        }
            //Debugging
//            for (Vector2 v : path) {
//                if (!(path.indexOf(v) == path.size() - 1)) {
//                    System.out.print((int) v.x + "," + (int) v.y + " -> ");
//                } else {
//                    System.out.println((int) v.x + "," + (int) v.y);
//                }
//            }
//            Vector2 start = new Vector2(player.getRowCol());
//            int num=1;
//            for (Vector2 v: path) {
//                Vector2 temp = new Vector2(v.x-start.x, v.y-start.y);
//                if (start.y%2==0) {
//                    if (temp.equals(new Vector2(1, 0))) //Right
//                        System.out.println(num+"//Right");
//                    if (temp.equals(new Vector2(1, 1))) //Right-Down
//                        System.out.println(num+"//Right-Down");
//                    if (temp.equals(new Vector2(0, 1))) //Left-Down
//                        System.out.println(num+"//Left-Down");
//                    if (temp.equals(new Vector2(-1, 0))) //Left
//                        System.out.println(num+" //Left");
//                    if (temp.equals(new Vector2(0, -1))) //Left-Up
//                        System.out.println(num+" //Left-Up");
//                    if (temp.equals(new Vector2(1, -1))) //Right-Up
//                        System.out.println(num+" //Right-Up");
//                }
//                else {
//                    if (temp.equals(new Vector2(1, 0))) //Right
//                        System.out.println(num+"//Right");
//                    if (temp.equals(new Vector2(0, 1))) //Right-Down
//                        System.out.println(num+"//Right-Down");
//                    if (temp.equals(new Vector2(-1, 1))) //Left-Down
//                        System.out.println(num+" //Left-Down");
//                    if (temp.equals(new Vector2(-1, 0))) //Left
//                        System.out.println(num+" //Left");
//                    if (temp.equals(new Vector2(-1, -1))) //Left-Up
//                        System.out.println(num+") //Left-Up");
//                    if (temp.equals(new Vector2(0, -1))) //Right-Up
//                        System.out.println(num+" //Right-Up");
//                }
//                num++;
//                start = new Vector2(v);
//            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
