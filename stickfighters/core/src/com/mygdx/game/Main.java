package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.Enemies.BigEnemy;
import com.mygdx.game.Objects.Ammo;
import com.mygdx.game.Objects.Bullet;

public class Main extends ApplicationAdapter {
	public final static float WIDTH = 800 * 2f;
	public final static float HEIGHT = 600 * 2f;
	public final static int TILE_SIZE = 250;
	public final static int X_TILE = 3;
	public final static int Y_TILE = 4;

	private SpriteBatch batch;
	private OrthographicCamera camera;

	public static LevelLoader level;
	public static Array<Enemy> enemies;
	private final Pool<Enemy> enemyPool = new Pool<Enemy>() {
		@Override
		protected Enemy newObject() {
			return new Enemy();
		}
	};
	private final Pool<BigEnemy> bigPool = new Pool<BigEnemy>() {
		@Override
		protected BigEnemy newObject() {
			return new BigEnemy();
		}
	};

	public static Array<Bullet> activeBullets = new Array<Bullet>();
	public static final Pool<Bullet> bulletPool = new Pool<Bullet>() {
		@Override
		protected Bullet newObject() {
			return new Bullet();
		}
	};

	public static Array<Ammo> activeAmmo = new Array<Ammo>();
	public static final Pool<Ammo> ammoPool = new Pool<Ammo>() {
		@Override
		protected Ammo newObject() {
			return new Ammo();
		}
	};
	private Texture box;

	public static Player player;
	private boolean isTesting;
	private float stateTime;
	private float timer;
	private int wave;
	private int ems;
	private int spawned;

	public static int killed;
	private int oldKilled;
	private float silly;

	private BitmapFont font;
	private Sprite backgroundSprite;
	private ShapeRenderer shapeRenderer;

	@Override
	public void create () {
		this.isTesting = false;

		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH, HEIGHT);
		camera.update();

		batch = new SpriteBatch();
		this.box = new Texture(Gdx.files.internal("objects/ammo.png"));

		this.shapeRenderer = new ShapeRenderer();

		player = new Player(250, 50, 70);
		player.set((int)camera.position.x, (int)camera.position.y);

		enemies = new Array<Enemy>();

		this.font = new BitmapFont();
		font.getData().setScale(1.2f);

		Texture wall_txt = new Texture(Gdx.files.internal("template.png"));
		Texture floor_txt = new Texture(Gdx.files.internal("materials/lightsquare.png"));
		this.backgroundSprite = new Sprite(new Texture(Gdx.files.internal("background.png")));

		Tile wall = new Wall(wall_txt);
		Tile floor = new Floor(floor_txt);

		Tile[][] tmap = new Tile[X_TILE][Y_TILE];
		for(int i=0; i<tmap.length; i++){
			for(int j=0; j<tmap[0].length; j++){
				tmap[i][j] = floor;
			}
		}
		int[] spawn = new int[]{2,2};

		if(isTesting){
			TestZone();
			spawn = new int[]{1, 1};
		}

		this.level = new LevelLoader(tmap, TILE_SIZE, player, spawn);
		this.stateTime = 0;
		this.timer = 0f;

