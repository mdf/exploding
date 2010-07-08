package uk.ac.horizon.ug;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XStreamTest
{
	public static void main(String args[])
	{
		XStreamTest t = new XStreamTest();
		t.test();
	}
	
	public XStreamTest()
	{
		
	}
	
	public void test()
	{
		String filename = "//home//martin//gameState.xml";
		
		XStream xstream = new XStream(new DomDriver());
		
		xstream.alias("gameState", GameState.class);
		xstream.aliasAttribute(GameState.class, "version", "version");
		
		xstream.alias("timeEvent", TimeEvent.class);
		xstream.aliasAttribute(TimeEvent.class, "ref", "ref");

		xstream.alias("zone", Zone.class);
		xstream.aliasAttribute(Zone.class, "ref", "ref");

		xstream.alias("indexList", IndexList.class);
		xstream.aliasAttribute(IndexList.class, "ref", "ref");

		xstream.alias("attributeSet", AttributeSet.class);
		xstream.aliasAttribute(AttributeSet.class, "ref", "ref");
		xstream.addImplicitCollection(AttributeSet.class,"fields", "field", Field.class);

		xstream.addImplicitCollection(Zone.class,"fields", "field", Field.class);
		xstream.registerConverter(new FieldConverter());
		
		try
		{
			InputStreamReader in = new InputStreamReader(new FileInputStream(filename));

			GameState gameState = (GameState) xstream.fromXML(in);
			
			// print some bits to make sure we've worked...
			System.err.println(gameState.getStartYear());
			System.err.println(gameState.getVersion());

			for(Zone z : gameState.getZones())
			{
				System.err.println(z.getName());
				
				for(Field f : z.getFields())
				{
					System.err.println(f.getName());
					System.err.println(f.getType());
					
					if("attributes".equals(f.getName()))
					{
						System.err.println(f.getAttributeSet());
						System.err.println(f.getAttributeSet().getFlags());
					}
					else if("coords".equals(f.getName()))
					{
						for(Coordinate c : f.getCoordinates())
						{
							System.err.println(c.getLatitude() + " " + c.getLongitude() + " " + c.getElevation());
						}
					}
				}
			}
			
			for(TimeEvent t : gameState.getTimeEvents())
			{
				System.err.println(t.getStartTime());
			}
		}
		catch(IOException e)
		{
			System.err.println(e);
			e.printStackTrace();
		}
		
	}
}
