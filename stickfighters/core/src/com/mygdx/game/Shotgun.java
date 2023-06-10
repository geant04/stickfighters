package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class Shotgun extends Weapon{
    public Texture tx;
    public Shotgun() {
        super(0, 0.6f, 2f,
                null, 2000, 20, 5);
        tx = new Texture(Gdx.files.internal("player/shotgun.png"));
        this.txt = tx;
        super.buildPack();
        this.x_offset = 0.3f;
        this.y_offset = 15f;
        this.bullet_start = 5f;
    }

    @Override
    public void Attack(Pool<Bullet> bulletPool, Array<Bullet> activeBullets, Vector2 size, Vector2 origin){
        if((this.AMMO > 0 || this.MAX_AMMO == -1) && !this.cool){ // if MAX_AMMO == -1, there is infinite ammo
            int RANGE = 5;
            for(int i = -1 * RANGE / 2; i < RANGE / 2; i++){
                Bullet item = bulletPool.obtain();
                item.setEndurance(2);
                item.setForce(35f);
                Vector2 dir = new Vector2();
                dir.x = Player.flip ? -1 : 1;
                dir.y = (float) (0.25 * Math.sin((i + Math.random()) / (2 * Math.PI)));
                item.init(origin.x, origin.y, txtPack[1], size, dir,
                        (float) (Math.random() * 2f + this.speed));
                activeBullets.add(item);
            }
            if(this.MAX_AMMO != -1){
                this.AMMO -= 1;
            }
            initTime = cooldown; // activate cooldown
            if(this.AMMO <= 0){
                this.initTime = this.reloadTime;
            }
            this.cool = true;
            return;
        }
        // reload now automatically
        if(this.initTime <= 0){
            if(this.cool){
                this.cool = false;
            }
            if(this.AMMO <= 0){
                this.AMMO = this.MAX_AMMO;
            }
        }
    }

    public void dispose(){
    }
}
