package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Rifle extends Weapon{
    public Texture tx;
    public Rifle() {
        super(1, 5f / 60, 2f,
                null, 1500, 20, 5);
        tx = new Texture(Gdx.files.internal("pistol.png"));
        this.txt = tx;
        super.buildPack();
    }
}
