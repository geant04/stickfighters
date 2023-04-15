package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class Weapon {
    public int type = 0; // -1 for melee, 0 is pistol, 1 is shotgun, 2 is auto
    public float cooldown;
    public Texture txt;
    public float speed;

    public Weapon(int type, float cooldown, Texture txt, float speed) {
        this.type = type;
        this.cooldown = cooldown;
        this.txt = txt;
        this.speed = speed;
    }

    public void Attack(Pool<Bullet> bulletPool, Array<Bullet> activeBullets, Vector2 size, Vector2 origin){
        // obviously this will get overrided based on what weapon you're using
        Bullet item = bulletPool.obtain();
        item.init(origin.x, origin.y, txt, size, new Vector2((Player.flip ? -1 : 1), 0), speed);
        activeBullets.add(item);
    }

    public void Attack(){
        // melee attack, no clue what to add here
    }
}
