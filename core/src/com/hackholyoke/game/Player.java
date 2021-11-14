package com.hackholyoke.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

public class Player extends Character{

    private TextureAtlas atlas;
    private String state;

    public Player(TextureAtlas atlas) {
        super(atlas);
        this.atlas = atlas;
    }

    public Player(TextureAtlas atlas, Coord location, Coord vel, Coord accel) {
        super(atlas, location, vel, accel);
        this.atlas = atlas;
    }

    public void offset (Coord offset) {
        Coord newLocation = coord.plus(offset);
        if (newLocation.y > GROUND) {
            coord.y = newLocation.y;
        } else {
            if (state.equals("jump")){
                // landing and stopping from jump
                setState("idle");
                velocity.x = 0;
            }
            coord.y = GROUND;
        }
        if (newLocation.x > leftBound && newLocation.x < rightBound) {
            coord.x = newLocation.x;
        } else {
            velocity.x = 0;
            //hit game end
            if (newLocation.x >= rightBound) {
                setState("idle");
            } else if (newLocation.x <= leftBound) {
                setState("idle");
            }
        }
    }

    //assumes valid state
    public void setState(String newState) {
        super.elapsedTime = 0;
        Array<Sprite> character = atlas.createSprites(newState);
        if (newState.equals("death")) {
            super.animatedSprite = new Animation<Sprite>(0.1f, character, Animation.PlayMode.NORMAL);
        } else {
            super.animatedSprite = new Animation<Sprite>(0.1f, character, Animation.PlayMode.LOOP);
        }
        state = newState;
    }

    public String getState() {
        return state;
    }
}
