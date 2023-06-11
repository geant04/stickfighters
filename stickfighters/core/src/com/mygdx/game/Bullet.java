package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class Bullet implements Pool.Poolable{ // memory management good
    public float speed;
    public float force = 20f;
    public Vector2 dir;
    public Vector2 origin;
    public Vector2 position;
    public Vector2 size;
    private TextureRegion texture; // will have to adjust this later
    public boolean alive;
    public boolean hit;
    public int endurance = 1;
    public Sprite sprite;

    public Bullet(){
        this.origin = new Vector2();
        this.position = new Vector2();
        this.alive = false;
        this.hit = false;
    }

    public void init(float posX, float posY, TextureRegion texture,
                     Vector2 size, Vector2 dir, float speed) {
        origin.set(posX, posY);
        position.set(posX,  posY);
        this.speed = speed;
        this.dir = dir;
        this.texture = texture;
        this.sprite = new Sprite(texture);
        this.size = size;
        alive = true;
        System.out.println("start: " + posX + "," + posY);
    }

    public void setEndurance(int lives){
        // factors into bullet penetration
        this.endurance = lives;
    }
    public void setForce(float f){
        this.force = f;
    }


    public void update(){
        if(hit){
            if(endurance <= 0){
                alive = false;
                return;
            }
            endurance--;
            return;
        }
        if(distance() >= 250000){
            alive = false;
            return;
        }

        position.x += dir.x * speed * Gdx.graphics.getDeltaTime();
        position.y += dir.y * speed * Gdx.graphics.getDeltaTime();
    }
    public int distance(){
        int delta_x = (int) ((position.x - origin.x) * (position.x - origin.x));
        int delta_y = (int) ((position.y - origin.y) * (position.y - origin.y));
        return (delta_x + delta_y);
    }
    public Sprite getSprite(){
        return this.sprite;
    }
    @Override
    public void reset() {
        position.set(0,0);
        alive = false;
        hit = false;
    }
}
