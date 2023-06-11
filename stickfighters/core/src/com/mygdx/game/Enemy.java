package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.Weapons.Fist;

// MAKE THIS IMPLEMENT POOLABLE LATER SO THAT YOU CAN RE-USE ENEMIES WHEN THEY DIE!!
public class Enemy implements Pool.Poolable{
    private static Weapon EQUIPPED_GUN;
    private float adjustedWidth;
    public final Sprite hand;
    private int speed;
    private int width;
    private int height;
    private int radius = 15;
    private boolean flip = false;
    public float t;
    public float ogf;
    public float ogy;

    public float vx;
    public float vy;

    private float x;
    private float y;

    Animation<TextureRegion> walkAnimation;
    Animation<TextureRegion> idleAnimation;
    Animation<TextureRegion> currAnim;
    private Texture stick_sprite;
    float stateTime;

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
    private float x_offset;
    private float y_offset;
    private float initTime;
    private float cooldown;
    private boolean cool;
    private boolean anim;

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
        this.stateTime = 0f;

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


        this.EQUIPPED_GUN = new Fist();
        this.hand = new Sprite(EQUIPPED_GUN.txtPack[0]);
        this.hand.setSize(hand.getWidth() / 3f, hand.getHeight() / 3f);
        updateHand();
        this.adjustedWidth = hand.getWidth() * this.EQUIPPED_GUN.x_offset;
        this.t = 0.1f;
        this.x_offset = 1.0f;
        this.y_offset = 15f;
        this.ogf = x_offset;
        this.ogy = y_offset;
        this.cooldown = 1f;
        this.EQUIPPED_GUN.DAMAGE = 15f;
    }
    public void init(Vector2 origin){
        this.x = origin.x;
        this.y = origin.y;
        // hierarchy: HURT --> ATTACK --> FOLLOW --> IDLE
    }


    public float getX(){
        return this.x;
    }
    public float getY(){
        return this.y;
    }

    public void set(float x, float y){
        this.x = x;
        this.y = y;
    }

    public void updateHand(){
        Vector2 pivot = Pivot();
        float a = 2;
        float speed = 15;
        float y = pivot.y - this.EQUIPPED_GUN.y_offset;
        float rad = radius;
        if(vx != 0 || vy != 0){
            a = 3;
            speed = 20;
            y -= 4;
            rad += vx * -3;
        }
        hand.setX(pivot.x + rad);
        hand.setY((float) (y + a * Math.sin(stateTime * speed)));
    }
    public void setHealth(float health){
        this.HEALTH = (int) health;
    }

    public void setSpeed(int speed) { this.speed = speed; }

    public boolean isCollide(Bullet b){
        return this.x + width / 2f >= b.position.x + b.size.x &&
                this.x - width / 2f <= b.position.x &&
                this.y + height >= b.position.y &&
                this.y <= b.position.y;
        //return this.x < b.position.x + b.size.x && this.y < b.position.y + b.size.y
        //        && this.x + b.size.x > b.position.x && this.y + b.size.y > b.position.y;
    }

    public boolean isCollide(Player player, Vector2 origin, Vector2 size){
        return player.x < origin.x + size.x && player.y < origin.y + size.y
                && player.x + size.x > origin.x && player.y + size.y > origin.y;
    }

    public boolean isCollide(Vector2 origin, Vector2 size){
        return this.x < origin.x + size.x && this.y < origin.y + size.y
                && this.x + size.x > origin.x && this.y + size.y > origin.y;
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
        EQUIPPED_GUN.fire.play(0.3f);
        int dir = (flip ? -1 : 1);
        float box_height = height;
        float box_width = width / 2f;
        Vector2 org = new Vector2(this.x + box_width / 2 * dir, this.y);
        Vector2 size = new Vector2(box_width, box_height / 2);

        if(isCollide(target, org, size)){
            target.setHealth((int) (target.getHealth() - EQUIPPED_GUN.DAMAGE));
            target.HURT = true;
            System.out.println("ouch, health left: " + target.health);
            EQUIPPED_GUN.hit.play(0.3f);
        }

        EQUIPPED_GUN.y_offset += 10;
        this.t = 0.1f;
        this.initTime = this.cooldown;
        this.cool = true;
        this.anim = true;
        System.out.println("start, " + initTime);
    }

    public void update(){ // the AI system
        IDLE = true;
        FOLLOW = false;

        if(!ALIVE){
            return;
        }
        if(HURT){ // thanks fizzy
            DAMAGE_TIME += Gdx.graphics.getDeltaTime();
            if(Math.abs(force) >= 0.1){
                float decay = 120f;
                this.x += 10 * force * Gdx.graphics.getDeltaTime();
                if(force < 0){
                    decay *= -1;
                }
                force -= decay * Gdx.graphics.getDeltaTime();
                hand.setX(this.x + radius);
            }
            if(DAMAGE_TIME >= DAMAGE_DURATION){
                DAMAGE_TIME = 0;
                HURT = false;
            }
            return;
        }
        if(ATTACK){
            // do the anim
            radius = (flip ? -1 : 1) * (int) adjustedWidth;

            if(anim){
                t *= 1.30f;
                EQUIPPED_GUN.y_offset -= 1.4f * t;
                adjustedWidth = hand.getWidth() * ogf * t;
                if(EQUIPPED_GUN.y_offset <= ogy){
                    EQUIPPED_GUN.y_offset = ogy;
                }
                if(t >= 1.5f){
                    t = 1.75f;
                }
                if(EQUIPPED_GUN.y_offset <= ogy && t >= 1.5f){
                    anim = false;
                }
            }
            if(cool){
                initTime -= Gdx.graphics.getDeltaTime();
                if(this.initTime <= 0){
                    ATTACK = false;
                    return;
                }
                if(this.cooldown - this.initTime >= EQUIPPED_GUN.cooldown){
                    adjustedWidth = hand.getWidth() * ogf;
                }
            }
            updateHand();
            return;
        }
        double dist = Math.sqrt((target.getX() - x) * (target.getX() - x) +
                (target.getY() - y) * (target.getY() - y));
        if(dist < 300){ // follow state
            FOLLOW = true;
            track();
            float dx = vx * this.speed * Gdx.graphics.getDeltaTime();
            float dy = vy * this.speed * Gdx.graphics.getDeltaTime();
            this.x += dx;
            this.y += dy;

            if(dist < 40){
                System.out.println("Attack mode!");
                ATTACK = true;
                attack();
            }
        }

        int dir = flip ? -1 : 1;
        radius = dir * (int) adjustedWidth;
        updateHand();
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
            return animations.get(0);
        }
        if(FOLLOW){
            return animations.get(1);
        }
        return animations.get(0); //idle state
    }

    public Vector2 Pivot(){
        return new Vector2(getX(), (float) (getY() + this.height / 2.5));
    }
    public void render(SpriteBatch batch, float stateTime){
        float time = stateTime;
        this.stateTime = time;
        currAnim = getAnim();
        if(HURT){ // or if dead... will add this later
            time = DAMAGE_TIME;
            batch.setColor(Color.RED);
        }
        TextureRegion currentFrame = currAnim.getKeyFrame(time, true);
        //batch.begin();
        batch.draw(currentFrame, this.x, this.y, this.width / 2f, 0, this.width, this.height,
                (flip ? -1 : 1) * 1f, 1f, 0);

        hand.setScale((flip ? -1 : 1) * 1f, 1f);
        hand.setOrigin(width / 2f, 0);
        hand.draw(batch);

        batch.setColor(Color.WHITE);
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
