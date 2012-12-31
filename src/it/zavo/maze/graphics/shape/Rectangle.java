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

import it.zavo.maze.graphics.TextureManager;
import it.zavo.maze.util.Tex;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Represents a fixed-position rectangle.
 * 
 * @author Marco Mandrioli
 */
public class Rectangle {
	protected float[] vertices;
	private FloatBuffer vertexBuffer;

	private float[] color = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };

	/**
	 * Constructs a new rectangle of a default color at the given coordinates.
	 * 
	 * @param left
	 *            x coordinate of the left side.
	 * @param right
	 *            x coordinate of the right side.
	 * @param top
	 *            y coordinate of the top side.
	 * @param bottom
	 *            y coordinate of the bottom side.
	 */
	public Rectangle(float left, float right, float top, float bottom) {
		// coordinates are (x, y)
		vertices = new float[] {
				left, top,		// top left vertex
				left, bottom,	// bottom left vertex
				right, top,		// top right vertex
				right, bottom	// bottom right vertex
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
	 * Constructs a new rectangle of the given color at the given coordinates.
	 * 
	 * @param left
	 *            x coordinate of the left side.
	 * @param right
	 *            x coordinate of the right side.
	 * @param top
	 *            y coordinate of the top side.
	 * @param bottom
	 *            y coordinate of the bottom side.
	 * @param red
	 *            the red channel of the color.
	 * @param green
	 *            the green channel of the color.
	 * @param blue
	 *            the blue channel of the color.
	 * @param alpha
	 *            the alpha channel (transparency) of the color.
	 */
	public Rectangle(float left, float right, float top, float bottom,
			float red, float green, float blue, float alpha) {
		this(left, right, top, bottom);

		color[0] = red;
		color[1] = green;
		color[2] = blue;
		color[3] = alpha;
	}

	/**
	 * Draws the untextured rectangle.
	 * 
	 * @param gl
	 *            the GL object.
	 */
	public void draw(GL10 gl) {
		// enables the vertex array client state
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		// makes gl point to the vertex buffer
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);

		// sets the rectangle color
		gl.glColor4f(color[0], color[1], color[2], color[3]);

		// draws the vertices as triangle strip
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 2);

		// disables the vertex array client state
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}

	/**
	 * Draws the textured rectangle.
	 * 
	 * @param gl
	 *            the GL object.
	 * @param tex
	 *            the texture to be applied on the square.
	 * 
	 * @see it.zavo.maze.util.Tex enum Tex
	 */
	public void draw(GL10 gl, Tex tex) {
		// enables the vertex array client state
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// enables the texture coordinates array client state
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// makes gl point to the vertex buffer
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);

		// binds the texture to the rectangle
		TextureManager.bindTexture(gl, tex);

		// sets the blending color
		gl.glColor4f(color[0], color[1], color[2], color[3]);

		// draws the vertices as triangle strip
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 2);

		// disables the vertex array client state
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		// disables the texture coordinates array client state
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}
}