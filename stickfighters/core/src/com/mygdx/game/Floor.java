package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Floor extends Tile{
    private int id;
    private Texture txt;
    private Sprite sprite;
    private boolean CanCollide;
    TextureRegion[] states;

    public Floor(Texture txt){
        super(txt);
        this.txt = txt;
        TextureRegion[][] tmp = TextureRegion.split(txt,
                txt.getWidth() / 4,
                txt.getHeight() / 4);
        this.states = new TextureRegion[16];
        for(int i=0; i<4; i++){
            for(int j=0; j<4; j++){
                states[i+j] = tmp[i][j];
            }
        }
        this.sprite = new Sprite(txt);
        this.CanCollide = false;
        this.id = 0;
    }

    @Override
    public boolean isCanCollide() {
        return CanCollide;
    }
    @Override
    public void setTexture(int i){
        this.sprite = new Sprite(states[0]);
    }
}
