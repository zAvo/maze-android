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

import it.zavo.maze.util.Tex;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

/**
 * Static class that manages the textures.
 * 
 * @author Marco Mandrioli
 */
public class TextureManager {
	private static ArrayList<Texture> textures;

	/**
	 * Initializes the textures, taking the data from the
	 * {@link it.zavo.maze.util.Tex enum Tex}.
	 * 
	 * @see it.zavo.maze.util.Tex enum Tex
	 */
	public static void init() {
		textures = new ArrayList<Texture>();
		// adds a new Texture for each entry in the Tex enum.
		for (Tex t : Tex.values())
			textures.add(new Texture(t.resID));
	}

	/**
	 * Loads the textures, calling the {@link Texture#load(GL10, Context)
	 * load(GL10 gl, Context context)} methods of every texture.
	 * 
	 * @param gl
	 *            the GL object.
	 * @param context
	 *            the context in which the drawable resources are.
	 * 
	 * @see Texture#load(GL10 gl, Context context)
	 */
	public static void loadTextures(GL10 gl, Context context) {
		// initializes the texture manager
		init();

		// calls the load method of every Texture.
		for (Texture t : textures)
			t.load(gl, context);
	}

	/**
	 * Calls the {@link Texture#bind(GL10) bind(GL10 gl)} method of the
	 * requested texture.
	 * 
	 * @param gl
	 * @param tex
	 * 
	 * @see Texture#bind(GL10 gl)
	 * @see it.zavo.maze.util.Tex enum Tex
	 */
	public static void bindTexture(GL10 gl, Tex tex) {
		textures.get(tex.index).bind(gl);
	}
}