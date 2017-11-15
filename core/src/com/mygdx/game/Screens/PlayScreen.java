package com.mygdx.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Axivals;
import com.mygdx.game.Maps.Board;
import com.mygdx.game.Maps.MapOverlay;
import com.mygdx.game.Maps.Navigator;
import com.mygdx.game.Maps.onClick;
import com.mygdx.game.Sprites.Hero;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.lang.Math.sqrt;
import static java.lang.Math.subtractExact;

public class PlayScreen implements Screen, InputProcessor {
    //status phase variable
    private int[] statusPhase;

    //order variable
    int order;

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
    private MapOverlay overlay;

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
    public Hero[] player;
    public int idx=0;

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
        //set game
        this.game = game;

        //create cam used to follow hero through cam world
        gamecam = new OrthographicCamera();

        //create a FitViewport to maintain virtual aspect ratio despite
        gamePort = new FitViewport(Axivals.V_WIDTH , Axivals.V_HEIGHT, gamecam);

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

        //create map
        map = new Texture("map-imgs/no-grid-map.png");
        overlay = new MapOverlay(board, game.batch);

        //create onClick
        click = new onClick(this, board);

        //create navigator
        walker = new Navigator(this);

        //create coordinate
        screenCoordinates = new Vector3();
        des = new Vector2(0, 0);

        //create and set font
        font = new BitmapFont();
        font.setColor(255, 255, 255, 1);

        //create phase status
        statusPhase = new int[9];
        statusPhase[1] = 4;
        statusPhase[2] = 2;
        statusPhase[3] = 2;
        statusPhase[4] = 1;

        //create hero and set spritesheet
        player = new Hero[4];
        settingHero();

