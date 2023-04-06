package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Floor extends Tile{
    private int id;
    private Texture[] txt;
    private Sprite sprite;
    private boolean CanCollide;

    public Floor(Texture[] txt){
        super(txt);
        this.txt = txt;
        this.sprite = new Sprite(txt[0]);
        this.CanCollide = false;
    }
}
