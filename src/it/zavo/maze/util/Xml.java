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

import java.io.File;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;

/**
 * Static class implementing the methods for the handling of the XML files.
 * 
 * @author Marco Mandrioli
 */
public class Xml {
	/**
	 * Performs a validation check against the XML Schema of a maze.
	 * 
	 * @param inputStream
	 *            the input stream of the XML file to validate.
	 * 
	 * @return <code>true</code> if the file validates correctly,
	 *         <code>false</code> otherwise.
	 */
	public static boolean validate(InputStream inputStream) {
		// hook up org.xml.sax.ErrorHandler implementation.
		// schemaFactory.setErrorHandler( myErrorHandler );

		try {
			// build an XSD-aware SchemaFactory
			SchemaFactory schemaFactory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			// get the custom xsd schema describing the required format for my
			// XML files.
			Schema schemaXSD = schemaFactory.newSchema(new File(
					"/raw/schema/maze.xsd"));

			// Create a Validator capable of validating XML files according to
			// my custom schema.
			Validator validator = schemaXSD.newValidator();

			// Get a parser capable of parsing vanilla XML into a DOM tree
			DocumentBuilder parser = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();

			// parse the XML purely as XML and get a DOM tree represenation.
			Document document = parser.parse(inputStream);

			// parse the XML DOM tree againts the stricter XSD schema
			validator.validate(new DOMSource(document));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}
}