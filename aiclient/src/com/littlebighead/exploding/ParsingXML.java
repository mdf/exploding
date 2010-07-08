//package org.anddev.android.parsingxml;
package com.littlebighead.exploding;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ParsingXML extends Activity {
	
	private final String MY_DEBUG_TAG = "ParsingXML";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		/* Create a new TextView to display the parsingresult later. */
		TextView tv = new TextView(this);
		try {
			/* Create a URL we want to load some xml-data from. */
			URL url = new URL("http://www.anddev.org/images/tut/basic/parsingxml/example.xml");

			/* Get a SAXParser from the SAXPArserFactory. */
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();

			/* Get the XMLReader of the SAXParser we created. */
			XMLReader xr = sp.getXMLReader();
			/* Create a new ContentHandler and apply it to the XML-Reader*/ 
			GameEventXMLHandler myXMLHandler = new GameEventXMLHandler();
			xr.setContentHandler(myXMLHandler);
			
			/* Parse the xml-data from our URL. */
			xr.parse(new InputSource(url.openStream()));
			/* Parsing has finished. */

			/* Our ExampleHandler now provides the parsed data to us. */
			ParsedXMLDataSet parsedExampleDataSet = 
				myXMLHandler.getParsedData();

			/* Set the result to be displayed in our GUI. */
			tv.setText(parsedExampleDataSet.toString());
			
		} catch (Exception e) {
			/* Display any Error to the GUI. */
			tv.setText("Error: " + e.getMessage());
			Log.e(MY_DEBUG_TAG, "WeatherQueryError", e);
		}
		/* Display the TextView. */
		this.setContentView(tv);
	}
}