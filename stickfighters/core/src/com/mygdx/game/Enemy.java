package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

// MAKE THIS IMPLEMENT POOLABLE LATER SO THAT YOU CAN RE-USE ENEMIES WHEN THEY DIE!!
public class Enemy {
    private int speed;
    private int width;
    private int height;
    public static boolean flip = false;

    public float vx;
    public float vy;

    private float x;
    private float y;

    Animation<TextureRegion> walkAnimation;
    Animation<TextureRegion> idleAnimation;
    Animation<TextureRegion> currAnim;
    private Texture stick_sprite;

    // STATES
    public boolean FOLLOW;
    public boolean IDLE;
    public boolean HURT;
    public boolean ATTACK;
    public boolean ALIVE;
    public int HEALTH;
    public int MAX_HEALTH;
    public float DAMAGE_DURATION;
    public float DAMAGE_TIME;
    private Array<Animation<TextureRegion>> animations;
    private Player target;

    public Enemy(int speed, int width, int height, Vector2 origin, int MAX_HEALTH){
        this.speed = speed;
        this.width = width;
        this.height = height;
        this.x = origin.x;
        this.y = origin.y;
        this.vy = 0;
        this.vx = 0;

        this.FOLLOW = false;
        this.IDLE = true;
        this.HURT = false;
        this.ATTACK = false;
        this.ALIVE = true;
        this.MAX_HEALTH = MAX_HEALTH;
        this.HEALTH = MAX_HEALTH;
        this.DAMAGE_DURATION = 0.5f;
        this.DAMAGE_TIME = 0;

        // hierarchy: HURT --> ATTACK --> FOLLOW --> IDLE

        stick_sprite = new Texture(Gdx.files.internal("sheet_test.png"));

        TextureRegion[][] tmp = TextureRegion.split(stick_sprite,
                stick_sprite.getWidth() / 6,
                stick_sprite.getHeight() / 2);

        TextureRegion[] idle = new TextureRegion[4];
        TextureRegion[] walkFrames = new TextureRegion[6];
        for(int i=0 ; i<6; i++){ //load walk, idle
            walkFrames[i] = tmp[1][i];
            if(i<4){
                idle[i]= tmp[0][i];
            }
        }
        walkAnimation = new Animation<TextureRegion>(0.07f, walkFrames);
        idleAnimation = new Animation<TextureRegion>(0.05f, idle);
        animations = new Array<>();
        animations.add(idleAnimation, walkAnimation);
        currAnim = animations.get(0);
        target = Main.player;
    }

    public void set(float x, float y){
        this.x = x;
        this.y = y;
    }

    public void setHealth(int health){
        this.HEALTH = health;
    }

    public boolean isCollide(Bullet b){
        return x < b.position.x + b.size.x && y < b.position.y + b.size.y
                && x + b.size.x > b.position.x && y + b.size.y > b.position.y;
    }

    public void damage(int amt){
        if(this.HEALTH - amt < 0){ // dead state
            this.ALIVE = false;
            //setHealth(this.MAX_HEALTH);
            return;
        }
        setHealth(this.HEALTH - amt); // have the pop-up for damage text
        System.out.printf("hit for %d!, health left: %d\n", amt, HEALTH);
        DAMAGE_TIME = 0;
        HURT = true;
    }

    public void update(){
        IDLE = true;
        FOLLOW = false;
        ATTACK = false;

        if(HURT){ // thanks fizzy
            DAMAGE_TIME += Gdx.graphics.getDeltaTime();
            if(DAMAGE_TIME >= DAMAGE_DURATION){
                DAMAGE_TIME = 0;
                HURT = false;
            }
            return;
        }
        double dist = Math.sqrt((target.getX() - x) * (target.getX() - x) +
                (target.getY() - y) * (target.getY() - y));
        if(dist < 500){
            FOLLOW = true;
            track();

            float dx = vx * speed * Gdx.graphics.getDeltaTime();
            float dy = vy * speed * Gdx.graphics.getDeltaTime();

            x += dx;
            y += dy;
        }
    }

    public void track(){ // called if state isn't IDLE
        // goal is to adjust vx and vy accordingly such that the unit vector points in the direction of the player
        // find delta x, find delta y, then normalize their values
        float xDir = target.getX() - x;
        float yDir = target.getY() - y;
        float magnitude = (float) Math.sqrt(xDir * xDir + yDir * yDir); // in theory should work
        vx = xDir / magnitude;
        vy = yDir / magnitude;
    }

    public Animation<TextureRegion> getAnim(){
        if(HURT){
            return animations.get(1);
        }
        if(ATTACK){
            return animations.get(1);
        }
        if(FOLLOW){
            return animations.get(1);
        }
        return animations.get(0); //idle state
    }

    public void render(SpriteBatch batch, float stateTime){
        float time = stateTime;
        currAnim = getAnim();
        if(HURT){
            time = DAMAGE_TIME;
        }
        TextureRegion currentFrame = currAnim.getKeyFrame(time, true);
        //batch.begin();
        batch.draw(currentFrame, x, y, width / 2, 0, width, height,
                (flip ? -1 : 1) * 1f, 1f, 0);
        //batch.end();
    }
    public void dispose(){
        stick_sprite.dispose();
    }
}
