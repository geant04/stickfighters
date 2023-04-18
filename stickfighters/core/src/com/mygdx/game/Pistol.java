package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Pistol extends Weapon{
    public Texture tx;
    public Pistol() {
        super(0, 0.00003f, 0.5f, null, 1500, 10);
        tx = new Texture(Gdx.files.internal("pistol.png"));
        this.txt = tx;
        super.buildPack();
    }
}
