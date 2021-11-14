package com.hackholyoke.game;

import com.badlogic.gdx.graphics.Texture;

public class Background {

    // Hardcoded right now, but totally could just cycle through all layers
    private final Texture[] backgrounds;
    private float velocity = 0;
    private final float[] offsets;

    public Background() {
        backgrounds = new Texture[12];
        backgrounds[0] = new Texture("layers/01.png");
        backgrounds[0].setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.ClampToEdge);
        backgrounds[1] = new Texture("layers/02.png");
        backgrounds[1].setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.ClampToEdge);
        backgrounds[2] = new Texture("layers/03.png");
        backgrounds[2].setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.ClampToEdge);
        backgrounds[3] = new Texture("layers/04.png");
        backgrounds[3].setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.ClampToEdge);
        backgrounds[4] = new Texture("layers/05.png");
        backgrounds[4].setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.ClampToEdge);
        backgrounds[5] = new Texture("layers/06.png");
        backgrounds[5].setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.ClampToEdge);
        backgrounds[6] = new Texture("layers/07.png");
        backgrounds[6].setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.ClampToEdge);
        backgrounds[7] = new Texture("layers/08.png");
        backgrounds[7].setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.ClampToEdge);
        backgrounds[8] = new Texture("layers/09.png");
        backgrounds[8].setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.ClampToEdge);
        backgrounds[9] = new Texture("layers/10.png");
        backgrounds[9].setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.ClampToEdge);
        backgrounds[10] = new Texture("layers/11.png");
        backgrounds[10].setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.ClampToEdge);
        backgrounds[11] = new Texture("layers/12.png");
        backgrounds[11].setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.ClampToEdge);




        offsets = new float[12];
    }

    public void tick() {
        for (int i = 0; i < backgrounds.length; i++) {
            offsets[i] = offsets[i] + (velocity * i * 0.25f);
        }
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public float[] getOffsets () {
        return offsets;
    }

    public Texture[] getTextures () {
        return backgrounds;
    }
}