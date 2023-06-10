package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Rifle extends Weapon{
    public Texture tx;
    public Rifle() {
        super(1, 5f / 60, 2f,
                null, 1500, 20, 5);
        tx = new Texture(Gdx.files.internal("player/rifle.png"));
        this.txt = tx;
        super.buildPack();
        this.x_offset = 0.4f;
        this.y_offset = 15f;
        this.bullet_start = 4f;
    }
}
