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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.sun.javafx.geom.Shape;

public class Player {
    private final int speed;
    private int width;
    private int height;
    private boolean flip = false;

    private float vx = 1;
    private float vy = 1;

    private float x;
    private float y;

    private final Sprite hand;
    private Sprite body;

    private int hand_offset = 20;
    private int radius = 30;
    private Texture hand_texture;
    private Texture body_texture;
    private ShapeRenderer shapeRenderer;

    Animation<TextureRegion> walkAnimation;
    Animation<TextureRegion> idleAnimation;
    Animation<TextureRegion> currAnim;
    private Texture stick_sprite;
    float stateTime;

    public Player(int speed, int width, int height){
        hand_texture = new Texture(Gdx.files.internal("badlogic.jpg"));
        body_texture = new Texture(Gdx.files.internal("body.gif"));

        this.speed = speed;
        this.width = width;
        this.height = height;

        hand = new Sprite(hand_texture);
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

        this.x = width / 2;
        this.y = 200;
        this.width = this.width;
        this.height = this.height;

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
        hand.setX(pivot.x + radius);
        hand.setY(pivot.y);
    }

    public void update(){
        this.vx = 0;
        this.vy = 0;

        boolean deltaX = false;
        boolean deltaY = false;

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
        updateHand();
    }

    public Vector2 Pivot(){
        return new Vector2(getX() + hand_offset, (float) (getY() + this.height / 2.5));
    }

    public void render(ShapeRenderer shape, SpriteBatch batch, OrthographicCamera camera){
        Vector3 cords = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        shape.setProjectionMatrix(camera.combined);
        stateTime += Gdx.graphics.getDeltaTime();

        float mx = cords.x;
        float my = cords.y;
        Vector2 pivot = Pivot();

        float angle = (float) ((MathUtils.atan2(my - hand.getY(), mx - hand.getX()))
                * 180 / Math.PI);
        float turn_angle = (float) ((MathUtils.atan2(my - pivot.y, mx - pivot.x))
                * 180 / Math.PI);

        if(turn_angle > 90 || turn_angle < - 90){
            this.radius = -30;
            angle += 180;
        }else{
            this.radius = 30;
        }

        currAnim = idleAnimation;
        if(!(vx == 0 && vy == 0)){
            currAnim = walkAnimation;
        }
        TextureRegion currentFrame = currAnim.getKeyFrame(stateTime, true);

        batch.begin();
        hand.setOriginCenter();
        batch.draw(currentFrame, x, y, width / 2, 0, width, height,
                (flip ? -1 : 1) * 1f, 1f, 0); // Draw current frame at (50, 50)

        hand.setSize(width / 2, width / 2);
        hand.setRotation(angle);
        hand.draw(batch);
        batch.end();
    }

    public void dispose(){
        stick_sprite.dispose();
        shapeRenderer.dispose();
    }
}
