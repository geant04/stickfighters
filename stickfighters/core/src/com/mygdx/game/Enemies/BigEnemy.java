package com.mygdx.game.Enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Enemy;

public class BigEnemy extends Enemy {
    private float x;
    private float y;

    public BigEnemy(){
        super(80, (int) (50 * 1.5), (int) (70 * 1.5), 300);
        this.hand.setSize(hand.getWidth() * 1.5f, hand.getHeight() * 1.5f);
        this.EQUIPPED_GUN.DAMAGE = 30f;
    }
}
