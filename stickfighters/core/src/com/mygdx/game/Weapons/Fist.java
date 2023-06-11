package com.mygdx.game.Weapons;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.Bullet;
import com.mygdx.game.Enemy;
import com.mygdx.game.Player;
import com.mygdx.game.Weapon;

import static com.mygdx.game.Main.enemies;
import static com.mygdx.game.Player.setAdjustedWidth;

public class Fist extends Weapon{
    public Texture tx;
    private float t;
    private float ogf;
    private float ogy;
    private boolean anim;
    public Fist() {
        super(-1, 5f / 30, 0.5f,
                null, 1500, 10, 7);
        tx = new Texture(Gdx.files.internal("player/fist.png"));
        this.fire = Gdx.audio.newSound(Gdx.files.internal("sounds/swing.ogg"));
        this.hit = Gdx.audio.newSound(Gdx.files.internal("sounds/punch.ogg"));
        this.txt = tx;
        txtPack = new TextureRegion[2];
        txtPack[0] = new TextureRegion(tx);
        this.x_offset = 1.0f;
        this.y_offset = 15f;
        this.t = 0.1f;
        this.ogf = x_offset;
        this.ogy = y_offset;
    }

    @Override
    public void Attack(Pool<Bullet> bp, Array<Bullet> ab, Vector2 s, Vector2 o){
        // melee attack, no clue what to add here
        // the hitbox ranges are for y: origin.y +/- box_height/2
        // for x : origin.x to origin.x + box_width
        if(!cool){
            int dir = (Player.flip ? -1 : 1);
            float box_height = Player.height;
            float box_width = Player.width / 2f;
            Vector2 org = new Vector2(Player.x + box_width / 2 * dir, Player.y);
            Vector2 size = new Vector2(box_width, box_height / 2);
            this.fire.play(0.3f);

            boolean hit = false;
            for(Enemy e : enemies){
                if(e.isCollide(org, size)){
                    e.damage(20, dir * 35f);
                    hit = true;
                }
            }
            if(hit){
                this.hit.play(0.3f);
            }
            this.y_offset += 10;
            this.t = 0.1f;
            this.initTime = this.cooldown;
            this.cool = true;
            this.anim = true;
        }
    }

    @Override
    public void update(){
        if(anim){
            t *= 1.30f;
            y_offset -= 1.4f * t;
            setAdjustedWidth(ogf * t);
            if(y_offset <= ogy){
                y_offset = ogy;
            }
            if(t >= 1.5f){
                t = 1.75f;
            }
            if(y_offset <= ogy && t >= 1.5f){
                anim = false;
            }
        }
        if(cool){
            initTime -= Gdx.graphics.getDeltaTime();
            if(this.initTime <= 0){
                setAdjustedWidth(ogf);
                if(this.cool){
                    this.cool = false;
                }
                if(this.AMMO <= 0){
                    this.AMMO = this.MAX_AMMO;
                }
            }
        }
    }
}
