package uk.ac.horizon.ug.exploding.author.model;

import java.util.StringTokenizer;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class FieldConverter implements Converter
{
	public boolean canConvert(Class clazz)
	{
		return clazz.equals(Field.class);
	}

	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context)
	{
		
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
	{		
		Field field = new Field();
		
        field.name = reader.getAttribute("name");
        field.type = reader.getAttribute("type");

        if("coords".equals(field.name))
        {
        	String coordinates = reader.getValue();
        	
        	StringTokenizer sto = new StringTokenizer(coordinates, ",\n");
        	
        	while(sto.hasMoreElements())
        	{
        		Coordinate c = new Coordinate();
        		
        		c.longitude = Double.parseDouble(sto.nextToken().trim());
        		c.latitude = Double.parseDouble(sto.nextToken().trim());
        		c.elevation = Double.parseDouble(sto.nextToken().trim());
        		
        		field.coordinates.add(c);
        	}
        }
        else if("attributes".equals(field.name))
        {
        	reader.moveDown();
        	field.attributeSet = (AttributeSet)context.convertAnother(field, AttributeSet.class);
        	reader.moveUp();
        }
		
		return field;
	}
}
