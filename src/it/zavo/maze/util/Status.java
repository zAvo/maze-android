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

/**
 * The possible statuses of the application.
 * 
 * @author Marco Mandrioli
 */
public enum Status {
	/** The game is currently running. */
	GAME_OK,
	/** The game is awaiting for the user to restart the level. */
	LEVEL_LOST,
	/** The game is awaiting for the user to start the next level. */
	LEVEL_COMPLETE,
	/** The game is awaiting for the user to restart from the beginning. */
	GAME_OVER,
	/** The game should load / is loading the next level. */
	INITIALIZE_NEW_LEVEL,
	/** The game should reset / is resetting the level. */
	RESET_LEVEL
}