package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Floor extends Tile {
    private int id;
    private Texture txt;
    private Sprite sprite;
    private boolean CanCollide;
    TextureRegion[] states;

    public Floor(Texture txt){
        super(txt);
        this.txt = txt;
        this.sprite = new Sprite(txt);
        this.CanCollide = false;
        this.id = 0;
    }

    @Override
    public boolean isCanCollide() {
        return CanCollide;
    }
    @Override
    public void setTexture(Texture txt){
        this.sprite = new Sprite(txt);
    }
}
