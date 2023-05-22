package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

import static com.mygdx.game.Main.enemies;
import static com.mygdx.game.Main.enemyPool;

public class Player {
    private final int speed;
    private int width;
    private int height;
    public static boolean flip = false;

    public float vx = 1;
    public float vy = 1;

    private float x;
    private float y;

    private final Sprite hand;
    private int hand_offset = 0;
    private int radius = 15;
    private Texture body_texture;
    private ShapeRenderer shapeRenderer;
    public Array<Weapon> arms;
    public Weapon EQUIPPED_GUN;

    Animation<TextureRegion> walkAnimation;
    Animation<TextureRegion> idleAnimation;
    Animation<TextureRegion> currAnim;
    private Texture stick_sprite;
    float stateTime;

    private final Array<Bullet> activeBullets = new Array<Bullet>();
    private final Pool<Bullet> bulletPool = new Pool<Bullet>() {
        @Override
        protected Bullet newObject() {
            return new Bullet();
        }
    };

    public Player(int speed, int width, int height){
        //body_texture = new Texture(Gdx.files.internal("body.gif"));

        this.speed = speed;
        this.width = width;
        this.height = height;

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
        currAnim = idleAnimation;

        stateTime = 0f;

        this.x = width / 2f;
        this.y = 200;
        this.arms = new Array<>();
        this.arms.add(new Pistol());
        this.arms.add(new Rifle());
        this.arms.add(new Shotgun());

        this.EQUIPPED_GUN = arms.get(0);
        hand = new Sprite(EQUIPPED_GUN.txtPack[0]);
        hand.setSize(hand.getWidth() / 2.2f, hand.getHeight() / 2.2f);
        updateHand();

        shapeRenderer = new ShapeRenderer();
    }

    // some setters/getters

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

    // --------------------
    public void updateHand(){
        Vector2 pivot = Pivot();
        float a = 2;
        float speed = 15;
        float y = pivot.y - 10;
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

    public void update(){
        Vector2 pivot = Pivot();
        Bullet item;
        Enemy enemy;
        if(Gdx.input.isKeyJustPressed(Input.Keys.I)){ // change weapon
            int indx = (arms.indexOf(EQUIPPED_GUN, false) + 1) % arms.size;
            System.out.println("change gun to " + indx);
            EQUIPPED_GUN = arms.get(indx);
        }
        if(EQUIPPED_GUN.type == 1){ // automatic
            if(Gdx.input.isKeyPressed(Input.Keys.J)){
                EQUIPPED_GUN.Attack(bulletPool, activeBullets, new Vector2(20, 20),
                        new Vector2(pivot.x + radius, getY() + 10));
            }
        }
        if(EQUIPPED_GUN.type != 1 &&
                Gdx.input.isKeyJustPressed(Input.Keys.J)){
            EQUIPPED_GUN.Attack(bulletPool, activeBullets, new Vector2(20, 20),
                    new Vector2(pivot.x + radius, getY() + 10));
        }
        for (int i = activeBullets.size; --i >= 0;) {
            item = activeBullets.get(i);
            if (!item.alive) {
                activeBullets.removeIndex(i);
                Pools.free(item);
            }
        }
        for (int i = enemies.size; --i >= 0;) {
            enemy = enemies.get(i);
            if (!enemy.ALIVE) {
                enemies.removeIndex(i);
                Pools.free(enemy);
            }
        }
        this.vx = 0;
        this.vy = 0;

        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
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
        radius = (flip ? -110 : 30);
        updateHand();
        for(Bullet b : activeBullets){
            for(Enemy e : enemies){
                if(e.isCollide(b)){
                    b.hit= true; // makes the bullet unalive itself
                    e.damage(EQUIPPED_GUN.DAMAGE, (flip ? -1 : 1) * b.force);
                }
            }
            b.update();
        }
        for(Weapon w : arms){
            w.update();
        }
    }

    public Vector2 Pivot(){
        return new Vector2(getX(), (float) (getY() + this.height / 2.5));
    }

    public void render(ShapeRenderer shape, SpriteBatch batch, OrthographicCamera camera, float stateTime){
        this.stateTime = stateTime;
        shape.setProjectionMatrix(camera.combined);
        currAnim = idleAnimation;
        if(!(vx == 0 && vy == 0)){
            currAnim = walkAnimation;
        }
        TextureRegion currentFrame = currAnim.getKeyFrame(stateTime, true);

        //batch.begin();
        //hand.setOriginCenter();
        batch.draw(currentFrame, x, y, width / 2f, 0, width, height,
                (flip ? -1 : 1) * 1f, 1f, 0); // Draw current frame at (50, 50)

        hand.setScale((flip ? -1 : 1) * 1f, 1f);
        hand.draw(batch);
        for(Bullet b : activeBullets){
            Sprite sprite = b.getSprite();
            sprite.setPosition(b.position.x, b.position.y + width/2f);
            sprite.setScale(b.dir.x < 0 ? -1 : 1, 1f);
            sprite.setSize(b.size.x, b.size.y);
            b.getSprite().draw(batch);
        }
        //batch.end();
    }
    public void dispose(){
        stick_sprite.dispose();
        shapeRenderer.dispose();
    }
}
