package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LevelLoader {
    private int tile_size;
    private Player player;
    private Tile[][] map;
    private int px;
    private int py;

    public LevelLoader(Tile[][] map, int tile_size, Player player, int[] source){
        this.map = map;
        this.tile_size = tile_size;
        this.player = player;

        player.set(source[0] * tile_size, source[1] * tile_size);
    }

    public int[] getPlayerPosition(){
        // somehow I convert the player (x,y) to that of the map
        this.px = (int) (player.getX() / tile_size);
        this.py = (int) (player.getY() / tile_size);

        return new int[] {this.px, this.py};
    }

    public void updatePlayer(){
        int[] pos = getPlayerPosition();
        int speed = player.getSpeed();

        float dx = player.getVx() * speed * Gdx.graphics.getDeltaTime();
        float dy = player.getVy() * speed * Gdx.graphics.getDeltaTime();

        int cx = (int) (player.getX() + dx + player.getWidth() / 2) /  tile_size;
        int cy = (int) (player.getY() + dy) /  tile_size;

        if(!map[cy][cx].isCanCollide()){
            player.set(player.getX() + dx,
                    player.getY() + dy);
        }
        //System.out.println(dx * dx + dy * dy);
    }

    public void render(SpriteBatch batch){
        batch.begin(); // this just draws the map...
        for(int i=0; i < map.length; i++){
            for(int j=0; j < map[0].length; j++){
                Tile t = map[i][j];
                Sprite sprite = t.getSprite();
                sprite.setPosition(j * tile_size, i * tile_size);
                sprite.setSize(tile_size, tile_size);
                sprite.draw(batch);

                // check id of x-1, x+1, y-1, y+1 match. Depending on the boolean?
                /*
                int id = t.getID();

                int[] x = {-1,1};
                int[] y = {-1,1};
                String encoding = "";

                for(int u=0; u<x.length; u++){ // constant time optimization
                    for(int v=0; v<y.length; v++){
                    Tile n = map[i+x[u]][j+y[v]];
                    if(n.getID() != id)){
                        encoding += "0";
                    else{
                        encoding += "1";
                    }
                    }
                }
                t.newSprite(texture_pack[Integer.parseInt(encoding, 2)]);

                // somehow convert encoding to binary
                // grab texture from that binary
                */
            }
        }
        batch.end();
    }
}
