package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Tile {
    private int id;
    private Texture txt;
    private Sprite sprite;
    private boolean CanCollide;

    public Tile(Texture txt){
        this.txt = txt;
        this.sprite = new Sprite(txt);
        this.CanCollide = false;
        this.id = 0;
    }

    public Sprite getSprite(){
        return this.sprite;
    }
    public boolean isCanCollide(){ return CanCollide;}
    public int getId(){
        return id;
    }
    public void setTexture(Texture txt){
        this.sprite = new Sprite(txt);
    }

    public void dispose(){
        this.dispose();
    }
}
