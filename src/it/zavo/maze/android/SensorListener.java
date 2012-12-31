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


package it.zavo.maze.android;

import it.zavo.maze.physics.Physics;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Receives notifications from the SensorManager when sensor values have
 * changed.
 * 
 * @author Marco Mandrioli
 */
public class SensorListener implements SensorEventListener {
	private float[] orientation = new float[3];
	private float[] gravity = new float[3];
	private float[] geomag = new float[3];

	private float[] rmat = new float[16];

	public SensorListener() {
	}

	/** Called when the accuracy of a sensor has changed. */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	/** Called when sensor values have changed. */
	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			gravity = event.values.clone();
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			geomag = event.values.clone();
			break;
		}

		// if gravity and geomag have values then find rotation matrix
		if (gravity != null && geomag != null)
			// checks that the rotation matrix is found
			if (SensorManager.getRotationMatrix(rmat, null, gravity, geomag))
				orientation = SensorManager.getOrientation(rmat, orientation);

		// update data in Physics library
		Physics.updateOrientation(orientation);
	}
}