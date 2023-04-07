package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Wall extends Tile{
    private int id;
    private Texture txt;
    private Sprite sprite;
    private boolean CanCollide;
    private TextureRegion[] states;

    public Wall(Texture txt){
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
