package com.fska.gameoflife;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;

public class GameOfLife extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;

	OrthographicCamera cam;

	ShapeRenderer renderer;

	// Boolean double buffer grid
	// grid[frame][width][height]
	// frame == 0 : 1
	//
	public boolean[][][] grid;

	private static final int GRID_WIDTH = 400;
	private static final int GRID_HEIGHT = 300;

	private static float RECT_WIDTH;
	private static float RECT_HEIGHT;

	@Override
	public void create() {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		renderer = new ShapeRenderer();

		grid = new boolean[2][GRID_WIDTH][GRID_HEIGHT];
		RECT_WIDTH = (float) Gdx.graphics.getWidth() / GRID_WIDTH;
		RECT_HEIGHT = (float) Gdx.graphics.getHeight() / GRID_HEIGHT;

		cam = new OrthographicCamera();
		RandomXS128 rand = new RandomXS128();

		// Randomize the Starting
		for (int i = 0; i < (GRID_HEIGHT * GRID_WIDTH) / 12; i++) {
			grid[0][rand.nextInt(GRID_WIDTH)][rand.nextInt(GRID_HEIGHT)] = true;
		}
	}

	private float updateFrameTime = 0;

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		updateFrameTime += Gdx.graphics.getDeltaTime();
		if (updateFrameTime >= 0.03f) {
			applyRules();
			updateFrameTime = 0f;
			if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
				applyExplosion(75);
			}
		}
		// cam.update();
		// renderer.setProjectionMatrix(cam.combined);
		// applyRules();
		// batch.begin();
		renderer.begin(ShapeType.Filled);
		renderer.setColor(Color.BLUE);
		for (int x = 0; x < GRID_WIDTH; x++) {
			for (int y = 0; y < GRID_HEIGHT; y++) {
				if (grid[0][x][y])
					renderer.rect(x * RECT_WIDTH, y * RECT_HEIGHT, RECT_WIDTH,
							RECT_HEIGHT);
			}
		}
		renderer.end();
		// batch.end();

		// Reset everything
		if (Gdx.input.isKeyPressed(Keys.R)) {
			create();
		}
	}

	public void applyExplosion(int radius) {
		final int doubleRadius = radius * radius;
		int mouseX = (int) ((float) Gdx.input.getX() / RECT_WIDTH);
		int mouseY = (int) ((float) ((float) (Gdx.graphics.getHeight() - Gdx.input
				.getY())) / RECT_HEIGHT);
		for (int x = 0; x < GRID_WIDTH; x++) {
			for (int y = 0; y < GRID_HEIGHT; y++) {
				if (((x - mouseX) * (x - mouseX) + (y - mouseY) * (y - mouseY)) <= doubleRadius) {
					grid[0][x][y] = false;
				}
			}
		}
	}

	/**
	 * RULES!!!
	 * 
	 * Any live cell with fewer than two live neighbours dies, as if caused by
	 * under-population. Any live cell with two or three live neighbours lives
	 * on to the next generation. Any live cell with more than three live
	 * neighbours dies, as if by overcrowding. Any dead cell with exactly three
	 * live neighbours becomes a live cell, as if by reproduction.
	 */
	public void applyRules() {
		grid[1] = grid[0].clone();
		for (int x = 0; x < GRID_WIDTH; x++) {
			for (int y = 0; y < GRID_HEIGHT; y++) {
				grid[0][x][y] = checkNeighbors(x, y, grid[1][x][y]);
			}
		}

	}

	public boolean checkNeighbors(int x, int y, boolean isAlive) {
		int numOfNeighbors = 0;

		if (x > 0)
			numOfNeighbors += (grid[1][x - 1][y]) ? 1 : 0;
		if (x < (GRID_WIDTH - 1))
			numOfNeighbors += (grid[1][x + 1][y]) ? 1 : 0;
		if (y > 0)
			numOfNeighbors += (grid[1][x][y - 1]) ? 1 : 0;
		if (y < (GRID_HEIGHT - 1))
			numOfNeighbors += (grid[1][x][y + 1]) ? 1 : 0;
		if (x > 0 && y < (GRID_HEIGHT - 1))
			numOfNeighbors += (grid[1][x - 1][y + 1]) ? 1 : 0;
		if (x < (GRID_WIDTH - 1) && y < (GRID_HEIGHT - 1))
			numOfNeighbors += (grid[1][x + 1][y + 1]) ? 1 : 0;
		if (x > 0 && y > 0)
			numOfNeighbors += (grid[1][x - 1][y - 1]) ? 1 : 0;
		if (x < (GRID_WIDTH - 1) && y > 0)
			numOfNeighbors += (grid[1][x + 1][y - 1]) ? 1 : 0;
		if (isAlive) {
			if(numOfNeighbors == 2 || numOfNeighbors == 3 || numOfNeighbors == 4)
				return true;
			
			return false;
		}
		if (numOfNeighbors == 3)
			return true;
		return false;
	}
}
