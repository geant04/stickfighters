package com.mygdx.game.Weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Weapon;

public class Pistol extends Weapon {
    public Texture tx;
    public Pistol() {
        super(0, 5f / 1000, 0.5f,
                null, 1500, -1, 5);
        tx = new Texture(Gdx.files.internal("player/pistol.png"));
        this.txt = tx;
        this.fire = Gdx.audio.newSound(Gdx.files.internal("sounds/pistol.ogg"));
        this.volume = 0.2f;
        super.buildPack(); // i forgot what this does
    }
}
