package com.mygdx.game.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.Main;
import com.mygdx.game.Player;
import com.mygdx.game.Weapon;

public class Ammo implements Pool.Poolable{ // memory management good
    public Vector2 origin;
    public Vector2 position;
    public Vector2 size;
    public boolean alive;
    public boolean hit;
    public float elapsed = 0f;
    public float timeSine = (float) Math.sin(elapsed * 3.5f);
    public float yOG = 0f;
    public Sprite sprite;

    public Ammo(){
        this.origin = new Vector2();
        this.position = new Vector2();
        this.alive = false;
        this.hit = false;
    }

    public void init(float posX, float posY, Texture texture) {
        System.out.println("spawned");
        origin.set(posX, posY);
        position.set(posX,  posY);
        this.yOG = posY;
        this.sprite = new Sprite(texture);
        this.size = new Vector2(40,40);
        alive = true;
    }

    public float getX(){
        return this.position.x;
    }
    
    public float getY(){
        return this.position.y;
    }

    public float getOGY(){
        return this.yOG;
    }

    public float getWidth(){
        return this.size.x;
    }

    public float getSine(){
        return this.timeSine * 0.2f;
    }

    // for now, ammo boxes replenish all the ammo, allowing the user to figure out how to manage their ammo
    // i'll just change it later on based on stuff y'know
    public void update(){
        if(hit){
            System.out.println("got extra ammo!");
            Player p = Main.player;
            for(int i = 0; i < p.arm_size; i++){
                int idx = p.arms[i];
                Weapon w = p.weapons[idx];
                w.AMMO = Math.min(w.MAX_AMMO, w.AMMO + Math.min(w.MAX_AMMO - w.AMMO_START, w.AMMO_START));
            }
            alive = false;
            return;
        }
        if(elapsed >= 60f){
            System.out.println("took too long");
            alive = false;
            return;
        }
        elapsed += Gdx.graphics.getDeltaTime();
        timeSine = (float) Math.sin(elapsed * 3.5f);
        position.set(position.x,  position.y + timeSine * 0.2f);
    }
    public void render(Batch batch){
        Sprite sprite = this.getSprite();
        sprite.setPosition(this.position.x, this.position.y);
        sprite.setSize(size.x, size.y);
        sprite.draw(batch);
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
