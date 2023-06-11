package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Wall extends Tile {
    private int id;
    private Texture txt;
    private Sprite sprite;
    private boolean CanCollide;

    public Wall(Texture txt){
        super(txt);
        this.txt = txt;
        this.sprite = new Sprite(txt);
        this.CanCollide = true;
        this.id = 1;
    }
    @Override
    public boolean isCanCollide() {
        return CanCollide;
    }
    @Override
    public int getId(){
        return id;
    }

}
