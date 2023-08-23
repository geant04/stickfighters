package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class LevelLoader {
    public static int tile_size;
    private Player player;
    public static Tile[][] map;
    private int px;
    private int py;
    private TextureRegion[][] textures;

    public LevelLoader(Tile[][] map, int tile_size, Player player, int[] source){
        this.map = map;
        this.tile_size = tile_size;
        this.player = player;

        Texture wall_txt = new Texture(Gdx.files.internal("template.png"));
        Texture floor_txt = new Texture(Gdx.files.internal("materials/grass.png"));

        TextureRegion[][] tmp = TextureRegion.split(wall_txt,
                wall_txt.getWidth() / 4,
                wall_txt.getHeight() / 4);
        TextureRegion[][] tmp2 = TextureRegion.split(floor_txt,
                floor_txt.getWidth() / 4,
                floor_txt.getHeight() / 4);
        textures = new TextureRegion[16][16];

        for(int i=0; i<4; i++){
            for(int j=0; j<4; j++){
                textures[1][(i*4)+j] = tmp[i][j];
                textures[0][(i*4)+j] = tmp2[i][j];
            }
        }

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

        float cx = (player.getX() + dx + player.getWidth() / 2) /  tile_size;
        float cy = (player.getY() + dy) /  tile_size;

        if(cx >= map[0].length || cx < 0){
            return;
        }
        if(cy >= map.length || cy < 0){
            return;
        }

        if(!map[(int) cy][(int) cx].isCanCollide()){
            player.set(player.getX() + dx,
                    player.getY() + dy);
        }
        //System.out.println(dx * dx + dy * dy);
    }

    public void render(SpriteBatch batch){
        Tile t;
        int id;
        int[] x = {-1,1,0,0};
        int[] y = {0,0,-1,1};
        String enc;
        Sprite sprite;

        //batch.begin(); // this just draws the map...
        for(int i=0; i < map.length; i++){
            for(int j=0; j < map[0].length; j++){
                t = map[i][j];
                enc = "";
                id = t.getId();
                for(int u=0; u<4; u++){
                    int nx = i + x[u];
                    int ny = j + y[u];
                    if(nx >= 0 && nx < map.length
                        && ny >= 0 && ny < map[0].length){
                        Tile n = map[nx][ny];
                        if(n.getId() != id){
                            enc += "1";
                        }else{
                            enc += "0";
                        }
                    }
                }
                //System.out.println(enc);
                int indx = Integer.parseInt(enc, 2);

                t.setTexture(textures[id][indx].getTexture());
                //t.setTexture(Integer.parseInt(String.valueOf(enc), 2));

                sprite = t.getSprite();
                sprite.setPosition(j * tile_size, i * tile_size);
                sprite.setSize(tile_size, tile_size);
                sprite.draw(batch);

                // check id of x-1, x+1, y-1, y+1 match. Depending on the boolean?
                // somehow convert encoding to binary
                // grab texture from that binary
            }
        }
        //batch.end();
    }

}
