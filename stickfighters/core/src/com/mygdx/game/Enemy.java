package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

// MAKE THIS IMPLEMENT POOLABLE LATER SO THAT YOU CAN RE-USE ENEMIES WHEN THEY DIE!!
public class Enemy implements Pool.Poolable{
    private int speed;
    private int width;
    private int height;
    private boolean flip = false;

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
    public float force;
    private Array<Animation<TextureRegion>> animations;
    private Player target;

    public Enemy(){
        this(100, 50, 70, 100);
    }
    public Enemy(int speed, int width, int height, int MAX_HEALTH){
        this.speed = speed;
        this.width = width;
        this.height = height;
        this.vy = 0;
        this.vx = 0;
        this.FOLLOW = false;
        this.IDLE = true;
        this.HURT = false;
        this.ATTACK = false;
        this.ALIVE = true;
        this.MAX_HEALTH = MAX_HEALTH;
        this.HEALTH = MAX_HEALTH;
        this.DAMAGE_DURATION = 0.25f;
        this.DAMAGE_TIME = 0;
        this.force = 0f;

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
    public void init(Vector2 origin){
        this.x = origin.x;
        this.y = origin.y;
        // hierarchy: HURT --> ATTACK --> FOLLOW --> IDLE
    }

    public void set(float x, float y){
        this.x = x;
        this.y = y;
    }

    public void setHealth(float health){
        this.HEALTH = (int) health;
    }

    public boolean isCollide(Bullet b){
        return this.x < b.position.x + b.size.x && this.y < b.position.y + b.size.y
                && this.x + b.size.x > b.position.x && this.y + b.size.y > b.position.y;
    }

    public void damage(float amt, float force){ // force should be like some mathematical bs
        this.force = force;
        System.out.printf("hit for %.3f!, health left: %.3f\n", amt, HEALTH - amt);
        if(this.HEALTH - amt <= 0){ // dead state
            this.ALIVE = false;
            //setHealth(this.MAX_HEALTH);
            return;
        }
        setHealth(this.HEALTH - amt); // have the pop-up for damage text
        DAMAGE_TIME = 0;
        HURT = true;
    }

    public void attack(){

    }

    public void update(){
        IDLE = true;
        FOLLOW = false;
        ATTACK = false;

        if(!ALIVE){
            return;
        }
        if(HURT){ // thanks fizzy
            DAMAGE_TIME += Gdx.graphics.getDeltaTime();
            if(Math.abs(force) >= 0.1){
                float decay = 120f;
                x += 10 * force * Gdx.graphics.getDeltaTime();
                if(force < 0){
                    decay *= -1;
                }
                force -= decay * Gdx.graphics.getDeltaTime();
            }
            if(DAMAGE_TIME >= DAMAGE_DURATION){
                DAMAGE_TIME = 0;
                HURT = false;
            }
            return;
        }
        double dist = Math.sqrt((target.getX() - x) * (target.getX() - x) +
                (target.getY() - y) * (target.getY() - y));
        if(dist < 300){
            FOLLOW = true;
            track();
            float dx = vx * this.speed * Gdx.graphics.getDeltaTime();
            float dy = vy * this.speed * Gdx.graphics.getDeltaTime();
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
        flip = xDir < 0;
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
        if(HURT){ // or if dead... will add this later
            time = DAMAGE_TIME;
        }
        TextureRegion currentFrame = currAnim.getKeyFrame(time, true);
        //batch.begin();
        batch.draw(currentFrame, this.x, this.y, this.width / 2, 0, this.width, this.height,
                (flip ? -1 : 1) * 1f, 1f, 0);
        //batch.end();
    }
    public void dispose(){
        stick_sprite.dispose();
    }
    @Override
    public void reset() {
        x = 0;
        y = 0;
        ALIVE = false;
    }
}
