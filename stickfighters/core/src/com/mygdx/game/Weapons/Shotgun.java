package com.mygdx.game.Weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.Objects.Bullet;
import com.mygdx.game.Player;
import com.mygdx.game.Weapon;

public class Shotgun extends Weapon {
    public Texture tx;
    public Shotgun() {
        super(0, 0.6f, 2f,
                null, 3000, 5, 14);
        tx = new Texture(Gdx.files.internal("player/shotgun.png"));
        this.txt = tx;
        this.fire = Gdx.audio.newSound(Gdx.files.internal("sounds/shotgun.ogg"));
        super.buildPack();
        this.x_offset = 0.3f;
        this.y_offset = 15f;
        this.bullet_start = 5f;
        this.volume = 0.1f;
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
                        (float) (Math.random() * 3f + this.speed));
                activeBullets.add(item);
            }
            this.fire.play(this.volume);

            if(this.MAX_AMMO != -1){
                this.AMMO -= 1;
            }
            initTime = cooldown; // activate cooldown
            if(this.AMMO <= 0 && this.MAX_AMMO != -1){
                this.initTime = this.reloadTime;
            }
            this.cool = true;
        }
    }

    public void dispose(){
    }
}
