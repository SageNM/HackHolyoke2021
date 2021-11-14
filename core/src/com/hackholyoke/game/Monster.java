package com.hackholyoke.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

public class Monster extends Character {
    private TextureAtlas atlas;
    private String state;

    public Monster(TextureAtlas atlas) {
        super(atlas);
        this.atlas = atlas;
    }

    public Monster(TextureAtlas atlas, Coord location, Coord vel, Coord accel) {
        super(atlas, location, vel, accel);
        this.atlas = atlas;
    }

    public void offset (Coord offset) {
        Coord newLocation = coord.plus(offset);
        if (newLocation.y > GROUND - 50) {
            coord.y = newLocation.y;
        } else {
            coord.y = GROUND - 50;
        }
        if (newLocation.x > leftBound && newLocation.x < rightBound) {
            coord.x = newLocation.x;
        } else {
            velocity.x = -1* velocity.x;
        }
    }

    //assumes valid state
    public void setState(String newState) {
        super.elapsedTime = 0;
        Array<Sprite> character = atlas.createSprites(newState);
        if (newState.equals("death")) {
            super.animatedSprite = new Animation<Sprite>(0.1f, character, Animation.PlayMode.NORMAL);
            velocity.x = 0;
            velocity.y = 0;
        } else {
            super.animatedSprite = new Animation<Sprite>(0.1f, character, Animation.PlayMode.LOOP);
        }
        state = newState;
    }

    public String getState() {
        return state;
    }
}
