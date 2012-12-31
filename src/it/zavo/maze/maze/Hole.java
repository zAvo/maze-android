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


package it.zavo.maze.maze;

import it.zavo.maze.graphics.shape.Circle;

import javax.microedition.khronos.opengles.GL10;

/**
 * Represents an hole of the maze.
 * 
 * @author Marco Mandrioli
 */
public class Hole {
	private float[] position;
	private float radius;
	private float[] color;
	private boolean isGoal;

	/**
	 * Constructs an hole (or the goal) with a specified radius at the specified
	 * coordinates.
	 * 
	 * @param scale
	 *            the size of the maze containing the ball.
	 * @param x
	 *            the x coordinate of the center of the hole.
	 * @param y
	 *            the x coordinate of the center of the hole.
	 * @param radius
	 *            the radius of the hole.
	 * @param isGoal
	 *            tells whether this is a common hole or the goal.
	 */
	public Hole(float scale, float x, float y, float radius, boolean isGoal) {
		// size is mapped to (-1, 1), so everything is multiplied by 2
		position = new float[] { ((x * 2) / scale) - 1, (y * 2) / scale - 1 };
		this.radius = ((radius * 2) / scale);

		this.isGoal = isGoal;

		if (isGoal)
			color = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
		else
			color = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
	}

	/**
	 * Draws the hole.
	 * 
	 * @param gl
	 *            the GL object.
	 */
	public void draw(GL10 gl) {
		// draws the hole as a simple circle
		Circle.getSingleton().draw(gl, position[0], position[1], radius, color[0], color[1],
				color[2], color[3]);
	}

	/**
	 * Returns the coordinates of the hole.
	 * 
	 * @return an array of two floats (x, y).
	 */
	public float[] getPosition() {
		return position.clone();
	}

	/**
	 * Returns the radius of the hole.
	 * 
	 * @return the radius of the hole.
	 */
	public float getRadius() {
		return radius;
	}

	/**
	 * Tells if the hole is the goal.
	 * 
	 * @return <code>true</code> if the hole is the goal, <code>false</code>
	 *         otherwise.
	 */
	public boolean isGoal() {
		return isGoal;
	}
}