        Gdx.input.setInputProcessor(this);
    }

    public void settingHero() {
        int job;
        for (int i=1; i<5; i++) {
            if (statusPhase[i] > 3) {
                order = statusPhase[i]-3;
                job = order;
            }
            else {
                job = statusPhase[i];
            }
            if (job == 1) {
                player[i-1] = new Hero(this, board, board.getHeroCoordinates());
                player[i-1].setAtlas("hero-imgs/DarkTemplarSpritesheet/DarkTemplarSpritesheet.atlas");
                player[i-1].setImg("hero-imgs/DarkTemplarSpritesheet/DarkTemplarSpritesheet.png");
            }
            else if (job == 2) {
                player[i-1] = new Hero(this, board, board.getHeroCoordinates());
                player[i-1].setAtlas("hero-imgs/spritesheets/myspritesheet.atlas");
                player[i-1].setImg("hero-imgs/spritesheets/myspritesheet.png");
            }
            else {
                player[i-1] = new Hero(this, board, board.getHeroCoordinates());
                player[i-1].setAtlas("hero-imgs/spritesheets/myspritesheet.atlas");
                player[i-1].setImg("hero-imgs/spritesheets/myspritesheet.png");
            }
            if (i%2==0) {
                player[i-1].setFacing(Hero.State.LEFT);
            }
            board.map[player[i-1].row][player[i-1].col].setObstacle(2);
        }
    }

    @Override
    public void show() {

    }

    public void simulatedInput(float dt) {
        //Right-move control
        if (walker.getRoute() == 1 && player[idx].getWalking() == 0) {
            player[idx].setWalking(1);
            player[idx].setCurrentState(Hero.State.WALKING);
            player[idx].setDes(board.map[player[idx].row][player[idx].col+1].corX,
                    board.map[player[idx].row][player[idx].col+1].corY);
        }
        else if (player[idx].getWalking() == 1 && walker.getRoute() == 1) {
            if (player[idx].getCoordinates().x < player[idx].getDes().x) {
                player[idx].setFacing(Hero.State.RIGHT);
                player[idx].setCurrentState(Hero.State.WALKING);
                player[idx].setCoordinates(player[idx].getCoordinates().x += tilePixelWidth * dt,
                        player[idx].getCoordinates().y);
            }
            else {
                player[idx].setRowCol(player[idx].row, player[idx].col+1);
                player[idx].setWalking(0);
                player[idx].setCurrentState(Hero.State.STANDING);
                walker.routing();
            }
        }

        //Left-move control
        if (walker.getRoute() == 2 && player[idx].getWalking() == 0) {
            player[idx].setWalking(2);
            player[idx].setCurrentState(Hero.State.WALKING);
            player[idx].setDes(board.map[player[idx].row][Math.max(0, player[idx].col-1)].corX,
                    board.map[player[idx].row][Math.max(0, player[idx].col-1)].corY);
        }
        else if (player[idx].getWalking() == 2 && walker.getRoute() == 2) {
            if (player[idx].getCoordinates().x > player[idx].getDes().x) {
                player[idx].setFacing(Hero.State.LEFT);
                player[idx].setCurrentState(Hero.State.WALKING);
                player[idx].setCoordinates(player[idx].getCoordinates().x -= tilePixelWidth * dt,
                        player[idx].getCoordinates().y);
            }
            else {
                player[idx].setRowCol(player[idx].row, Math.max(0, player[idx].col-1));
                player[idx].setWalking(0);
                player[idx].setCurrentState(Hero.State.STANDING);
                walker.routing();
            }
        }

        //Up-Right combination move
        if (walker.getRoute() == 3 && player[idx].getWalking() == 0) {
            player[idx].setWalking(3);
            player[idx].setCurrentState(Hero.State.WALKING);
            if (player[idx].row%2 == 0) {
                player[idx].setDes(board.map[Math.max(0, player[idx].row-1)][player[idx].col+1].corX,
                        board.map[Math.max(0, player[idx].row-1)][player[idx].col+1].corY);
            }
            else {
                player[idx].setDes(board.map[Math.max(0, player[idx].row-1)][player[idx].col].corX,
                        board.map[Math.max(0, player[idx].row-1)][player[idx].col].corY);
            }
        }
        else if (player[idx].getWalking() == 3 && walker.getRoute() == 3) {
            if (player[idx].getCoordinates().x < player[idx].getDes().x || player[idx].getCoordinates().y
                    < player[idx].getDes().y) {
                player[idx].setFacing(Hero.State.RIGHT);
                player[idx].setCurrentState(Hero.State.WALKING);
                if (player[idx].getCoordinates().x < player[idx].getDes().x) {
                    player[idx].setCoordinates(player[idx].getCoordinates().x += (1/sqrt(3)) * tilePixelWidth * dt,
                            player[idx].getCoordinates().y);
                }
                if (player[idx].getCoordinates().y < player[idx].getDes().y) {
                    player[idx].setCoordinates(player[idx].getCoordinates().x,
                            player[idx].getCoordinates().y += (sqrt(3)/2) * tilePixelHeight * dt);
                }
            }
            else if (player[idx].getCoordinates().x >= player[idx].getDes().x && player[idx].getCoordinates().y
                    >= player[idx].getDes().y){
                if (player[idx].row%2 == 0) {
                    player[idx].setRowCol(Math.max(0, player[idx].row-1), player[idx].col+1);
                }
                else {
                    player[idx].setRowCol(Math.max(0, player[idx].row-1), player[idx].col);
                }
                player[idx].setWalking(0);
                player[idx].setCurrentState(Hero.State.STANDING);
                walker.routing();
            }
        }

        //Down-Right combination move
        if (walker.getRoute() == 4 && player[idx].getWalking() == 0) {
            player[idx].setWalking(4);
            player[idx].setCurrentState(Hero.State.WALKING);
            if (player[idx].row%2 == 0) {
                player[idx].setDes(board.map[player[idx].row+1][player[idx].col+1].corX,
                        board.map[player[idx].row+1][player[idx].col+1].corY);
            }
            else {
                player[idx].setDes(board.map[player[idx].row+1][player[idx].col].corX,
                        board.map[player[idx].row+1][player[idx].col].corY);
            }
        }
        else if (player[idx].getWalking() == 4 && walker.getRoute() == 4) {
            if (player[idx].getCoordinates().x < player[idx].getDes().x || player[idx].getCoordinates().y
                    > player[idx].getDes().y) {
                player[idx].setFacing(Hero.State.RIGHT);
                player[idx].setCurrentState(Hero.State.WALKING);
                if (player[idx].getCoordinates().x < player[idx].getDes().x) {
                    player[idx].setCoordinates(player[idx].getCoordinates().x += (1/sqrt(3)) * tilePixelWidth * dt,
                            player[idx].getCoordinates().y);
                }
                if (player[idx].getCoordinates().y > player[idx].getDes().y) {
                    player[idx].setCoordinates(player[idx].getCoordinates().x,
                            player[idx].getCoordinates().y -= (sqrt(3)/2) * tilePixelHeight * dt);
                }
            }
            else if (player[idx].getCoordinates().x >= player[idx].getDes().x && player[idx].getCoordinates().y
                    <= player[idx].getDes().y){
                if (player[idx].row%2 == 0) {
                    player[idx].setRowCol(player[idx].row+1, player[idx].col+1);
                }
                else {
                    player[idx].setRowCol( player[idx].row+1, player[idx].col);
                }
                player[idx].setWalking(0);
                player[idx].setCurrentState(Hero.State.STANDING);
                walker.routing();
            }
        }

        //Up-Left combination move
        if (walker.getRoute() == 5 && player[idx].getWalking() == 0) {
            player[idx].setWalking(5);
            player[idx].setCurrentState(Hero.State.WALKING);
            if (player[idx].row%2 == 0) {
                player[idx].setDes(board.map[player[idx].row-1][player[idx].col].corX,
                        board.map[player[idx].row-1][player[idx].col].corY);
            }
            else {
                player[idx].setDes(board.map[player[idx].row-1][player[idx].col-1].corX,
                        board.map[player[idx].row-1][player[idx].col-1].corY);
            }
        }
        else if (player[idx].getWalking() == 5 && walker.getRoute() == 5) {
            if (player[idx].getCoordinates().x > player[idx].getDes().x || player[idx].getCoordinates().y
                    < player[idx].getDes().y) {
                player[idx].setFacing(Hero.State.LEFT);
                player[idx].setCurrentState(Hero.State.WALKING);
                if (player[idx].getCoordinates().x > player[idx].getDes().x) {
                    player[idx].setCoordinates(player[idx].getCoordinates().x -= (1/sqrt(3)) * tilePixelWidth * dt,
                            player[idx].getCoordinates().y);
                }
                if (player[idx].getCoordinates().y < player[idx].getDes().y) {
                    player[idx].setCoordinates(player[idx].getCoordinates().x,
                            player[idx].getCoordinates().y += (sqrt(3)/2) * tilePixelHeight * dt);
                }
            }
            else if (player[idx].getCoordinates().x <= player[idx].getDes().x && player[idx].getCoordinates().y
                    >= player[idx].getDes().y){
                if (player[idx].row%2 == 0) {
                    player[idx].setRowCol(player[idx].row-1, player[idx].col);
                }
                else {
                    player[idx].setRowCol( player[idx].row-1, player[idx].col-1);
                }
                player[idx].setWalking(0);
                player[idx].setCurrentState(Hero.State.STANDING);
                walker.routing();
            }
        }

        //Down-Left combination move
        if (walker.getRoute() == 6 && player[idx].getWalking() == 0) {
            player[idx].setWalking(6);
            player[idx].setCurrentState(Hero.State.WALKING);
            if (player[idx].row%2 == 0) {
                player[idx].setDes(board.map[player[idx].row+1][player[idx].col].corX,
                        board.map[player[idx].row+1][player[idx].col].corY);
            }
            else {
                player[idx].setDes(board.map[player[idx].row+1][player[idx].col-1].corX,
                        board.map[player[idx].row+1][player[idx].col-1].corY);
            }
        }
        else if (player[idx].getWalking() == 6 && walker.getRoute() == 6) {
            if (player[idx].getCoordinates().x > player[idx].getDes().x || player[idx].getCoordinates().y
                    > player[idx].getDes().y) {
                player[idx].setFacing(Hero.State.LEFT);
                player[idx].setCurrentState(Hero.State.WALKING);
                if (player[idx].getCoordinates().x > player[idx].getDes().x) {
                    player[idx].setCoordinates(player[idx].getCoordinates().x -= (1/sqrt(3)) * tilePixelWidth * dt,
                            player[idx].getCoordinates().y);
                }
                if (player[idx].getCoordinates().y > player[idx].getDes().y) {
                    player[idx].setCoordinates(player[idx].getCoordinates().x,
                            player[idx].getCoordinates().y -= (sqrt(3)/2) * tilePixelHeight * dt);
                }
            } else if (player[idx].getCoordinates().x <= player[idx].getDes().x && player[idx].getCoordinates().y
                    <= player[idx].getDes().y) {
                if (player[idx].row % 2 == 0) {
                    player[idx].setRowCol(player[idx].row + 1, player[idx].col);
                } else {
                    player[idx].setRowCol(player[idx].row + 1, player[idx].col - 1);
                }
                player[idx].setWalking(0);
                player[idx].setCurrentState(Hero.State.STANDING);
                walker.routing();
            }
        }
    }

    public void fontDrawDebugging() {
        //render screen coordinates
        font.draw(game.batch, "Screen Coordinates", 155, 660);
        font.draw(game.batch, (int) screenCoordinates.x + " , "
                + (int) Math.abs(mapPixelHeight-screenCoordinates.y), 190, 635);

        //render hero coordinates
        font.draw(game.batch, "Hero Coordinates (changed)", 400, 660);
        font.draw(game.batch, (int) player[idx].getCoordinates().x + " , " + (int) player[idx].getCoordinates().y, 435, 635);

        //render destination coordinates
        font.draw(game.batch, "Destination Coordinates", 600, 660);
        font.draw(game.batch, (int) player[idx].getDes().x + " , " + (int) player[idx].getDes().y, 635, 635);

        //render walk coordinates
        font.draw(game.batch, "Status", 800, 660);
        font.draw(game.batch, Integer.toString(player[idx].getWalking()), 820, 635);

        //row-col coordinates
        font.draw(game.batch, "Row-Column", 900, 660);
        font.draw(game.batch, player[idx].row + " , " + player[idx].col, 920, 635);

        //render walk coordinates
        font.draw(game.batch, "Routing", 1000, 660);
        font.draw(game.batch, Integer.toString(player[idx].getWalking()), 1000, 635);

    }

    public void renderingHero(int idx) {
        if (player[idx].facing.compareTo(Hero.State.RIGHT) == 0) {
            game.batch.draw(player[idx].action().getKeyFrame(player[idx].getElapsedTime(), true),
                    player[idx].getCoordinates().x + (player[idx].action().getKeyFrame(player[idx].getElapsedTime(),
                            true).getRegionWidth()), player[idx].getCoordinates().y,
                    -(player[idx].action().getKeyFrame(player[idx].getElapsedTime(), true).getRegionWidth()),
                    player[idx].action().getKeyFrame(player[idx].getElapsedTime(), true).getRegionHeight());
        }
        else {
            game.batch.draw(player[idx].action().getKeyFrame(player[idx].getElapsedTime(), true),
                    player[idx].getCoordinates().x - 20, player[idx].getCoordinates().y);
        }
    }

    @Override
    public void render(float delta) {
        //hero walking simulating
        simulatedInput(delta);

        //update player
        player[0].update(delta);
        player[1].update(delta);
        player[2].update(delta);
        player[3].update(delta);

        //Clear the game screen with Black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // get coordinates
        screenCoordinates.set(Gdx.input.getX(), Gdx.input.getY(), 0);

        game.batch.begin();

        //render map
        game.batch.draw(map, 0, 0, Axivals.V_WIDTH, Axivals.V_HEIGHT );

        fontDrawDebugging();

        //render Overlay
        overlay.showOverlay(player[idx].col, player[idx].row, player[idx].walk);

       //render hero
        renderingHero(0);
        renderingHero(1);
        renderingHero(2);
        renderingHero(3);

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
        if (Gdx.input.isKeyPressed(keycode)) {

        }
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
//        System.out.println(rowcol.x+","+rowcol.y);
        List<Vector2> area = new LinkedList<Vector2>();
        area.addAll(board.getOverlay(player[idx].col, player[idx].row, player[idx].walk));
        if (!board.map[(int)rowcol.y][(int)rowcol.x].isObstacle() && walker.getRoute() == 0
                && area.contains(rowcol) && !(rowcol.equals(new Vector2(player[idx].col, player[idx].row)))) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && walker.isRouting() == 0) {
                walker.setRouting(1);
//                System.out.println("Mouse clicked!");
                float x = Gdx.input.getX();
                float y =  Math.abs(mapPixelHeight-Gdx.input.getY());
                path = new LinkedList<Vector2>();
                Vector2 goal = click.getRowCol(x, y);
//                System.out.println("Column-Row = " + goal.x + "," + goal.y);
                path.addAll(board.getPath(player[idx].getRowCol(), goal));
                board.map[player[idx].row][player[idx].col].setObstacle(0);
                board.map[(int)goal.y][(int)goal.x].setObstacle(2);
                walker.setPath(player[idx].getRowCol(), path);
                walker.routing();
            }
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
