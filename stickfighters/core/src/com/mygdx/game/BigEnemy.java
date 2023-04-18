package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;


public class BigEnemy extends Enemy{
    private float x;
    private float y;

    public BigEnemy(){
        super(100, (int) (50 * 1.5), (int) (70 * 1.5), 300);
    }
    @Override
    public void init(Vector2 origin){
        this.x = origin.x;
        this.y = origin.y;
    }
}
