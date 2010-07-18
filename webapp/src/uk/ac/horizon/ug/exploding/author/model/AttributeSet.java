package uk.ac.horizon.ug.exploding.author.model;

import java.util.ArrayList;
import java.util.List;

public class AttributeSet
{
	public String ref;
	
	public Integer health;
	
	public Integer wealth;
	
	public Integer brains;
	
	public Integer action;
	
	public Integer instant;
	
	public Integer flags;
	
	public Integer absolute;

	public List<Field> fields = new ArrayList<Field>();
	
	public HealthOp healthOp;

	public WealthOp wealthOp;

	public ActionOp actionOp;

	public BrainsOp brainsOp;
}
