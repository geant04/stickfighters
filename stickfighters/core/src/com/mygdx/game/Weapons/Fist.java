package com.mygdx.game.Weapons;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.Weapon;

public class Fist extends Weapon{
    public Texture tx;
    public Fist() {
        super(-1, 5f / 1000, 0.5f,
                null, 1500, 10, 20);
        tx = new Texture(Gdx.files.internal("player/fist.png"));
        this.txt = tx;
        txtPack = new TextureRegion[2];
        txtPack[0] = new TextureRegion(tx);
        this.x_offset = 1.25f;
        this.y_offset = 10f;
    }
}
