package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.mygdx.game.Objects.Ammo;
import com.mygdx.game.Objects.Bullet;
import com.mygdx.game.Weapons.Fist;
import com.mygdx.game.Weapons.Pistol;
import com.mygdx.game.Weapons.Rifle;
import com.mygdx.game.Weapons.Shotgun;

import static com.mygdx.game.Main.enemies;
import static com.mygdx.game.Main.activeBullets;
import static com.mygdx.game.Main.bulletPool;


public class Player {
    private float DAMAGE_TIME;
    private float DAMAGE_DURATION;
    private final int speed;
    public static int width;
    public static int height;
    public int health;
    public int MAX_HEALTH;
    public static boolean flip = false;
    public boolean HURT = false;
    public boolean DEAD = false;
    public boolean DEADANIM = false;

    public float vx = 1;
    public float vy = 1;

    public static float x;
    public static float y;

    private static Sprite hand;
    private int radius = 15;
    private static float adjustedWidth;

    private Texture box;
    private ShapeRenderer shapeRenderer;
    //public Array<Weapon> arms;
    public Weapon[] weapons;
    public int[] arms; // this is an int array keeping track of the arms indices
    public int eindx;
    public int arm_size;
    public static Weapon EQUIPPED_GUN;

    Animation<TextureRegion> walkAnimation;
    Animation<TextureRegion> idleAnimation;
    Animation<TextureRegion> deathAnimation;
    Animation<TextureRegion> currAnim;
    private Texture stick_sprite;
    float stateTime;

    public Player(int speed, int width, int height){
        //body_texture = new Texture(Gdx.files.internal("body.gif"));

        this.speed = speed;
        this.width = width;
        this.height = height;
        this.MAX_HEALTH = 100;
        this.health = MAX_HEALTH;
        this.DAMAGE_DURATION = 0.25f;
        this.DAMAGE_TIME = 0;

        this.stick_sprite = new Texture(Gdx.files.internal("sheet_test2.png"));

        TextureRegion[][] tmp = TextureRegion.split(stick_sprite, // get the animations
                stick_sprite.getWidth() / 6,
                stick_sprite.getHeight() / 3);

        TextureRegion[] idle = new TextureRegion[4];
        TextureRegion[] walkFrames = new TextureRegion[6];
        TextureRegion[] death = new TextureRegion[6];
        for(int i=0 ; i<6; i++){ //load walk, idle
            walkFrames[i] = tmp[1][i];
            death[i] = tmp[2][i];
            if(i<4){
                idle[i]= tmp[0][i];
            }
        }
        walkAnimation = new Animation<TextureRegion>(0.07f, walkFrames);
        deathAnimation = new Animation<TextureRegion>(0.17f, death);
        idleAnimation = new Animation<TextureRegion>(0.05f, idle);
        currAnim = idleAnimation;

        stateTime = 0f;

        this.x = width / 2f;
        this.y = 200;
        this.weapons = new Weapon[100]; // 100 inventory size
        weapons[0] = new Fist();
        weapons[1] = new Pistol();
        weapons[2] = new Rifle();
        weapons[3] = new Shotgun();

        this.arm_size = 4;
        this.eindx = 0;
        this.arms = new int[]{0, 1, 2, 3};

        this.EQUIPPED_GUN = weapons[arms[eindx]];
        hand = new Sprite(EQUIPPED_GUN.txtPack[0]);
        hand.setSize(hand.getWidth() / 3f, hand.getHeight() / 3f);
        updateHand();
        adjustedWidth = hand.getWidth() * this.EQUIPPED_GUN.x_offset;
        shapeRenderer = new ShapeRenderer();
    }

    // some setters/getters
    public float getAbsoluteX(){
        return this.x + (width / 2f);
    }
    public float getAbsoluteY(){
        return this.y + (height / 2f);
    }

    public float getX(){
        return this.x;
    }
    public float getY(){
        return this.y;
    }
    public int getSpeed() {
        return this.speed;
    }
    public float getVx() {
        return this.vx;
    }
    public float getVy() {
        return this.vy;
    }
    public float getHeight(){
        return this.height;
    }
    public float getWidth(){
        return this.width;
    }
    public void set(float x, float y){
        this.x = x;
        this.y = y;
    }

    public void setHealth(int h){
        health = h;
    }
    public int getHealth(){
        return health;
    }

    // --------------------
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

