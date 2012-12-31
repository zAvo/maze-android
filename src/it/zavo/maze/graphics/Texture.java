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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

/**
 * A single 2D texture.
 * 
 * @author Marco Mandrioli
 */
public class Texture {
	private int textureID;
	private int[] texture;

	private FloatBuffer textureBuffer;

	/**
	 * Constructs a texture, setting its resource ID.
	 * 
	 * @param resID
	 *            the resource identifier of the drawable file to be used as a
	 *            texture.
	 */
	public Texture(int resID) {
		textureID = resID;
	}

	/**
	 * Binds the texture and its texture coordinates buffer to the gl context.
	 * 
	 * @param gl
	 *            the GL object.
	 */
	public void bind(GL10 gl) {
		// binds the texture
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[0]);

		// makes gl point to the texture coordinates buffer
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
	}

	/**
	 * Loads the texture. This should be called by the
	 * {@link Graphics#onSurfaceCreated(GL10 gl, EGLConfig config)
	 * onSurfaceCreated(...)}.
	 * 
	 * @param gl
	 *            the GL object.
	 * @param context
	 *            the context the context in which the drawable resource is.
	 * 
	 * @see Graphics#onSurfaceCreated(GL10 gl, EGLConfig config)
	 */
	public void load(GL10 gl, Context context) {
		float[] textureVertices = {
				0.0f, 0.0f,	// top left vertex
				0.0f, 1.0f,	// bottom left vertex
				1.0f, 0.0f,	// top right vertex
				1.0f, 1.0f	// bottom right vertex
		};

		texture = new int[1];

		// constructs a buffer of bytes to store the texture's vertices array
		// (float = 4 bytes)
		ByteBuffer byteBuf = ByteBuffer
				.allocateDirect(textureVertices.length * 4);
		// sets the byte order to the platform's (Little Endian or Big Endian)
		byteBuf.order(ByteOrder.nativeOrder());
		// converts the byte buffer to a vertex buffer (float)
		textureBuffer = byteBuf.asFloatBuffer();
		// puts the vertices in the texture's vertex buffer
		textureBuffer.put(textureVertices);
		// sets the position of the texture's vertex buffer to the start
		textureBuffer.position(0);

		// opens the input stream of the texture's resource from the resource ID
		InputStream is = context.getResources().openRawResource(textureID);
		// decodes the input stream to a bitmap
		Bitmap bitmap = BitmapFactory.decodeStream(is);

		// closes input stream
		try {
			is.close();
			is = null;
		} catch (IOException e) {
			e.printStackTrace();
		}

		// generates the texture
		gl.glGenTextures(1, texture, 0);
		// binds the texture
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[0]);

		// creates nearest filtered texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);

		// sets the texture parameters
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_CLAMP_TO_EDGE);

		// determines the internalFormat and type of the bitmap
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		// frees the native object associated to the bitmap
		bitmap.recycle();
	}
}