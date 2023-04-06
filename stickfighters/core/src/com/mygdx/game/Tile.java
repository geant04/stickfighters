package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Tile {
    private int id;
    private Texture[] txt;
    private Sprite sprite;
    private boolean CanCollide;

    public Tile(Texture[] txt){
        this.txt = txt;
        this.sprite = new Sprite(txt[0]);
        this.CanCollide = false;
        this.id = 0;
    }

    public Sprite getSprite(){
        return this.sprite;
    }
    public boolean isCanCollide(){ return CanCollide;}

    public void setTexture(int i){
        this.sprite = new Sprite(txt[i]);
    }

    // keep track of position?
    /*
    public void render(ShapeRenderer shape, SpriteBatch batch){
        batch.begin();
            // how does raycasting lighting work for my game?
        batch.end();

        shape.begin(ShapeRenderer.ShapeType.Filled); // draw line, rectangle
        shape.end();
    }
     */

    public void dispose(){
        this.dispose();
    }
}
