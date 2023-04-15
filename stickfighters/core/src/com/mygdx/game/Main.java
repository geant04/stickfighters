package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {
	public final static float WIDTH = 800;
	public final static float HEIGHT = 600;

	private SpriteBatch batch;
	private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;

	public static LevelLoader level;
	public static Array<Enemy> enemies;
	public static Player player;

	private float stateTime;

	@Override
	public void create () {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH, HEIGHT);
		camera.update();

		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		player = new Player(250, 50, 70);
		player.set((int)camera.position.x, (int)camera.position.y);
		enemies = new Array<>();
		enemies.add(new Enemy(100, 50, 70,
				new Vector2(300,150), 100));

		Texture wall_txt = new Texture(Gdx.files.internal("template.png"));
		Texture floor_txt = new Texture(Gdx.files.internal("flr.png"));

		Tile wall = new Wall(wall_txt);
		Tile floor = new Floor(floor_txt);
		 // consider using a texture atlas, i think you can shove this into the wall file

		Tile[][] tmap = {
				{floor, floor, floor, floor},
				{floor, floor, floor, floor},
				{floor, floor, floor, floor}
		};

		this.level = new LevelLoader(tmap, 120, player, new int[]{1,1});
		this.stateTime = 0;
	}

	@Override
	public void render () {
		stateTime += Gdx.graphics.getDeltaTime();
		ScreenUtils.clear( (float) 0.5, (float) 0.5, (float) 0.5, 0);
		// tell the camera to update its matrices.
		camera.position.x = player.getX() + player.getWidth() / 2;
		camera.position.y = player.getY() + player.getHeight() / 2;
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		batch.setProjectionMatrix(camera.combined);

		/*
		shapeRenderer.setColor(Color.BLUE);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.rect(0, 0, WIDTH, (HEIGHT_LIMIT) - player.getHeight() + 10);
		shapeRenderer.end();
		 */
		batch.begin();
			level.render(batch);
			level.updatePlayer();
			for(Enemy e : enemies){
				e.render(batch, stateTime);
			}
			player.render(shapeRenderer, batch, camera, stateTime);
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
