package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    private int speed;
    private final Vector2 dir;
    private final Vector2 size;
    private final Vector2 origin;
    private Texture texture; // will have to adjust this later
    private Rectangle fig;

    public Bullet(int speed, Vector2 dir, Vector2 size, Vector2 origin, Texture texture){
        this.speed = speed;
        this.dir = dir;
        this.size = size;
        this.origin = origin;
        this.texture = texture;

        this.fig = new Rectangle(origin.x, origin.y, size.x, size.y);
        // determine the angle based on the direction, perhaps; do that later
    }

    public void update(){
        fig.x += dir.x * speed * Gdx.graphics.getDeltaTime();
        fig.y += dir.y * speed * Gdx.graphics.getDeltaTime();
    }

    public float distance(){
        double delta_x = (fig.x - origin.x) * (fig.x - origin.x);
        double delta_y = (fig.y - origin.y) * (fig.y - origin.y);

        return (float) Math.sqrt(delta_x + delta_y);
    }


}
