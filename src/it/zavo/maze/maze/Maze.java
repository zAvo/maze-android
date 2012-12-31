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

import it.zavo.maze.physics.Physics;
import it.zavo.maze.util.Status;
import it.zavo.maze.util.Tex;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import android.content.Context;

/**
 * Represents the maze. Contains the ball, the walls and the holes.
 * 
 * @author Marco Mandrioli
 */
public class Maze {
	// private String name;
	private float size = 400.0f;

	private Ball ball;
	private ArrayList<Wall> walls = new ArrayList<Wall>();
	private ArrayList<Hole> holes = new ArrayList<Hole>();
	private float[] start;

	/**
	 * Constructs a new maze from an XML file.
	 * 
	 * @param gl
	 *            the GL object.
	 * @param context
	 *            the context in which the resource is.
	 * @param resId
	 *            the resource ID of the XML file containing the maze data.
	 */
	public Maze(GL10 gl, Context context, int resId) {
		try {
			// opens the input stream of the maze's XML file from the resource
			// ID
			InputStream is = context.getResources().openRawResource(resId);

			// parses the document
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			// validates the document against the XML Schema
			// Xml.validate(is);
			Document doc = docBuilder.parse(is);

			// closes input stream
			try {
				is.close();
				is = null;
			} catch (IOException e) {
				e.printStackTrace();
			}

			// normalizes text representation
			doc.getDocumentElement().normalize();

			/* maze */
			// size-x -> size
			size = Float.parseFloat(doc.getDocumentElement().getAttributes()
					.getNamedItem("size-x").getNodeValue());

			/* ball */
			ball = new Ball(size);

			/* start */
			NodeList st = doc.getElementsByTagName("start");
			// size is mapped to (-1, 1), so everything is multiplied by 2
			start = new float[2];
			// x
			start[0] = Float.parseFloat(st.item(0).getAttributes()
					.getNamedItem("x").getNodeValue());
			start[0] = ((start[0] * 2) / size) - 1;
			// y
			start[1] = Float.parseFloat(st.item(0).getAttributes()
					.getNamedItem("y").getNodeValue());
			start[1] = ((start[1] * 2) / size) - 1;

			/* walls */
			NodeList wa = doc.getElementsByTagName("w");
			for (int i = 0; i < wa.getLength(); i++)
				// adds new wall
				walls.add(new Wall(size, Float.parseFloat(wa.item(i)
						.getAttributes().getNamedItem("x1").getNodeValue()),
						Float.parseFloat(wa.item(i).getAttributes()
								.getNamedItem("x2").getNodeValue()), Float
								.parseFloat(wa.item(i).getAttributes()
										.getNamedItem("y1").getNodeValue()),
						Float.parseFloat(wa.item(i).getAttributes()
								.getNamedItem("y2").getNodeValue())));

			/* holes */
			NodeList ho = doc.getElementsByTagName("h");
			float radius;
			Node node;
			for (int i = 0; i < ho.getLength(); i++) {
				node = ho.item(i).getAttributes().getNamedItem("radius");
				// hole's radius can be omitted, so if it's the case we set it
				// to 7.0f
				if (node != null)
					radius = Float.parseFloat(node.getNodeValue());
				else
					radius = 7.0f;

				// adds new hole
				holes.add(new Hole(size, Float.parseFloat(ho.item(i)
						.getAttributes().getNamedItem("x").getNodeValue()),
						Float.parseFloat(ho.item(i).getAttributes()
								.getNamedItem("y").getNodeValue()), radius,
						false));
			}

			/* goal */
			NodeList go = doc.getElementsByTagName("goal");

			node = ho.item(0).getAttributes().getNamedItem("radius");
			if (node != null)
				radius = Float.parseFloat(node.getNodeValue());
			else
				radius = 7.0f;

			// adds goal
			holes.add(new Hole(size, Float.parseFloat(go.item(0)
					.getAttributes().getNamedItem("x").getNodeValue()), Float
					.parseFloat(go.item(0).getAttributes().getNamedItem("y")
							.getNodeValue()), radius, true));

			// initializes physics engine
			Physics.init(this);

		} catch (SAXParseException err) {
			System.out.println("** Parsing error" + ", line "
					+ err.getLineNumber() + ", uri " + err.getSystemId());
			System.out.println(" " + err.getMessage());

		} catch (SAXException e) {
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace();

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Checks if the ball is colliding with an hole of the maze and returns the
	 * according application status.
	 * 
	 * @param position
	 *            an array of two floats that contains the coordinates of the
	 *            ball (x, y).
	 * 
	 * @return {@link it.zavo.maze.util.Status#GAME_OK GAME_OK} if no collision
	 *         is found, {@link it.zavo.maze.util.Status#LEVEL_LOST LEVEL_LOST}
	 *         if collided with a normal hole,
	 *         {@link it.zavo.maze.util.Status#LEVEL_COMPLETE LEVEL_COMPLETE} if
	 *         collided with the goal.
	 * 
	 * @see it.zavo.maze.util.Status
	 */
	public Status checkHolesCollision(float[] position) {
		float[] center;
		float radius;

		// for every hole
		for (Hole h : holes) {
			center = h.getPosition();
			radius = h.getRadius();

			// if both the distances on the x and y axis are smaller than the
			// radius
			if (Math.abs(position[0] - center[0]) < radius
					&& Math.abs(position[1] - center[1]) < radius) {
				// if the hole is the goal
				if (h.isGoal())
					return Status.LEVEL_COMPLETE;
				else
					return Status.LEVEL_LOST;
			}
		}

		return Status.GAME_OK;
	}

	/**
	 * Checks if the ball is colliding with a wall of the maze.
	 * 
	 * @param position
	 *            an array of two floats that contains the coordinates of the
	 *            ball (x, y).
	 * @param speed
	 *            the actual speed of the ball.
	 * @param acceleration
	 *            the actual acceleration of the ball.
	 * @param bounceReduction
	 *            a constant coefficient representing the speed reduction of the
	 *            ball when it bounces against a wall.
	 * 
	 * @return <code>true</code> if a collision is detected, <code>false</code>
	 *         otherwise.
	 * 
	 * @see it.zavo.maze.util.Status
	 */
	public boolean checkWallsCollisions(float[] position, float[] speed,
			float[] acceleration, final float bounceReduction) {
		float[] center;
		float[] halfSize;
		float[] distance = new float[2];

		boolean collision = false;

		float ballRadius = ball.getRadius();

		// for every wall
		for (Wall w : walls) {
			center = w.getCenter();
			halfSize = w.getHalfSize();

			// calculates the distance on the two axes from the central point of
			// the wall
			distance[0] = Math.abs(position[0] - center[0]);
			distance[1] = Math.abs(position[1] - center[1]);

			// if the distance from the border of the wall is greater on the x
			// axis than on the y axis
			if (distance[0] - halfSize[0] >= distance[1] - halfSize[1]) {
				// if the ball penetrates the wall on the x axis
				if (distance[0] < halfSize[0] + ballRadius) {
					// if the ball is on the left of the center of the wall
					if (position[0] <= center[0])
						// flips the amount that penetrates on the other
						// direction
						position[0] = 2 * (center[0] - halfSize[0])
								- position[0] - 2 * ballRadius;
					// if the ball is on the right of the center of the wall
					if (position[0] > center[0])
						// overturns outside of the wall the amount that
						// penetrates
						position[0] = 2 * (center[0] + halfSize[0])
								- position[0] + 2 * ballRadius;

					// multiplies speed on the x axis for the bounce reduction
					// constant and changes the sign
					speed[0] = -(speed[0] * bounceReduction);
					// resets acceleration
					acceleration[0] = 0.0f;

					collision = true;
				}
			}

			// if the distance from the border of the wall is smaller on the x
			// axis than on the y axis
			if (distance[0] - halfSize[0] <= distance[1] - halfSize[1]) {
				// if the ball penetrates the wall on the y axis
				if (distance[1] < halfSize[1] + ballRadius) {
					// if the ball is above the center of the wall
					if (position[1] <= center[1])
						// overturns outside of the wall the amount that
						// penetrates
						position[1] = 2 * (center[1] - halfSize[1])
								- position[1] - 2 * ballRadius;
					// if the ball is under the center of the wall
					if (position[1] > center[1])
						// overturns outside of the wall the amount that
						// penetrates
						position[1] = 2 * (center[1] + halfSize[1])
								- position[1] + 2 * ballRadius;

					// multiplies speed on the y axis for the bounce reduction
					// constant and changes the sign
					speed[1] = -(speed[1] * bounceReduction);
					// resets acceleration
					acceleration[1] = 0.0f;

					collision = true;
				}
			}
		}

		return collision;
	}

	/**
	 * Draws the entire maze structure, with the ball at the specified
	 * coordinates.
	 * 
	 * @param gl
	 *            the GL object.
	 * @param ballX
	 *            the x coordinate of the ball.
	 * @param ballY
	 *            the y coordinate of the ball.
	 */
	public void draw(GL10 gl, float ballX, float ballY) {
		// for every wall
		for (Wall w : walls)
			w.draw(gl);

		// for every hole
		for (Hole h : holes)
			h.draw(gl);

		// draws the ball
		ball.draw(gl, Tex.BALL, ballX, ballY);
	}

	/**
	 * Returns the radius of the ball.
	 * 
	 * @return the radius of the ball.
	 */
	public float getBallRadius() {
		return ball.getRadius();
	}

	/**
	 * Returns the speed multiplier of the ball.
	 * 
	 * @return the speed multiplier of the ball.
	 */
	public float getBallSpeed() {
		return ball.getSpeed();
	}

	/**
	 * Return the starting coordinates of the ball in this maze.
	 * 
	 * @return an array of two floats representing the x and y coordinates of
	 *         the starting point.
	 */
	public float[] getStart() {
		return start.clone();
	}
}