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


package it.zavo.maze.graphics.shape;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Singleton class used to draw a circle.
 * 
 * @author Marco Mandrioli
 */
public class Circle {
	/** the private variable containing the singleton instance of this class. */
	private static Circle instance;

	/**
	 * the number of vertices used to draw the circle.<br>
	 * <b>NOTE:</b> if changed, the cycle in the function
	 * {@link #draw(GL10, float, float, float, float, float, float, float) draw}
	 * should be edited accordingly.
	 * <p>
	 * Constant value: {@value}
	 */
	private final int verticesCount = 45;
	
	/**
	 * the constant for the conversion from degrees to radians.
	 * <p>
	 * Constant value: <code>2 * PI / 360</code> = {@value}
	 */
	private final float toRad = 0.017453293f;

	private FloatBuffer vertexBuffer;
	
	/** Constructs the new singleton. */
	private Circle() {
		float[] vertices = new float[verticesCount * 2];
		int j = 0;

		for (int i = 0; i < 360; i += 8) {
			float rad = i * toRad;

			// x coordinate
			vertices[j++] = (float) Math.cos(rad);
			// y coordinate
			vertices[j++] = (float) Math.sin(rad);
		}

		// constructs a buffer of bytes to store the vertices array (float =
		// 4 bytes)
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
		// sets the byte order to the platform's (Little Endian or Big
		// Endian)
		byteBuf.order(ByteOrder.nativeOrder());
		// converts the byte buffer to a vertex buffer (float)
		vertexBuffer = byteBuf.asFloatBuffer();
		// puts the vertices in the vertex buffer
		vertexBuffer.put(vertices);
		// sets the position of the vertex buffer to the start
		vertexBuffer.position(0);
	}
	
	/**
	 * Gets the singleton instance. If there's no instances of this class, creates a new instance.
	 * 
	 * @return the singleton instance.
	 */
	public static Circle getSingleton() {
		// if there are no instances of this class creates a new instance
		if (instance == null)
			instance = new Circle();
		
		// returns the existing instance
		return instance;
	}

	/**
	 * Draws the circle of the given color, with the given radius, in the given
	 * position.
	 * 
	 * @param gl
	 *            the GL object.
	 * @param x
	 *            the x coordinate of the center of the circle.
	 * @param y
	 *            the y coordinate of the center of the circle.
	 * @param radius
	 *            the radius of the circle.
	 * @param red
	 *            the red channel of the color.
	 * @param green
	 *            the green channel of the color.
	 * @param blue
	 *            the blue channel of the color.
	 * @param alpha
	 *            the alpha channel (transparency) of the color.
	 */
	public void draw(GL10 gl, float x, float y, float radius, float red,
			float green, float blue, float alpha) {
		// enables the vertex array client state
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		// makes gl point to the vertex buffer
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);

		// sets the circle color
		gl.glColor4f(red, green, blue, alpha);

		// pushes the matrix stack
		gl.glPushMatrix();
		// translates to the point where the center of the circle should be
		gl.glTranslatef(x, y, 0.0f);
		// scales to the radius of the circle on both x and y axes
		gl.glScalef(radius, radius, 1.0f);

		// draws the vertices as triangle fan
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, verticesCount);

		// pops back to the previous matrix in the stack
		gl.glPopMatrix();

		// disables the vertex array client state
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
}