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


package it.zavo.maze.util;

import it.zavo.maze.R;

/**
 * The textures used in the application.
 * 
 * @author Marco Mandrioli
 */
public enum Tex {
	/** The maze board background texture. */
	BOARD(0, R.drawable.board),
	/** The ball texture. */
	BALL(1, R.drawable.ball),
	/** The texture with the message that is shown when a level is completed. */
	LEVEL_COMPLETE(2, R.drawable.level_complete),
	/** The texture with the message that is shown when the player loses. */
	LEVEL_LOST(3, R.drawable.level_lost);

	/**
	 * the index of the texture as stored by the
	 * {@link it.zavo.maze.graphics.TextureManager TextureManager}.
	 * 
	 * @see it.zavo.maze.graphics.TextureManager TextureManager
	 */
	public final int index;

	/** the ID of the drawable resource. */
	public final int resID;

	/**
	 * Constructs the enum.
	 * 
	 * @param index
	 *            the index of the texture as stored by the
	 *            {@link it.zavo.maze.graphics.TextureManager TextureManager}.
	 * @param resID
	 *            the ID of the drawable resource.
	 * 
	 * @see it.zavo.maze.graphics.TextureManager TextureManager
	 */
	private Tex(int index, int resID) {
		this.index = index;
		this.resID = resID;
	}
}