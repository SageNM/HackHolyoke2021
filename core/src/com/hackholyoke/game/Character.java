package com.hackholyoke.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;
import java.lang.Math;

public class Character {
    public static final int MAX_DAMAGE_FRAMES = 30;
    public static final int MAX_ACCELERATION = 1;
    public static final int MAX_VELOCITY = 15;
    public static final int GROUND = 50;
    public static final int DEFAULT_HEALTH = 5;
    public static final int DEFAULT_LEFT = -928;
    public static final int DEFAULT_RIGHT = 928 * 15;
    public int leftBound = DEFAULT_LEFT;
    public int rightBound = DEFAULT_RIGHT;
    // tracking time, location, velocity, and frames
    protected float elapsedTime = 0;
    protected Coord coord;
    protected Coord velocity;
    private final Coord acceleration;
    protected int health = DEFAULT_HEALTH;
    private int damageCounter = 0;
    private boolean damaged = false;
    protected Animation<Sprite> animatedSprite;

    /**
     * Generates character that loops through all sprites on sprite sheet. Does not move.
     */
    public Character(TextureAtlas atlas) {
        Array<Sprite> character = atlas.createSprites();
        animatedSprite = new Animation<Sprite>(0.1f, character, Animation.PlayMode.LOOP);
        coord = new Coord(0, 0);
        velocity = new Coord(0, 0);
        acceleration = new Coord(0, 0);
    }

    public Character(TextureAtlas atlas, Coord start, Coord startVel, Coord startAccel) {
        Array<Sprite> character = atlas.createSprites();
        animatedSprite = new Animation<Sprite>(0.1f, character, Animation.PlayMode.LOOP);
        coord = start;
        velocity = startVel;
        acceleration = startAccel;
    }

    public void tick (float timeChange) {
        changeVel(acceleration);
        offset(velocity);
        if (damaged) {
            damageCounter++;
        }
        if (damageCounter > MAX_DAMAGE_FRAMES) {
            damaged = false;
            damageCounter = 0;
        }
        elapsedTime += timeChange;
    }

    public void setLeftBound(int bound) {
        leftBound = bound;
    }

    public void setRightBound(int bound) {
        rightBound = bound;
    }

    public Coord getLocation() {
        return coord;
    }

    public void offset (Coord offset) {
        Coord newLocation = coord.plus(offset);
        if (newLocation.y > GROUND) {
            coord.y = newLocation.y;
        } else {
            coord.y = GROUND;
        }
        if (newLocation.x > leftBound && newLocation.x < rightBound) {
            coord.x = newLocation.x;
        } else {
            velocity.x = 0;
        }
    }

    public void setLocation (Coord location) {
        if (location.y > GROUND) {
            coord.y = location.y;
        } else {
            coord.y = GROUND;
        }
        if (location.x > leftBound && location.x < rightBound) {
            coord.x = location.x;
        }
    }

    public Coord getVelocity () {
        return velocity;
    }

    public void changeVel (Coord change) {
        Coord newVel = velocity.plus(change);
        if (Math.abs(newVel.x) < MAX_VELOCITY) {
            velocity.x = newVel.x;
        }
        if (Math.abs(newVel.y) < MAX_VELOCITY) {
            velocity.y = newVel.y;
        }
        if (coord.y + velocity.y < GROUND) {
            velocity.y = 0;
        }
    }

    public void setVel (Coord vel) {
        if (Math.abs(vel.x) < MAX_VELOCITY) {
            velocity.x = vel.x;
        }
        if (Math.abs(vel.y) < MAX_VELOCITY) {
            velocity.y = vel.y;
        }
    }

    public Coord getAcceleration() {
        return acceleration;
    }

    public void changeAccel (Coord change) {
        Coord newAccel = acceleration.plus(change);
        if (Math.abs(newAccel.x) < MAX_ACCELERATION) {
            acceleration.x = newAccel.x;
        }
        if (Math.abs(newAccel.y) < MAX_ACCELERATION) {
            acceleration.y = newAccel.y;
        }
    }

    public void setAccel (Coord accel) {
        if (Math.abs(accel.x) < MAX_ACCELERATION) {
            acceleration.x = accel.x;
        }
        if (Math.abs(accel.y) < MAX_ACCELERATION) {
            acceleration.y = accel.y;
        }
    }

    public boolean isDamaged() {
        return damaged;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void takeHit(int damage) {
        damaged = true;
        health = health - damage;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public boolean isHit(Character character) {
        Rectangle selfRect = this.getSprite().getBoundingRectangle();
        Vector2 selfCenter = new Vector2();
        selfCenter = selfRect.getCenter(selfCenter);
        Rectangle otherRect = character.getSprite().getBoundingRectangle();
        Vector2 otherCenter = new Vector2();
        otherCenter = otherRect.getCenter(otherCenter);
        return selfCenter.dst(otherCenter) < (selfRect.getWidth() / 2);
    }

    public boolean isHit(Character character, int factor) {
        Rectangle selfRect = this.getSprite().getBoundingRectangle();
        Vector2 selfCenter = new Vector2();
        selfCenter = selfRect.getCenter(selfCenter);
        Rectangle otherRect = character.getSprite().getBoundingRectangle();
        Vector2 otherCenter = new Vector2();
        otherCenter = otherRect.getCenter(otherCenter);
        return selfCenter.dst(otherCenter) < (selfRect.getWidth() / factor);
    }

    public Sprite getSprite() {
        Sprite sprite = animatedSprite.getKeyFrame(elapsedTime);
        sprite.setPosition(coord.x, coord.y);
        if (velocity.x < 1) {
            sprite.setFlip(true, false);
        }
        return sprite;
    }
}
