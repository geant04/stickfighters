package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class Weapon {
    public int type = 0; // -1 for melee, 0 is pistol, 1 is shotgun, 2 is auto
    public float cooldown;
    public Texture txt;
    public TextureRegion[] txtPack;
    public float speed;
    public int MAX_AMMO;
    public int AMMO;

    public float reloadTime;
    public float initTime;

    private boolean cool;
    public Weapon(){
        this(0, 0, 1, null, 1500, 10);
    }

    public Weapon(int type, float cooldown, float reloadTime, Texture txt, float speed, int MAX_AMMO) {
        this.type = type;
        this.cooldown = cooldown;
        this.reloadTime = reloadTime;
        this.initTime = reloadTime + 1;
        this.txt = txt;
        this.speed = speed;
        this.txtPack = new TextureRegion[2];
        this.MAX_AMMO = MAX_AMMO;
        this.AMMO = MAX_AMMO;
    }
    public void buildPack(){
        TextureRegion[][] tmp = TextureRegion.split(txt,
                txt.getWidth(), // adjustable later
                txt.getHeight() / 2);
        txtPack = new TextureRegion[2];
        for(int i = 0; i < txtPack.length; i++){
            txtPack[i] = tmp[i][0];
        }
    }
    public void Attack(Pool<Bullet> bulletPool, Array<Bullet> activeBullets, Vector2 size, Vector2 origin){
        // obviously this will get overrided based on what weapon you're using
        if((AMMO > 0 || MAX_AMMO == -1) && !cool){ // if MAX_AMMO == -1, there is infinite ammo
            Bullet item = bulletPool.obtain();
            item.init(origin.x, origin.y, txtPack[1], size, new Vector2((Player.flip ? -1 : 1), 0), speed);
            activeBullets.add(item);
            if(MAX_AMMO != -1){
                AMMO -= 1;
            }
            initTime = cooldown; // activate cooldown
            if(AMMO <= 0){
                initTime = reloadTime;
            }
            cool = true;
            return;
        }
        // reload now automatically
        if(initTime <= 0){
            if(cool){
                cool = false;
            }
            if(AMMO <= 0){
                AMMO = MAX_AMMO;
            }
        }
    }
    public void update(){
        if(cool){
            initTime -= Gdx.graphics.getDeltaTime();
        }
    }
    public void Attack(){
        // melee attack, no clue what to add here
    }

    public void dispose(){
        txt.dispose();
    }
}
