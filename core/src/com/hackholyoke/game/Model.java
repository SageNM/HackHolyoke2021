package com.hackholyoke.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public class Model implements Screen{
    // tracking game state
    public enum GAME_STATE {START, MINI, CONVERSATION, END}
    private GAME_STATE currentState = GAME_STATE.START;
    private int tick;
    private float elapsedTime = 0;

    // CONSTANTS
    public static final int WORLD_WIDTH = 928;
    public static final int WORLD_HEIGHT = 793;
    public static final int GROUND = 50;
    public static final double GRAVITY = -0.25;
    public static final int MAX_MONSTERS = 50;

    // views
    private Camera camera;
    private Viewport viewport;

    // Drawing tools
    private SpriteBatch batch;
    private BitmapFont font;
    private TextureAtlas playerAtlas;
    private TextureAtlas monsterAtlas;
    private TextureAtlas starAtlas;
    private Sprite sprite;

    // Start screen variables
    private GlyphLayout startText;

    // Mini game variables
    private Background miniBack;
    private Player player;
    private double spawnRate = 3;
    private double playerSpeed = 2;
    private ArrayList<Monster> monsters;
    private ArrayList<Character> stars;


    public Model() {
        // Setting up variables
        miniBack = new Background();
        font = new BitmapFont();
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera); //what the user sees

        // setting up fixed values
        startText = new GlyphLayout();
        startText.setText(font, "MONSTARS\nPress SPACE to Start!");
        playerAtlas = new TextureAtlas("Warrior.pack");
        monsterAtlas = new TextureAtlas("skeleton.pack");

        // starting game
        restart();
    }

    private void restart() {
        tick = 0;
        spawnRate = 5;
        Coord start = new Coord(WORLD_WIDTH/2f,12);
        Coord vel = new Coord(0, 0);
        Coord accel = new Coord(0, GRAVITY);
        player = new Player(playerAtlas, start, vel, accel);
        player.setLeftBound(WORLD_WIDTH/8);
        player.setRightBound(WORLD_WIDTH * 15);
        player.setState("idle");
        // resetting minigame
        monsters = new ArrayList<>();
        stars = new ArrayList<>();
    }


    @Override
    public void render(float deltaTime) {
        switch (currentState) {
            case START:
                renderStart();
                break;
            case MINI:
                renderMini();
                break;
            case CONVERSATION:
                renderConvo(deltaTime);
                break;
            case END:
                renderEnd(deltaTime);
                break;
            default: System.out.println("Fuck");
                break;
        }
    }

    /**
     * Basic bitch background until I get around to fixing this.
     */
    public void renderStart() {
        tick++;
        // running background
        batch.begin();
        miniBack.setVelocity(1);
        miniBack.tick();
        Texture[] textures = miniBack.getTextures();
        float[] offsets = miniBack.getOffsets();
        for (int i = 0; i < textures.length; i++) {
            batch.draw(textures[i], 0, 0, (int) offsets[i], 0, (int) camera.viewportWidth, textures[i].getHeight());
        }
        // Drawing title
        font.setColor(Color.WHITE);
        font.draw(batch, startText, (camera.viewportWidth - startText.width)/2, (camera.viewportHeight + startText.height)/2);
        batch.end();
        //starting game after 1 second
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && tick > 60) {
            currentState = GAME_STATE.MINI;
            tick = 0;
        }
    }

    public void renderMini() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        tick++;
        // if hitting end of time
        if (tick > 60 * 60) {
            tick = 0;
            currentState = GAME_STATE.CONVERSATION;
        }
        // centering camera on character
        camera.position.set(player.getLocation().x, camera.viewportHeight / 2f, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        // Character stuff
        player.tick(deltaTime);
        sprite = player.getSprite();
        checkMiniInput();
        // Background stuff
        miniBack.tick();
        miniBack.setVelocity(player.getVelocity().x);
        Texture[] textures = miniBack.getTextures();
        float[] offsets = miniBack.getOffsets();
        for (int i = 0; i < textures.length; i++) {
            batch.draw(textures[i], -300, 0, (int) offsets[i], 0, Gdx.graphics.getWidth() * 15, textures[i].getHeight());
        }
        // Enemy stuff
        genMonsters();
        genStars();
        checkCollisions(deltaTime);
        // draw character and foreground
        sprite.draw(batch);
        batch.end();
    }

    public void renderConvo(float deltaTime) {

    }

    public void renderEnd(float deltaTime) {

    }

    public void checkMiniInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !(player.getState().equals("run") && player.getVelocity().x > 0)) {
            player.setState("run");
            player.setVel(new Coord(playerSpeed, player.getVelocity().y));
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)&& !(player.getState().equals("run") && player.getVelocity().x < 0)) {
            player.setState("run");
            player.setVel(new Coord(-1 * playerSpeed, player.getVelocity().y));
        } else if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && !player.getState().equals("attack")) {
            player.setState("attack");
            player.setVel(new Coord(0, player.getVelocity().y));
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP) && !player.getState().equals("jump")) {
            player.setState("jump");
            if (player.getLocation().y <= GROUND) {
                player.setVel(new Coord(player.getVelocity().x * 0.8, 5));
            }
        }
        // if all keys lifted
        if (!Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.LEFT)
                && !Gdx.input.isKeyPressed(Input.Keys.UP) && !Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            player.setState("idle");
            player.setVel(new Coord(0, player.getVelocity().y));
        }
    }

    public void genMonsters() {
        // randomly spawns monsters up to a point
        if (tick % (int)(60 * spawnRate) == 0 && monsters.size() < MAX_MONSTERS) {
            if (spawnRate > 0.5) {
                spawnRate -= 0.05;
            }
            int velocity = (int)(Math.random()*4) + 1;
            boolean spawnRight = (int)(Math.random() *2) == 0;
            Monster newMonster;
            if (spawnRight) {
                newMonster = new Monster(monsterAtlas, new Coord(player.getLocation().x + 600, 0), new Coord(-velocity, 0), new Coord(0, GRAVITY));
            } else {
                newMonster = new Monster(monsterAtlas, new Coord(player.getLocation().x - 600, 0), new Coord(velocity, 0), new Coord(0, GRAVITY));
            }
            newMonster.setState("run");
            monsters.add(newMonster);
        }
    }

    public void genStars() {

    }

    public void checkCollisions(float delta) {
        for (Monster monster : monsters) {
            monster.tick(delta);
            Sprite monsterSprite = monster.getSprite();
            if (monster.isHit(player) && player.getState().equals("attack") && !monster.isDamaged() && monster.isAlive()) {
                int playerDamage = (int) (Math.random() * 10) + 2;
                monster.takeHit(playerDamage);
                monster.setState("hurt");
                monster.setVel(new Coord(-1 * monster.getVelocity().x, 5));
                if (!monster.isAlive()) {
                    monster.setState("death");
                }
            } else if (player.isHit(monster, 10) && !player.isDamaged() && monster.isAlive()) {
                int monsterDamage = (int) (Math.random() * 2) + 1;
                player.takeHit(monsterDamage);
                player.setState("hurt");
                player.setVel(new Coord(0, player.getVelocity().y));
                if (!player.isAlive()) {
                    currentState = GAME_STATE.CONVERSATION;
                    tick = 0;
                }
            }
            if (monster.isDamaged()) {
                monsterSprite.setColor(Color.RED);
            } else {
                monsterSprite.setColor(Color.WHITE);
                if (monster.getState().equals("hurt")) {
                    monster.setState("run");
                }
            }
            monsterSprite.draw(batch);
        }
        if (player.isDamaged()) {
            sprite.setColor(Color.RED);
        } else {
            if (player.getState().equals("hurt")) {
                player.setState("idle");
            }
            sprite.setColor(Color.WHITE);
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void dispose() {
        font.dispose();
        batch.dispose();
    }
}
