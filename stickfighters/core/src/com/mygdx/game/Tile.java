package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Tile {
    private int id;
    private Texture txt;
    private Sprite sprite;
    private boolean CanCollide;
    TextureRegion[] states;

    public Tile(Texture txt){
        this.txt = txt;
        TextureRegion[][] tmp = TextureRegion.split(txt,
                txt.getWidth() / 4,
                txt.getHeight() / 4);
        this.states = new TextureRegion[16];
        for(int i=0; i<4; i++){
            for(int j=0; j<4; j++){
                states[(i*4)+j] = tmp[i][j];
            }
        }
        this.sprite = new Sprite(states[0]);
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
    public void setTexture(int i){
        this.sprite = new Sprite(states[i]);
    }

    public void dispose(){
        this.dispose();
    }
}
