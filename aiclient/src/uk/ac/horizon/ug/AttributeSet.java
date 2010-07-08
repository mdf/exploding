package uk.ac.horizon.ug;

import java.util.ArrayList;
import java.util.List;

public class AttributeSet
{
	String ref;
	
	Integer health;
	
	Integer wealth;
	
	Integer brains;
	
	Integer action;
	
	Integer instant;
	
	private Integer flags;
	
	Integer absolute;

	List<Field> fields = new ArrayList<Field>();

	public void setFlags(Integer flags) {
		this.flags = flags;
	}

	public Integer getFlags() {
		return flags;
	}
}
