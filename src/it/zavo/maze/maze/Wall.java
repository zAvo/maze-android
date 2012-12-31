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

import it.zavo.maze.graphics.shape.Rectangle;

/**
 * Represents a wall of the maze.
 * 
 * @author Marco Mandrioli
 * 
 * @see it.zavo.maze.shape.Rectangle
 */
public class Wall extends Rectangle {
	private float[] center;
	private float[] halfSize;

	/**
	 * Constructs a wall of the default color at the given coordinates.
	 * 
	 * @param scale
	 *            the size of the maze containing the ball.
	 * @param left
	 *            x coordinate of the left side.
	 * @param right
	 *            x coordinate of the right side.
	 * @param top
	 *            y coordinate of the top side.
	 * @param bottom
	 *            y coordinate of the bottom side.
	 */
	public Wall(float scale, float left, float right, float top, float bottom) {
		// size is mapped to (-1, 1), so everything is multiplied by 2
		super(((left * 2) / scale) - 1, ((right * 2) / scale) - 1,
				((top * 2) / scale) - 1, ((bottom * 2) / scale) - 1, 0.0f,
				0.0f, 0.0f, 1.0f);

		// gets values from the vertices to calculate the half width and height
		halfSize = new float[] { (vertices[6] - vertices[0]) / 2,
				(vertices[7] - vertices[1]) / 2 };

		// gets values from the vertices to calculate the center
		center = new float[] { vertices[0] + halfSize[0],
				vertices[1] + halfSize[1] };
	}

	/**
	 * Gets the center coordinates of this wall.
	 * 
	 * @return an array of two floats (x, y).
	 */
	public float[] getCenter() {
		return center.clone();
	}

	/**
	 * Gets the half widht and half height of this wall.
	 * 
	 * @return an array of two floats (half width, half height).
	 */
	public float[] getHalfSize() {
		return halfSize.clone();
	}
}