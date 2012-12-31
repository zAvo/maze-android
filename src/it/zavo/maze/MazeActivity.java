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


package it.zavo.maze;

import it.zavo.maze.android.SensorListener;
import it.zavo.maze.graphics.Graphics;
import it.zavo.maze.util.Status;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

/**
 * The main activity.
 * 
 * @author Marco Mandrioli
 */
public class MazeActivity extends Activity {
	SensorListener sensorListener;
	SensorManager sm;
	GLSurfaceView glSurface;

	private static Status status;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		status = Status.INITIALIZE_NEW_LEVEL;

		// prevents the device from disabling the screen
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// gets reference to SensorManager
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorListener = new SensorListener();

		// registers the listeners
		sm.registerListener(sensorListener,
				sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);
		sm.registerListener(sensorListener,
				sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_GAME);

		// sets the window to full screen with no title
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// creates the gl surface and sets the renderer
		glSurface = new GLSurfaceView(this);
		glSurface.setRenderer(new Graphics(this));

		// sets the content to the new gl surface
		setContentView(glSurface);
	}

	/** Called when the activity is going into the background. */
	@Override
	protected void onPause() {
		glSurface.onPause();

		// unregisters listener
		sm.unregisterListener(sensorListener);

		super.onPause();
	}

	/** Called when the activity is restarting. */
	@Override
	protected void onResume() {
		super.onResume();

		glSurface.onResume();

		// registers this class as a listener for the orientation sensor
		sm.registerListener(sensorListener,
				sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);
		sm.registerListener(sensorListener,
				sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_GAME);
	}

	/** Called when the activity is no longer visible to the user. */
	@Override
	protected void onStop() {
		glSurface.onPause();
		
		// unregisters listener
		sm.unregisterListener(sensorListener);

		super.onStop();
	}

	/** Called when a touch event happens. */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		// if the status is not GAME_OK, changes the status when a tap on the
		// screen occurs
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (MazeActivity.status == Status.GAME_OK
					|| MazeActivity.status == Status.LEVEL_LOST) {
				MazeActivity.status = Status.RESET_LEVEL;
			} else if (MazeActivity.status == Status.LEVEL_COMPLETE) {
				MazeActivity.status = Status.INITIALIZE_NEW_LEVEL;
			}
			
			return true;
		}

		return false;
	}

	/**
	 * Returns the application status.
	 * 
	 * @return the status of the application.
	 * 
	 * @see it.zavo.maze.util.Status enum Status
	 */
	public static Status getStatus() {
		return status;
	}

	/**
	 * Sets the status of the application to
	 * {@link it.zavo.maze.util.Status#GAME_OK GAME_OK}.
	 * 
	 * @see it.zavo.maze.util.Status enum Status
	 */
	public static void setGameOk() {
		MazeActivity.status = Status.GAME_OK;
	}

	/**
	 * Sets the status of the application to
	 * {@link it.zavo.maze.util.Status#LEVEL_COMPLETE LEVEL_COMPLETE}.
	 * 
	 * @see it.zavo.maze.util.Status enum Status
	 */
	public static void setLevelComplete() {
		MazeActivity.status = Status.LEVEL_COMPLETE;
	}

	/**
	 * Sets the status of the application to
	 * {@link it.zavo.maze.util.Status#LEVEL_LOST LEVEL_LOST}.
	 * 
	 * @see it.zavo.maze.util.Status enum Status
	 */
	public static void setLevelLost() {
		MazeActivity.status = Status.LEVEL_LOST;
	}
}