		silly = 1f;
		wave = 0;
		ems = 4;
		killed = 0;
		oldKilled = 0;
	}

	public void TestZone() {
		Enemy dummy = enemyPool.obtain();
		dummy.init(new Vector2(100,100));
		dummy.setSpeed(0);
		dummy.setHealth(100);

		enemies.add(dummy);

		Ammo box = ammoPool.obtain();
		box.init(400, 100, this.box);
		activeAmmo.add(box);
	}

	public void waveSystem () {
		timer -= Gdx.graphics.getDeltaTime();
		if(timer <= 0){
			timer = 2f;
			if((enemies.size == 0
					|| spawned <= Math.min(ems / 2f + 2 , 4 * silly)
					|| killed - oldKilled >= 2) && spawned < ems){ // a substantial amount of guys are dead
				for(int i = Math.min(2, ems - spawned); i > 0; i--){
					Enemy e = enemyPool.obtain();
					// origin (x,y)  = 0
					float player_radius = 20f;
					float x_max = TILE_SIZE * X_TILE;
					float y_max = TILE_SIZE * (Y_TILE - 1);

					float x1 = Math.max(0, player.getAbsoluteX() - player_radius);
					float x2 = Math.min(x_max, player.getAbsoluteX() + player_radius);

					float y1 = Math.max(0, player.getAbsoluteY() - player_radius);
					float y2 = Math.min(y_max, player.getAbsoluteY() + player_radius);

					float ex = (float) Math.random() * x1;
					float ey = (float) Math.random() * y1;

					if(ex == 0 || (ex != 0 && Math.random() < 0.5f)){ // decide whether or not to spawn on the left or right
						ex = (float) Math.random() * (x_max - x2) + x2;
					}
					if(ey == 0 || (ey != 0 && Math.random() < 0.5f)){
						ey = (float) Math.random() * (y_max - y2) + y2;
					}

					if(wave >= 3){
						// crazy probability function
						// \frac{50}{-x^{0.4}-1}+50
						double xD = 50 / (-1 * Math.pow(silly, 0.4) - 1) + 50;
						double p = Math.random() * 100;
						if(p <= xD){
							e = bigPool.obtain();
						}
					}
					e.init(new Vector2((float) ex, (float) ey));
					enemies.add(e);
					spawned++;
				}
			}
			if(enemies.size == 0){
				System.out.printf("Wave %d completed! Onto the wave %d\n", wave, wave + 1);
				wave++;
				killed = 0;
				oldKilled = 0;
				spawned = 0;
				ems += 10;
				silly += 2;
				return;
			}
			if(stateTime % 4f <= 0.01f){
				oldKilled = killed; // update the oldKilled amount
			}
			System.out.printf("spawned %d enemies, %d left in the wave\n",
					spawned,
					ems - spawned);
		}
		// keep spawning until half the enemies are there. then stop. If you killed enough people, do the other system
	}

	public void updateBeings(){
		Bullet item;
		Enemy enemy;
		Ammo ammo;

		for (int i = activeBullets.size; --i >= 0;) { // cleanup bullets
			item = activeBullets.get(i);
			if (!item.alive) {
				activeBullets.removeIndex(i);
				Pools.free(item);
			}
		}
		for (int i = enemies.size; --i >= 0;) { // cleanup enemies
			enemy = enemies.get(i);
			if (!enemy.ALIVE && !enemy.DEADANIM) {
				killed++;
				if(Math.random() <= 0.80f){ // ~ 20% chance of an enemy dropping an ammo box
					Ammo box = ammoPool.obtain();
					box.init(enemy.getX(),enemy.getY(), this.box);
					activeAmmo.add(box);
				}
				enemies.removeIndex(i);
				Pools.free(enemy);
			}
		}
		for (int i = activeAmmo.size; --i >= 0;) { // cleanup Ammo
			ammo = activeAmmo.get(i);
			if (!ammo.alive) {
				activeAmmo.removeIndex(i);
				Pools.free(ammo);
			}
		}

		for(Enemy e : enemies){
			e.update();
		}
		for(Bullet b : activeBullets){
			for(Enemy e : enemies){
				if(e.ALIVE && e.isCollide(b)){
					b.hit= true; // makes the bullet unalive itself
					e.damage(Player.EQUIPPED_GUN.DAMAGE, (Player.flip ? -1 : 1) * b.force);
				}
			}
			b.update();
		}
		for(Ammo a : activeAmmo){
			if(player.isCollide(a)){
				a.hit = true;
			}
			a.update();
		}
	}

	public void renderObjects(){
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
			level.render(batch);
			level.updatePlayer();
		batch.end();
		batch.begin();
			for(Ammo a: activeAmmo){
				shapeRenderer.setProjectionMatrix(camera.combined);
				shapeRenderer.begin(ShapeType.Filled);
				shapeRenderer.setColor(Color.BLACK);
				shapeRenderer.rect(a.getX(), a.getOGY(), a.getWidth(), 3);
				shapeRenderer.end();
			}
			for(Enemy e: enemies){
				shapeRenderer.setProjectionMatrix(camera.combined);
				shapeRenderer.begin(ShapeType.Filled);
				shapeRenderer.setColor(Color.GREEN);
				shapeRenderer.rect(e.getX(), e.getY() - 6, e.getHealth() * 0.6f, 4);
				shapeRenderer.end();
			}
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(Color.GREEN);
			shapeRenderer.rect(player.getX(), player.getY() - 6, player.getHealth() * 0.5f, 4);
			shapeRenderer.end();
			batch.end();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
			for(Bullet b : activeBullets){
				b.render(batch);
			}
			for(Ammo a : activeAmmo){
				a.render(batch);
			}
			for(Enemy e : enemies){
				e.render(batch, stateTime);
			}
			player.render(batch, stateTime);
		batch.end();
	}
	public void renderUI(Matrix4 uiMatrix){
		//Stage stage = new Stage(new ScreenViewport());
		//Gdx.input.setInputProcessor(stage);
		// you need an actor for text on SCREEN... YAY.... YAY.... YAY. I think all of this junk needs to be in a different class

		batch.setProjectionMatrix(uiMatrix); // draw your UI stuff here
		batch.begin();
			font.draw(batch,  " fps:" + Gdx.graphics.getFramesPerSecond(), WIDTH / 100, 65);
			font.draw(batch,  " HEALTH: " + player.health, WIDTH / 10 + 20, 65);
			font.draw(batch,  " WAVE: " + wave, WIDTH / 3 + 20, 65);
			font.draw(batch,  " AMMO: " + player.EQUIPPED_GUN.AMMO, WIDTH / 2 + 150, 65);
			font.draw(batch, " POS: " + player.getX() + ", " + player.getY(), WIDTH / 10 + 20, 30);
			font.draw(batch, " ADJ.POS: " + player.getAbsoluteX() + ", " + player.getAbsoluteY(), WIDTH / 10 + 20, 10);
		batch.end();
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

		batch.begin();
		batch.draw(backgroundSprite, 0 , 0, Gdx.graphics.getWidth(), Gdx.graphics.getWidth());
		batch.end();
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		renderObjects();
		renderUI(uiMatrix);

		if(!isTesting){
			waveSystem();
		}else{
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(Color.RED);
			shapeRenderer.rect(player.getAbsoluteX(), player.getAbsoluteY(), 10, 10);
			shapeRenderer.end();

			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(Color.BLUE);
			shapeRenderer.rect(player.getX(), player.getY(), 10, 10);
			shapeRenderer.end();

			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(Color.GREEN);
			shapeRenderer.rect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
			shapeRenderer.end();
		}
		player.update();
		updateBeings();
	}

	@Override
	public void dispose() {
		batch.dispose();
		player.dispose();
	}
}