    public void update(){ // call every tick
        int dir;
        this.vx = 0;
        this.vy = 0;

        if(DEAD){
            return;
        }

        if(DEADANIM){
            DAMAGE_TIME += Gdx.graphics.getDeltaTime();
            if(DAMAGE_TIME >= 0.17f * 6){
                DEAD = true;
                DEADANIM = false;
            }
            return;
        }

        if(getHealth() <= 0){
            DEADANIM = true;
            return;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.A)) { // movement
            vx = -1;
            flip = true;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            vy = -1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            vx = 1;
            flip = false;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            vy = 1;
        }
        dir = flip ? -1 : 1;
        radius = dir * (int) adjustedWidth;
        updateHand();

        // this whole part should probably be refactored

        int type = EQUIPPED_GUN.type; // save this so we can do some weapon swapping stuff
        if(Gdx.input.isKeyJustPressed(Input.Keys.I)){ // change weapon
            eindx = (eindx + 1) % arm_size;
            System.out.println("change gun to " + eindx);

            EQUIPPED_GUN = weapons[arms[eindx]];
            hand = new Sprite(EQUIPPED_GUN.txtPack[0]);
            hand.setSize(hand.getWidth() / 3f, hand.getHeight() / 3f); //1.25f i think for fist
            adjustedWidth = hand.getWidth() * this.EQUIPPED_GUN.x_offset;
            updateHand(); // change the position of the hand
        }
        if(type == 1){ // automatic
            if(Gdx.input.isKeyPressed(Input.Keys.J)){ // is held
                EQUIPPED_GUN.Attack(bulletPool, activeBullets, new Vector2(20, 20),
                        new Vector2(x - adjustedWidth, getY() + 30));
            }//EQUIPPED_GUN.bullet_start * radius - width / 2f
        }
        if(type != 1 && Gdx.input.isKeyJustPressed(Input.Keys.J)){ // press, not hold
            EQUIPPED_GUN.Attack(bulletPool, activeBullets, new Vector2(20, 20),
                    new Vector2(x - adjustedWidth, getY() + 30));
        }

        for(int i=0; i<arm_size; i++){
            weapons[arms[i]].update(); // be responsible for cooldowns going down + updating guns
        }

        if(HURT){ // thanks fizzy
            DAMAGE_TIME += Gdx.graphics.getDeltaTime();
            if(DAMAGE_TIME >= DAMAGE_DURATION){
                DAMAGE_TIME = 0;
                HURT = false;
            }
        }
    }

    public Vector2 Pivot(){
        return new Vector2(getX(), (float) (getY() + this.height / 2.5));
    }

    public static void setAdjustedWidth(float newOffset){
        adjustedWidth = hand.getWidth() * newOffset;
    }

    public boolean isCollide(Ammo b){
        float x1 = getAbsoluteX() - (width / 2f);
        float x2 = getAbsoluteX() + (width / 2f);
        boolean xright = (x2 <= b.position.x + b.size.x && x2 >= b.position.x);
        boolean xleft = (x1 <= b.position.x + b.size.x && x1 >= b.position.x);
        
        float y1 = getAbsoluteY() - (height / 2f);
        boolean ybottom = (y1 <= b.position.y + b.size.y && y1 >= b.position.y);
        
        return (xright || xleft) && (ybottom);
    }

    public void render(SpriteBatch batch, float stateTime){
        this.stateTime = stateTime;
        float time = stateTime;
        //shape.setProjectionMatrix(camera.combined);
        currAnim = idleAnimation;
        if(!(vx == 0 && vy == 0)){
            currAnim = walkAnimation;
        }
        if(!DEADANIM && HURT){ // or if dead... will add this later
            batch.setColor(Color.RED);
        }
        if(DEADANIM){
            currAnim = deathAnimation;
            time = DAMAGE_TIME;
        }// Draw current frame at (50, 50)
        if(!DEAD){
            TextureRegion currentFrame = currAnim.getKeyFrame(time, true);
            batch.draw(currentFrame, x, y, width / 2f, 0, width, height,
                    (flip ? -1 : 1) * 1f, 1f, 0);
            if(!DEADANIM){
                hand.setScale((flip ? -1 : 1) * 1f, 1f);
                hand.setOrigin(width / 2f, 0);
                hand.draw(batch);
            }
        }
        batch.setColor(Color.WHITE);
        //batch.end();
    }
    public void dispose(){
        stick_sprite.dispose();
        shapeRenderer.dispose();
    }
}
