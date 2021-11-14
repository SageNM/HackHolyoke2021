package com.hackholyoke.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public class Model implements Screen {

    public class MyInputProcessor extends InputAdapter {

        @Override
        public boolean keyDown(int keycode) {
            if (currentState == GAME_STATE.INTRO) {
                if (keycode == Input.Keys.SPACE) {
                     if (currentScenes[currentScene].advance()) {
                         if (currentScene < currentScenes.length - 1) {
                             currentScene++;
                             speaker.setText(font, currentScenes[currentScene].getSpeaker());
                             String dialogueText = currentScenes[currentScene].getDialogue().replace(". ", ".\n");
                             dialogue.setText(font, dialogueText);
                         } else {
                             currentState = GAME_STATE.MINI;
                             currentScene = 0;
                         }
                     } else {
                         speaker.setText(font, currentScenes[currentScene].getSpeaker());
                         String dialogueText = currentScenes[currentScene].getDialogue().replace(". ", ".\n");
                         dialogue.setText(font, dialogueText);
                     }
                }
            } else if (currentState == GAME_STATE.CONVERSATION) {
                if (keycode == Input.Keys.SPACE) {
                    if (awakeScenes[currentScene].advance()) {
                        if (currentScene < awakeScenes.length - 1) {
                            currentScene++;
                            speaker.setText(font, awakeScenes[currentScene].getSpeaker());
                            String dialogueText = awakeScenes[currentScene].getDialogue().replace(". ", ".\n");
                            dialogue.setText(font, dialogueText, Color.WHITE, (float)MAX_WIDTH, Align.left, true);
                        } else {
                            currentState = GAME_STATE.BATTLE;
                            currentScene = 0;
                        }
                    } else {
                        speaker.setText(font, awakeScenes[currentScene].getSpeaker());
                        String dialogueText = awakeScenes[currentScene].getDialogue().replace(". ", ".\n");
                        dialogue.setText(font, dialogueText, Color.WHITE, (float)MAX_WIDTH, Align.left, true);
                    }
                }
            }
            return super.keyDown(keycode);
        }
    }

    // tracking game state
    public enum GAME_STATE {START, INTRO, MINI, CONVERSATION, BATTLE, END}
    private GAME_STATE currentState = GAME_STATE.START;
    private int tick;
    private float elapsedTime = 0;

    // CONSTANTS
    public static final int WORLD_WIDTH = 928;
    public static final int WORLD_HEIGHT = 793;
    public static final int GROUND = 50;
    public static final double GRAVITY = -0.25;
    public static final double STAR_GRAVITY = -0.15;
    public static final int MAX_MONSTERS = 50;
    public static final int MAX_WIDTH = 928 * 5/7 - 50;

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
    private ShapeRenderer shape;

    // Overall game variables
    private final Comment[] truths;
    private final Comment[] lies;
    private ArrayList<Comment> playerWords;
    private int currentScene = 0;
    private Scene[] currentScenes;

    // Start screen variables
    private GlyphLayout startText;
    private Scene[] introScenes;

    // Mini game variables
    private Background miniBack;
    private Player player;
    private double spawnRate = 3;
    private double playerSpeed = 2;
    private ArrayList<Monster> monsters;
    private ArrayList<Character> stars;
    private int lives = 5;
    private GlyphLayout wordCollected;

    // Conversation Variables
    private Scene[] awakeScenes;
    private Texture convoBack;
    private Texture body;
    private Texture eyes;
    private Texture mouth;
    private int playerLies = 0;
    private int playerTruths = 0;
    private Portrait portrait;
    private int yourCred = 100;
    private int sitCred = 100;
    private int otherCred = 100;
    private Comment.EFFECT currentEmotion = Comment.EFFECT.NONE;
    private Comment.EFFECT pastEmotion = Comment.EFFECT.NONE;
    private int currentEmotionLevel = 0; // ranges from 0 to 100
    private GlyphLayout dialogue; // for plot stuff
    private GlyphLayout speaker;

    // Battle Variables
    private Scene[] quips;
    private boolean options = true;

    public Model() {

        // Setting up variables
        miniBack = new Background();
        font = new BitmapFont();
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera); //what the user sees
        shape = new ShapeRenderer();
        Gdx.input.setInputProcessor(new MyInputProcessor());

        // parsing XML files
        CommentParser truthsLies = new CommentParser("TruthsLies.xml");
        truths = truthsLies.getTruths();
        lies = truthsLies.getLies();

        // setting up fixed values
        startText = new GlyphLayout();
        startText.setText(font, "MONSTARS\nPress SPACE to Start!");
        wordCollected = new GlyphLayout();
        dialogue = new GlyphLayout();
        speaker = new GlyphLayout();
        playerAtlas = new TextureAtlas("Warrior.pack");
        monsterAtlas = new TextureAtlas("skeleton.pack");
        starAtlas = new TextureAtlas("Star.pack");
        convoBack = new Texture("dorm.jpg");
        body = new Texture("fm02/fm02-body.png");

        // starting game
        fullReset();
    }

    private void restart() {
        tick = 0;
        spawnRate = 5;
        currentScene = 0;
        Coord start = new Coord(WORLD_WIDTH * 15/2f,12);
        Coord vel = new Coord(0, 0);
        Coord accel = new Coord(0, GRAVITY);
        player = new Player(playerAtlas, start, vel, accel);
        player.setLeftBound(WORLD_WIDTH/8);
        player.setRightBound(WORLD_WIDTH * 15);
        player.setState("idle");
        // resetting minigame
        wordCollected.setText(font, "");
        monsters = new ArrayList<>();
        stars = new ArrayList<>();
        // resetting conversation
        yourCred = 100;
        sitCred = 100;
        otherCred = 100;
        currentEmotion = Comment.EFFECT.NONE;
        currentEmotionLevel = 0; // ranges from 0 to 100
        dialogue.setText(font, "");
        speaker.setText(font, "");
        options = true;
        eyes = new Texture("fm02/fm02-eyes-smile.png");
        mouth = new Texture("fm02/fm02-mouth-smile00.png");
    }

    public void fullReset() {
        lives = 5;
        playerWords = new ArrayList<>();
        // resetting all dialogue
        DialogueParser intro = new DialogueParser("Intro.xml");
        introScenes = intro.getScenes();
        DialogueParser awake = new DialogueParser("Awake.xml");
        awakeScenes = awake.getScenes();
        DialogueParser quip = new DialogueParser("Quips.xml");
        quips = quip.getScenes();
        playerLies = 0;
        playerTruths = 0;
        restart();
    }

    @Override
    public void render(float deltaTime) {
        switch (currentState) {
            case START:
                renderStart();
                break;
            case INTRO:
                renderIntro();
                break;
            case MINI:
                renderMini();
                break;
            case CONVERSATION:
                renderConvo(deltaTime);
                break;
            case BATTLE:
                renderBattle();
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
            currentState = GAME_STATE.INTRO;
            tick = 0;
        }
    }

    public void renderIntro() {
        currentScenes = introScenes;
        // place background
        if (currentScene == 0) {
            batch.begin();
            batch.draw(convoBack, (camera.viewportWidth - convoBack.getWidth())/2, (camera.viewportHeight - convoBack.getHeight())/2);
            batch.end();
        } else {
            ScreenUtils.clear(Color.BLACK);
        }
        // generic text box
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.TEAL);
        shape.rect(camera.viewportWidth/7, 50, 5* camera.viewportWidth/7, camera.viewportHeight/5);
        shape.end();
        batch.begin();
        //actual text
        font.setColor(Color.WHITE);
        font.draw(batch, speaker, (camera.viewportWidth - speaker.width)/2, camera.viewportHeight/5 + 25);
        font.draw(batch, dialogue, (camera.viewportWidth - dialogue.width)/2, camera.viewportHeight/5 - speaker.height);
        batch.end();
    }

    public void renderMini() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        tick++;
        // if hitting end of time
        if (tick > 60 * 60) {
            tick = 0;
            currentState = GAME_STATE.CONVERSATION;
            lives--;
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
        // draw character
        sprite.draw(batch);
        // show what lie/truth was collected
        font.setColor(Color.WHITE);
        font.draw(batch, wordCollected, player.getLocation().x - (wordCollected.width)/2, (WORLD_HEIGHT + wordCollected.height)/2);
        batch.end();
    }

    public void renderConvo(float deltaTime) {
        // ask if user wants to retry game
        batch.begin();
        if (lives > 0) {
            ScreenUtils.clear(Color.BLACK);
            sprite = player.getSprite();
            sprite.draw(batch);
            // Run again
            dialogue.setText(font, "You have collected " + playerTruths + " truths and " + playerLies + " lies\n" +
                    "You have "+ lives+ " more lives remaining. Do you wish to continue?\nPress Y for YES or N for NO", Color.WHITE, (float)MAX_WIDTH, Align.left, true);
            font.draw(batch, dialogue, player.getLocation().x-(dialogue.width)/2, camera.viewportHeight/2f);
            if (Gdx.input.isKeyPressed(Input.Keys.Y)) {
                currentState = GAME_STATE.MINI;
                restart();
            } else if (Gdx.input.isKeyPressed(Input.Keys.N)) {
                lives = 0;
                currentScene = 0;
                currentScenes = awakeScenes;
                speaker.setText(font, "");
                dialogue.setText(font, "");
                // print out old lines
            }
        } else {
            // centering camera on character
            camera.position.set(WORLD_WIDTH/2f, camera.viewportHeight / 2f, 0);
            camera.update();
            batch.setProjectionMatrix(camera.combined);
            // background
            batch.draw(convoBack, (camera.viewportWidth - convoBack.getWidth())/2, (camera.viewportHeight - convoBack.getHeight())/2);
            batch.end();
            // person
            if (currentScene > 0) {
                batch.begin();
                batch.draw(body, camera.viewportWidth/8, 0, camera.viewportWidth *3 /4, camera.viewportHeight);
                batch.draw(eyes, camera.viewportWidth/8, 0, camera.viewportWidth * 3/4, camera.viewportHeight);
                batch.draw(mouth, camera.viewportWidth/8, 0, camera.viewportWidth *3/4, camera.viewportHeight);
                batch.end();
            }
            // generic text box
            shape.begin(ShapeRenderer.ShapeType.Filled);
            shape.setColor(Color.TEAL);
            shape.rect(camera.viewportWidth/7, 50, 5* camera.viewportWidth/7, camera.viewportHeight/5);
            shape.end();
            batch.begin();
            // dialogue
            //actual text
            font.setColor(Color.WHITE);
            font.draw(batch, speaker, (camera.viewportWidth - speaker.width)/2, camera.viewportHeight/5 + 25);
            font.draw(batch, dialogue, (camera.viewportWidth - dialogue.width)/2, camera.viewportHeight/5 - speaker.height);

        }
        batch.end();
    }

    public void renderBattle() {
        // making background
        batch.begin();
        batch.draw(convoBack, (camera.viewportWidth - convoBack.getWidth())/2, (camera.viewportHeight - convoBack.getHeight())/2);
        batch.end();
        // person
        if (options) {
            // display 4 options
            shape.begin(ShapeRenderer.ShapeType.Filled);
            shape.setColor(Color.TEAL);
            shape.rect(camera.viewportWidth/7, 50 + 50, 5* camera.viewportWidth/7, camera.viewportHeight/7);
            shape.rect(camera.viewportWidth/7, 50 + 50*2 + camera.viewportHeight/7, 5* camera.viewportWidth/7, camera.viewportHeight/7);
            shape.rect(camera.viewportWidth/7, 50 + 50*3 + camera.viewportHeight/7 * 2, 5* camera.viewportWidth/7, camera.viewportHeight/7);
            shape.rect(camera.viewportWidth/7, 50 + 50 * 4 + camera.viewportHeight/7 * 3, 5* camera.viewportWidth/7, camera.viewportHeight/7);
            shape.end();
            batch.begin();
            GlyphLayout option = new GlyphLayout();
            String flavor = "None";
            if (playerWords.size() >= 4) {
                flavor = playerWords.get(3).getFlavor();
            }
            option.setText(font, "4. " + flavor, Color.WHITE, (float) MAX_WIDTH, Align.left, true);
            font.draw(batch, option, camera.viewportWidth/7 + 20, 50 * 3 + option.height);
            if (playerWords.size() >= 3) {
                flavor = playerWords.get(2).getFlavor();
            }
            option.setText(font, "3. " + flavor, Color.WHITE, (float) MAX_WIDTH, Align.left, true);
            font.draw(batch, option, camera.viewportWidth/7 + 20, 50 * 4+ option.height + camera.viewportHeight/7);
            if (playerWords.size() >= 2) {
                flavor = playerWords.get(1).getFlavor();
            }
            option.setText(font, "2. " + flavor, Color.WHITE, (float) MAX_WIDTH, Align.left, true);
            font.draw(batch, option, camera.viewportWidth/7 + 20, 50 * 5 + option.height + camera.viewportHeight/7 * 2);
            if (playerWords.size() >= 1) {
                flavor = playerWords.get(0).getFlavor();
            }
            option.setText(font, "1. " + flavor, Color.WHITE, (float) MAX_WIDTH, Align.left, true);
            font.draw(batch, option, camera.viewportWidth/7 + 20, 50 * 6 + option.height + camera.viewportHeight/7 * 3);
            batch.end();
            // press number keys to activate
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
                if (playerWords.size() >= 1) {
                    evaluate(playerWords.get(0));
                } else {
                    evaluate(null);
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
                if (playerWords.size() >= 2) {
                    evaluate(playerWords.get(1));
                } else {
                    evaluate(null);
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
                if (playerWords.size() >= 3) {
                    evaluate(playerWords.get(2));
                } else {
                    evaluate(null);
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)) {
                if (playerWords.size() >= 4) {
                    evaluate(playerWords.get(3));
                } else {
                    evaluate(null);
                }
            }
        } else {
            int response;

            if (pastEmotion != currentEmotion){
                response = (int) (Math.random() * 2);
                switch (currentEmotion) {
                    case NONE:
                        eyes = new Texture("fm02/fm02-eyes-smile.png");
                        mouth = new Texture("fm02/fm02-mouth-smile00.png");
                        speaker.setText(font, quips[quips.length - 1].getSpeaker());
                        dialogue.setText(font, quips[quips.length - 1].getDialogue());
                        break;
                    case ANGER:
                        eyes = new Texture("fm02/fm02-eyes-upset.png");
                        mouth = new Texture("fm02/fm02-mouth-upset00.png");
                        speaker.setText(font, quips[3].getSpeaker());
                        dialogue.setText(font, quips[3].getDialogue());
                        break;
                    case TRUST:
                        eyes = new Texture("fm02/fm02-eyes-joy.png");
                        mouth = new Texture("fm02/fm02-mouth-smile01.png");
                        speaker.setText(font, quips[5].getSpeaker());
                        dialogue.setText(font, quips[5].getDialogue());
                        break;
                    case HURT:
                        eyes = new Texture("fm02/fm02-eyes-bawl.png");
                        mouth = new Texture("fm02/fm02-mouth-cry01.png");
                        speaker.setText(font, quips[0].getSpeaker());
                        dialogue.setText(font, quips[0].getDialogue());
                        break;
                    default:
                        speaker.setText(font, quips[quips.length - 1].getSpeaker());
                        dialogue.setText(font, quips[quips.length - 1].getDialogue());
                        break;
                }
            }
            // get response
            batch.begin();
            batch.draw(body, camera.viewportWidth/8, 0, camera.viewportWidth *3 /4, camera.viewportHeight);
            batch.draw(eyes, camera.viewportWidth/8, 0, camera.viewportWidth * 3/4, camera.viewportHeight);
            batch.draw(mouth, camera.viewportWidth/8, 0, camera.viewportWidth *3/4, camera.viewportHeight);
            batch.end();
            // generic text box
            shape.begin(ShapeRenderer.ShapeType.Filled);
            shape.setColor(Color.TEAL);
            shape.rect(camera.viewportWidth/7, 50, 5* camera.viewportWidth/7, camera.viewportHeight/5);
            shape.end();
            batch.begin();
            font.setColor(Color.WHITE);
            font.draw(batch, speaker, (camera.viewportWidth - speaker.width)/2, camera.viewportHeight/5 + 25);
            font.draw(batch, dialogue, (camera.viewportWidth - dialogue.width)/2, camera.viewportHeight/5 - speaker.height);
            batch.end();
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                options = true;
            }
        }
        // display
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
                player.setVel(new Coord(player.getVelocity().x * 0.8, 10));
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
        if (tick % (int)(30 * spawnRate) == 0 && monsters.size() < MAX_MONSTERS) {
            if (spawnRate > 0.5) {
                spawnRate -= 0.05;
            }
            int velocity = (int)(Math.random()*4) + 1;
            boolean spawnRight = (int)(Math.random() *2) == 0;
            Monster newMonster;
            if (spawnRight) {
                newMonster = new Monster(monsterAtlas, new Coord(player.getLocation().x + 500, 0), new Coord(-velocity, 0), new Coord(0, GRAVITY));
            } else {
                newMonster = new Monster(monsterAtlas, new Coord(player.getLocation().x - 500, 0), new Coord(velocity, 0), new Coord(0, GRAVITY));
            }
            newMonster.setState("run");
            monsters.add(newMonster);
        }
    }

    public void genStars() {
        // randomly spawns stars up to a point
        if (tick % (int)(5 * spawnRate) == 0) {
            int xPos = (int)(Math.random()*700) - 350;
            int velocity = (int)(Math.random()*4) - 2;
            Character star = new Character(starAtlas, new Coord(xPos + player.getLocation().x, WORLD_HEIGHT), new Coord(velocity, 0), new Coord(0, STAR_GRAVITY));
            stars.add(star);
        }
    }

    public void checkCollisions(float delta) {
        // checking player/monster collisions
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
                    getLie();
                }
            } else if (player.isHit(monster, 10) && !player.isDamaged() && monster.isAlive()) {
                int monsterDamage = (int) (Math.random() * 2) + 1;
                player.takeHit(monsterDamage);
                player.setState("hurt");
                player.setVel(new Coord(0, player.getVelocity().y));
                if (!player.isAlive()) {
                    currentState = GAME_STATE.CONVERSATION;
                    lives--;
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
        //checking player/star collisions
        ArrayList<Character> remove = new ArrayList<>();
        for (Character star: stars) {
            star.tick(delta);
            Sprite starSprite = star.getSprite();
            // if player jumps to catch star
            if (star.isHit(player) && player.getLocation().y > GROUND) {
                remove.add(star);
                getTruth();
            } else if (player.isHit(star, 10) && !player.isDamaged()) {
                int starDamage = (int) (Math.random() * 2) + 1;
                player.takeHit(starDamage);
                player.setState("hurt");
                player.setVel(new Coord(0, player.getVelocity().y));
                if (!player.isAlive()) {
                    currentState = GAME_STATE.CONVERSATION;
                    lives--;
                    tick = 0;
                }
                remove.add(star);
            } else if (star.getLocation().y <= GROUND + 5) {
                remove.add(star);
            }
            starSprite.draw(batch);
        }
        stars.removeAll(remove);
    }

    public void getLie() {
        int numLies = lies.length;
        int randomLie = (int)(Math.random() * numLies);
        playerWords.add(lies[randomLie].copy());
        wordCollected.setText(font, lies[randomLie].getFlavor().replace(". ", ".\n"));
        playerLies++;
    }

    public void getTruth() {
        int numTruths = truths.length;
        int randomTruth = (int)(Math.random() * numTruths);
        playerWords.add(truths[randomTruth].copy());
        wordCollected.setText(font, truths[randomTruth].getFlavor().replace(". ", ".\n"));
        playerTruths++;
    }

    public void evaluate(Comment comment) {
        if (comment != null) {
            playerWords.remove(comment);
            playerWords.add(comment);
            // who is it targetting
            Comment.TARGET target = comment.getTarget();
            pastEmotion = currentEmotion;
            currentEmotion = comment.getEffect();
        }
        options = false;
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
        shape.dispose();
    }
}
