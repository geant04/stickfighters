package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.Objects.Bullet;

/*
    ID CLASSIFICATION CHART (YOU MUST READ THIS):
        -1 : MELEE
         0 : ONE-CLICK -- SEMI-AUTO (sniper, pistol, shotgun, missile strike)
         1 : HOLD-DOWN -- AUTO (rifle, machine gun, flamethrower, laser-ray)

    PLAN ON DOING: rifle class, extends weapon class by having id=1 <- not hard at all
    PLAN ON DOING: shotgun class, extends weapon class by having id=0 <- maybe a little harder
    PLAN ON DOING: sword class, extends weapon class by having id=-1 <- completely new mechanic
 */
public class Weapon {
    public int type = 0;
    public float cooldown;
    public Texture txt;
    public TextureRegion[] txtPack;
    public float speed;
    public int MAX_AMMO;
    public int AMMO;
    public int AMMO_START;
    public float DAMAGE;
    public float reloadTime;
    public float initTime;
    public boolean cool;
    public float x_offset;
    public float y_offset;
    public float bullet_start;
    public Sound fire;
    public float volume;
    public Sound hit;

    public Weapon(){
        this(0, 0, 1, null, 1500, 10, 20);
    }

    public Weapon(int type, float cooldown, float reloadTime,
                  Texture txt, float speed, int AMMO_START, float DAMAGE) {
        this.type = type;
        this.cooldown = cooldown;
        this.reloadTime = reloadTime;
        this.initTime = reloadTime + 1;
        this.txt = txt;
        this.speed = speed;
        this.txtPack = new TextureRegion[2];
        this.MAX_AMMO = 999;
        this.AMMO_START = AMMO_START;
        this.AMMO = AMMO_START;
        this.DAMAGE = DAMAGE;
        this.x_offset = 0.8f;
        this.y_offset = 20f;
        this.bullet_start = 3f;
        this.volume = 1f;
    }

    // splits our textures so that we can work with stuff well... I THINK THAT'S WHAT THIS DOES
    public void buildPack(){
        TextureRegion[][] tmp = TextureRegion.split(txt,
                txt.getWidth(),
                txt.getHeight() / 2); // exactly cuts in half
        txtPack = new TextureRegion[2];
        for(int i = 0; i < txtPack.length; i++){
            txtPack[i] = tmp[i][0];
        }
        txtPack[1] = new TextureRegion(txt, 0, txt.getHeight()/2, 114, txt.getHeight()/2);
    }
    public void Attack(Pool<Bullet> bulletPool, Array<Bullet> activeBullets, Vector2 size, Vector2 origin){
        if((AMMO > 0 || MAX_AMMO == -1) && !cool){ // if MAX_AMMO == -1, there is infinite ammo
            this.fire.play(this.volume);
            Bullet item = bulletPool.obtain();
            item.init(origin.x, origin.y, txtPack[1], size, new Vector2((Player.flip ? -1 : 1), 0), speed);
            activeBullets.add(item);

            if(MAX_AMMO != -1){
                AMMO -= 1;
            }
            initTime = cooldown; // activate cooldown
            //if(AMMO <= 0 && MAX_AMMO != -1){
              //  initTime = reloadTime;
            //}
            cool = true;
        }
    }
    public void update(){
        if(cool){
            initTime -= Gdx.graphics.getDeltaTime();
            if(this.initTime <= 0){
                if(this.cool){
                    this.cool = false;
                }
            }
        }
    }

    public void dispose(){
        txt.dispose();
    }
}
