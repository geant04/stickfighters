package com.mygdx.game.Enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class Grunk implements Pool.Poolable{
    private float x;
    private float y;
    private int speed;
    private int width;
    private int height;
    private int radius = 15;
    
    public int HEALTH;
    public int MAX_HEALTH;
    public float DAMAGE_DURATION;
    public float DAMAGE_TIME;
    public float force;

    Animation<TextureRegion> idleAnimation;
    private Array<Animation<TextureRegion>> animations;

    // STATES
    public boolean FOLLOW;
    public boolean IDLE;
    public boolean HURT;
    public boolean ATTACK;
    public boolean ALIVE;
    public boolean DEADANIM;

    private Texture stickSprite;
    
    public Grunk(){
        this(110, 50, 70, 100);
    }

    public Grunk(int speed, int width, int height, int MAX_HEALTH){
        this.speed = speed;
        this.width = width;
        this.height = height;
        //this.vy = 0;
        //this.vx = 0;
        this.FOLLOW = false;
        this.IDLE = true;
        this.HURT = false;
        this.ATTACK = false;
        this.ALIVE = true;

        stickSprite = new Texture(Gdx.files.internal("grunk/grunk.png"));

        TextureRegion[][] tmp = TextureRegion.split(stickSprite, // get the animations
                stickSprite.getWidth() / 4,
                stickSprite.getHeight());

        TextureRegion[] idle = new TextureRegion[4];
        for(int i=0 ; i<4; i++){ //load walk, idle
            //walkFrames[i] = tmp[1][i];
            //death[i] = tmp[2][i];
            //if(i<4){
            idle[i]= tmp[0][i];
            //}
        }
        idleAnimation = new Animation<TextureRegion>(0.05f, idle);
        
        animations = new Array<>();
        animations.add(idleAnimation);
    }
    
    public void init(Vector2 origin){
        this.x = origin.x;
        this.y = origin.y;
        // hierarchy: HURT --> ATTACK --> FOLLOW --> IDLE
    }

    @Override
    public void reset() {
        x = 0;
        y = 0;
        ALIVE = false;
        DEADANIM = false;
    }
}
