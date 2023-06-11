package com.mygdx.game.Weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Weapon;

public class Rifle extends Weapon {
    public Texture tx;
    public Rifle() {
        super(1, 5f / 60, 2f,
                null, 1500, -1, 2);
        tx = new Texture(Gdx.files.internal("player/rifle.png"));
        this.fire = Gdx.audio.newSound(Gdx.files.internal("sounds/rifle.ogg"));
        this.volume = 1f;
        this.txt = tx;
        super.buildPack();
        this.x_offset = 0.4f;
        this.y_offset = 12f;
        this.bullet_start = 4f;
    }
}
