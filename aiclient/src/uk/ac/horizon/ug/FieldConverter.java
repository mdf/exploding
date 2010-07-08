package uk.ac.horizon.ug;

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
		
        field.setName(reader.getAttribute("name"));
        field.setType(reader.getAttribute("type"));

        if("coords".equals(field.getName()))
        {
        	String coordinates = reader.getValue();
        	
        	StringTokenizer sto = new StringTokenizer(coordinates, ",\n");
        	
        	while(sto.hasMoreElements())
        	{
        		Coordinate c = new Coordinate();
        		
        		c.setLongitude(Double.parseDouble(sto.nextToken().trim()));
        		c.setLatitude(Double.parseDouble(sto.nextToken().trim()));
        		c.setElevation(Double.parseDouble(sto.nextToken().trim()));
        		
        		field.getCoordinates().add(c);
        	}
        }
        else if("attributes".equals(field.getName()))
        {
        	reader.moveDown();
        	field.setAttributeSet((AttributeSet)context.convertAnother(field, AttributeSet.class));
        	reader.moveUp();
        }
		
		return field;
	}
}
