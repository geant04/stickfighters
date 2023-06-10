package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Pistol extends Weapon{
    public Texture tx;
    public Pistol() {
        super(0, 5f / 1000, 0.5f,
                null, 1500, 10, 20);
        tx = new Texture(Gdx.files.internal("player/pistol.png"));
        this.txt = tx;
        super.buildPack(); // i forgot what this does
    }
}
