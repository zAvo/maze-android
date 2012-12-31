/*
 *	Maze-Android
 *	Copyright 2009, 2010, 2011, 2012 Marco Mandrioli
 *
 *	This file is part of Maze-Android.
 *
 *	Maze-Android is free software; you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation; either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	Maze-Android is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with Maze-Android.  If not, see <http://www.gnu.org/licenses/>.
 *
 */


package it.zavo.maze.graphics;

import it.zavo.maze.MazeActivity;
import it.zavo.maze.R;
import it.zavo.maze.graphics.shape.Rectangle;
import it.zavo.maze.maze.Maze;
import it.zavo.maze.physics.Physics;
import it.zavo.maze.util.Status;
import it.zavo.maze.util.Tex;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.os.Vibrator;

/**
 * The renderer responsible for making OpenGL calls to render a frame.
 * 
 * @author Marco Mandrioli
 */
public class Graphics implements Renderer {
	/**
	 * the border to be left around the board. The size is relative to the width
	 * of the screen, and the height border should be scaled to match the screen
	 * ratio.
	 * <p>
	 * Constant value: {@value}
	 */
	private static final float BORDER = 0.015f;

	private Random random;
	private ArrayList<Integer> resourcesList;
	private Context context;
	private Vibrator vibrator;

	private Maze maze = null;
	private Rectangle board;

	/**
	 * Constructs a new renderer within the specified context.
	 * 
	 * @param gl
	 *            the GL object.
	 * @param context
	 *            the context in which the vibrator service is.
	 */
	public Graphics(Context context) {
		this.context = context;

		// gets the vibrator reference
		vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);

		// gets a list of files in the /res/raw/ folder
		resourcesList = new ArrayList<Integer>();
		Field[] fields = R.raw.class.getFields();
		for (Field f : fields) {
			try {
				resourcesList.add(f.getInt(null));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		// creates a new random number generator
		random = new Random();
		
		// creates a new rectangle for the background of the board and the messages
		board = new Rectangle(-1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f);
	}

	/** Called to draw the current frame. */
	@Override
	public void onDrawFrame(GL10 gl) {
		// clears to the background color
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// draws the board background
		board.draw(gl, Tex.BOARD);

		// gets the status of the game
		Status status = MazeActivity.getStatus();
		if (status == Status.GAME_OK) {
			// gets the updated position of the ball
			float[] position = Physics.update(gl, maze);

			// draws the maze
			maze.draw(gl, position[0], position[1]);

			// gets the status of the game
			status = MazeActivity.getStatus();

			// if the level is completed or lost starts the vibrator
			if (status == Status.LEVEL_COMPLETE) {
				// 500ms vibration, 200ms no vibration 3 times
				vibrator.vibrate(
						new long[] { 0, 500, 200, 500, 200, 500, 200 }, -1);

			} else if (status == Status.LEVEL_LOST) {
				// 1000ms vibration
				vibrator.vibrate(1000);
			}
		} else {
			if (status == Status.INITIALIZE_NEW_LEVEL) {
				// creates new maze, chosen randomly from the available maze
				// files
				maze = new Maze(gl, context, resourcesList.get(random
						.nextInt(resourcesList.size())));
				
				// cancels vibration (if still running)
				vibrator.cancel();
				// sets the game status to GAME_OK
				MazeActivity.setGameOk();

			} else if (status == Status.RESET_LEVEL) {
				Physics.init(maze);
				// cancels vibration (if still running)
				vibrator.cancel();
				// sets the game status to GAME_OK
				MazeActivity.setGameOk();

			} else if (status == Status.LEVEL_COMPLETE) {
				// prints the 'Level Complete' message
				board.draw(gl, Tex.LEVEL_COMPLETE);

			} else if (status == Status.LEVEL_LOST) {
				// prints the 'Level Lost' message
				board.draw(gl, Tex.LEVEL_LOST);
			}
		}
	}

	/** Called when the surface changed size. */
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// sets the viewport
		gl.glViewport(0, 0, width, height);
		// switches to the projection matrix stack
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// loads identity matrix
		gl.glLoadIdentity();

		// scales the width to get equal proportions on x and y (thus making the
		// x = (-1, 1), y = (1, -1) region squared)
		gl.glScalef((float) height / (float) width, 1, 1);

		// sets up a 2D orthographic projection reducing the (-1, 1) area of 2 *
		// BORDER (thus leaving a border around the board)
		GLU.gluOrtho2D(gl, -1.0f - BORDER, 1.0f + BORDER, 1.0f + BORDER, -1.0f
				- BORDER);

		// switches back to the modelview matrix stack
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		// loads identity matrix
		gl.glLoadIdentity();
	}

	/** Called when the surface is created or recreated. */
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// background clear color
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		// enables the blending
		gl.glEnable(GL10.GL_BLEND);
		// sets the blending function
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		// enables 2D textures
		gl.glEnable(GL10.GL_TEXTURE_2D);

		// loads the textures (every time the EGL context is lost, the OpenGL resources are freed.
		TextureManager.loadTextures(gl, context);
	}
}