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

import it.zavo.maze.graphics.TextureManager;
import it.zavo.maze.graphics.shape.Circle;
import it.zavo.maze.util.Tex;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Represents the game ball.
 * 
 * @author Marco Mandrioli
 */
public class Ball {
	protected float[] vertices;
	private FloatBuffer vertexBuffer;

	private float[] color = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };

	private float radius = 5.0f;
	private float speed = 0.00000000125f;

	/**
	 * Constructs a ball of a default color.
	 * 
	 * @param scale
	 *            the size of the maze containing the ball.
	 */
	public Ball(float scale) {
		// size is mapped to (-1, 1), so everything is multiplied by 2
		this.radius = (this.radius * 2) / scale;
		// 4196 seems to be a good value :)
		this.speed = this.speed * (4196 - scale);

		// coordinates are (x, y)
		vertices = new float[] {
				-radius, -radius,	// top left vertex
				-radius, radius,	// bottom left vertex
				radius, -radius,	// top right vertex
				radius, radius		// bottom right vertex
		};

		// constructs a buffer of bytes to store the vertices array (float = 4
		// bytes)
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
		// sets the byte order to the platform's (Little Endian or Big Endian)
		byteBuf.order(ByteOrder.nativeOrder());
		// converts the byte buffer to a vertex buffer (float)
		vertexBuffer = byteBuf.asFloatBuffer();
		// puts the vertices in the vertex buffer
		vertexBuffer.put(vertices);
		// sets the position of the vertex buffer to the start
		vertexBuffer.position(0);
	}

	/**
	 * Constructs a ball of the specified color.
	 * 
	 * @param scale
	 *            the size of the maze containing the ball.
	 * @param red
	 *            the red channel of the color.
	 * @param green
	 *            the green channel of the color.
	 * @param blue
	 *            the blue channel of the color.
	 * @param alpha
	 *            the alpha channel (transparency) of the color.
	 */
	public Ball(float scale, float red, float green, float blue, float alpha) {
		this(scale);

		color[0] = red;
		color[1] = green;
		color[2] = blue;
		color[3] = alpha;
	}

	/**
	 * Draws the untextured ball at the given coordinates.
	 * 
	 * @param gl
	 *            the GL object.
	 * @param x
	 *            the x coordinate of the center of the ball.
	 * @param y
	 *            the y coordinate of the center of the ball.
	 */
	public void draw(GL10 gl, float x, float y) {
		// draws the untextured rectangle
		Circle.getSingleton().draw(gl, x, y, radius, color[0], color[1], color[2], color[3]);
	}

	/**
	 * Draws the textured ball at the given coordinates.
	 * 
	 * @param gl
	 *            the GL object.
	 * @param tex
	 *            the texture to be applied to the ball.
	 * @param x
	 *            the x coordinate of the center of the ball.
	 * @param y
	 *            the y coordinate of the center of the ball.
	 * 
	 * @see it.zavo.maze.util.Tex enum Tex
	 * 
	 */
	public void draw(GL10 gl, Tex tex, float x, float y) {
		// enables the vertex array client state
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// enables the texture coordinates array client state
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// makes gl point to the vertex buffer
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);

		// binds the texture to the ball
		TextureManager.bindTexture(gl, tex);

		// no blending, texture on full white
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		// pushes the matrix stack
		gl.glPushMatrix();
		// translates to the point where the center of the ball should be
		gl.glTranslatef(x, y, 0.0f);

		// draws the vertices as triangle strip
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 2);

		// pops back to the previous matrix in the stack
		gl.glPopMatrix();

		// disables the vertex array client state
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		// disables the texture coordinates array client state
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

	/**
	 * Returns the radius of the ball.
	 * 
	 * @return the radius of the ball.
	 */
	public float getRadius() {
		return radius;
	}

	/**
	 * Returns the speed multiplier of the ball.
	 * 
	 * @return the speed multiplier of the ball.
	 */
	public float getSpeed() {
		return speed;
	}
}