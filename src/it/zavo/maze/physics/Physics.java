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


package it.zavo.maze.physics;

import it.zavo.maze.MazeActivity;
import it.zavo.maze.maze.Maze;
import it.zavo.maze.util.Status;

import javax.microedition.khronos.opengles.GL10;

import android.os.SystemClock;

/**
 * Static class implementing the physics engine.
 * 
 * @author Marco Mandrioli
 */
public class Physics {
	/**
	 * the threshold under which sensor values are ignored.
	 * <p>
	 * Constant value: {@value}
	 */
	private static final float threshold = 0.01f;

	/**
	 * the coefficient of reduction of the speed after a bounce against a wall.
	 * <p>
	 * Constant value: {@value}
	 */
	private static final float bounceReduction = 0.53f;

	private static float ballSpeed;
	private static float ballRadius;

	private static long time = 0;
	private static long timeOld = 0;
	private static long timeElapsed = 0;

	private static float[] orientation = new float[3];
	private static float[] position = { 0.0f, 0.0f };
	private static float[] speed = { 0.0f, 0.0f };
	private static float[] acceleration = { 0.0f, 0.0f };

	/**
	 * Checks if the ball is colliding with a border of the board.
	 * 
	 * @return <code>true</code> if a collision is detected, <code>false</code>
	 *         otherwise.
	 */
	private static boolean checkBorders() {
		boolean collision = false;

		// if the ball goes out of the left side
		if (position[0] < ballRadius - 1.0f) {
			// flips the amount that passed the border on the other direction
			position[0] = 2 * ballRadius - position[0] - 2.0f;

			// multiplies speed on the x axis for the bounce reduction constant
			// and changes the sign
			speed[0] = -(speed[0] * bounceReduction);
			// resets acceleration
			acceleration[0] = 0.0f;

			collision = true;
		}
		// if the ball goes out of the right side
		else if (position[0] > 1.0f - ballRadius) {
			// flips the amount that passed the border on the other direction
			position[0] = 2 * (1.0f - ballRadius) - position[0];

			// multiplies speed on the x axis for the bounce reduction constant
			// and changes the sign
			speed[0] = -(speed[0] * bounceReduction);
			// resets acceleration
			acceleration[0] = 0.0f;

			collision = true;
		}

		// if the ball goes out of the top side
		if (position[1] < ballRadius - 1) {
			// flips the amount that passed the border on the other direction
			position[1] = 2 * ballRadius - position[1] - 2;

			// multiplies speed on the x axis for the bounce reduction constant
			// and changes the sign
			speed[1] = -(speed[1] * bounceReduction);
			// resets acceleration
			acceleration[1] = 0.0f;

			collision = true;
		}
		// if the ball goes out of the bottom side
		else if (position[1] > 1.0f - ballRadius) {
			// flips the amount that passed the border on the other direction
			position[1] = 2 * (1.0f - ballRadius) - position[1];

			// multiplies speed on the x axis for the bounce reduction constant
			// and changes the sign
			speed[1] = -(speed[1] * bounceReduction);
			// resets acceleration
			acceleration[1] = 0.0f;

			collision = true;
		}

		return collision;
	}

	/**
	 * Checks if the ball is colliding with walls, holes and borders.
	 * 
	 * @param maze
	 *            the maze to check for collisions.
	 * 
	 * @return the new status of the application.
	 */
	private static Status checkCollisions(final Maze maze) {
		// checks for collisions with the borders
		checkBorders();

		// checks for collisions with the walls
		maze.checkWallsCollisions(position, speed, acceleration,
				bounceReduction);
		
		// checks for collisions with the holes
		return maze.checkHolesCollision(position);
	}

	/**
	 * Computes the new acceleration of the ball using the data from the
	 * sensors.
	 */
	private static void computeAcceleration() {
		// acceleration = values * -1

		// checks for the threshold
		if (Math.abs(orientation[1]) > threshold)
			acceleration[0] = -(float) (Math.sin(orientation[1])
					* Math.cos(orientation[1]) * ballSpeed);
		else
			acceleration[0] = 0.0f;

		// checks for the threshold
		if (Math.abs(orientation[2]) > threshold)
			acceleration[1] = -(float) (Math.sin(orientation[2])
					* Math.cos(orientation[2]) * ballSpeed);
		else
			acceleration[1] = 0.0f;
	}

	/** Computes the new speed based on the acceleration and the time. */
	private static void computeSpeed() {
		// computes acceleration
		computeAcceleration();

		// updates time difference
		timeOld = time;
		time = SystemClock.uptimeMillis();
		timeElapsed = time - timeOld;

		// computes speed
		speed[0] += acceleration[0] * timeElapsed;
		speed[1] += acceleration[1] * timeElapsed;
	}

	/**
	 * Computes the new position of the ball in the given maze.
	 * 
	 * @param maze
	 *            the maze to process.
	 * 
	 * @return the new status of the application.
	 */
	private static Status computePosition(final Maze maze) {
		// computes speed
		computeSpeed();

		// correct formulae, but worse result
		// position[0] = position[0] + speed[0] * timeElapsed;
		// position[1] = position[1] + speed[1] * timeElapsed;

		// wrong formulae, but better result
		position[0] = position[0] + speed[0] * timeElapsed + 0.5f
				* acceleration[0] * timeElapsed * timeElapsed;
		position[1] = position[1] + speed[1] * timeElapsed + 0.5f
				* acceleration[1] * timeElapsed * timeElapsed;

		// checks collisions
		return checkCollisions(maze);
	}

	/**
	 * Updates the status of the physics engine. Computes the new position of
	 * the ball and checks for collisions.
	 * 
	 * @param gl
	 *            the GL object.
	 * @param maze
	 *            the maze to process.
	 * 
	 * @return an array of two floats containing the new coordinates (x, y) of
	 *         the ball.
	 */
	public static float[] update(GL10 gl, final Maze maze) {
		// computes position
		Status s = computePosition(maze);

		// updates the status of the application
		if (s == Status.LEVEL_COMPLETE)
			MazeActivity.setLevelComplete();
		else if (s == Status.LEVEL_LOST)
			MazeActivity.setLevelLost();
		else
			MazeActivity.setGameOk();

		return position.clone();
	}

	/**
	 * Initializes the physical engine using the data from the given maze.
	 * 
	 * @param maze
	 *            the maze to initialize the physical engine onto.
	 */
	public static void init(final Maze maze) {
		ballSpeed = maze.getBallSpeed();
		ballRadius = maze.getBallRadius();

		time = SystemClock.uptimeMillis();
		timeOld = time;
		timeElapsed = 0;
		
		acceleration[0] = 0;
		acceleration[1] = 0;

		speed[0] = 0;
		speed[1] = 0;

		position = maze.getStart();
	}

	/**
	 * Updates the orientation using the values from the sensors. Call this
	 * method when the sensors detected a change.
	 * 
	 * @param values
	 *            an array of three floats containing the new data from the
	 *            sensors.
	 */
	public static void updateOrientation(final float[] values) {
		orientation[0] = values[0];
		orientation[1] = values[1];
		orientation[2] = values[2];
	}
}