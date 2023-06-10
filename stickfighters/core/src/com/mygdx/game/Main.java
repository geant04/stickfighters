package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {
	public final static float WIDTH = 800;
	public final static float HEIGHT = 600;

	private SpriteBatch batch;
	private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;

	public static LevelLoader level;
	public static Array<Enemy> enemies;

	public static Pool<Enemy> enemyPool = Pools.get(Enemy.class);
	public static Pool<BigEnemy> bigPool = Pools.get(BigEnemy.class);

	public static Player player;
	private float stateTime;
	private BitmapFont font;

	@Override
	public void create () {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH, HEIGHT);
		camera.update();

		this.font = new BitmapFont();

		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		player = new Player(250, 50, 70);
		player.set((int)camera.position.x, (int)camera.position.y);
		enemies = new Array<Enemy>();

		Enemy dummy = enemyPool.obtain();
		Enemy guy = enemyPool.obtain();
		Enemy guy2 = enemyPool.obtain();
		BigEnemy guy3 = bigPool.obtain();

		dummy.init(new Vector2(100,100));
		dummy.setSpeed(0);

		guy.init(new Vector2(300, 150));
		guy2.init(new Vector2(350, 150));
		guy3.init(new Vector2(350, 150));

		enemies.add(dummy);

		//enemies.add(guy);
		//enemies.add(guy2);
		//enemies.add(guy3);
		/*
		enemies.add(new Enemy(100, 50, 70,
				new Vector2(300,150), 100));
		enemies.add(new Enemy(80, 50, 70,
				new Vector2(350,150), 140));
		*/
		Texture wall_txt = new Texture(Gdx.files.internal("template.png"));
		Texture floor_txt = new Texture(Gdx.files.internal("materials/blueblock.png"));

		Tile wall = new Wall(wall_txt);
		Tile floor = new Floor(floor_txt);
		 // consider using a texture atlas, i think you can shove this into the wall file

		Tile[][] tmap = {
				{floor, floor, floor, floor, floor ,floor, floor, floor, floor, floor, floor ,floor},
				{floor, floor, floor, floor, floor ,floor, floor, floor, floor, floor, floor ,floor},
				{floor, floor, floor, floor, floor ,floor, floor, floor, floor, floor, floor ,floor},
				{floor, floor, floor, floor, floor ,floor, floor, floor, floor, floor, floor ,floor},
				{floor, floor, floor, floor, floor ,floor, floor, floor, floor, floor, floor ,floor},
				{floor, floor, floor, floor, floor ,floor, floor, floor, floor, floor, floor ,floor}
		};

		this.level = new LevelLoader(tmap, 60, player, new int[]{1,1});
		this.stateTime = 0;
	}

	@Override
	public void render () {
		ScreenUtils.clear( (float) 0.5, (float) 0.5, (float) 0.5, 0);
		// tell the camera to update its matrices.
		stateTime += Gdx.graphics.getDeltaTime();
		Matrix4 uiMatrix = camera.combined.cpy();
		uiMatrix.setToOrtho2D(0, 0, WIDTH, HEIGHT);

		float lerp = 9.5f;
		Vector3 position = camera.position;
		position.x += (player.getX() + player.getWidth() / 2 - position.x)
				* lerp * Gdx.graphics.getDeltaTime();
		position.y += (player.getY() + player.getHeight() / 2 - position.y)
				* lerp * Gdx.graphics.getDeltaTime();
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
			level.render(batch);
			level.updatePlayer();
			for(Enemy e : enemies){
				e.render(batch, stateTime);
			}
			player.render(shapeRenderer, batch, camera, stateTime);
		batch.end();
		batch.setProjectionMatrix(uiMatrix);
		batch.begin();
		font.draw(batch,  " fps:" + Gdx.graphics.getFramesPerSecond(), 26, 65);
		batch.end();
		player.update();
		for(Enemy e : enemies){
			e.update();
		}
	}

	@Override
	public void dispose() {
		batch.dispose();
		player.dispose();
	}
}
