package uk.ac.horizon.ug;

import java.util.ArrayList;
import java.util.List;

public class Zone
{
	String ref;
	
	Integer id;
	
	private String name;
	
	Integer polygon;	//boolean - default = 1
	
	Float radius;

	private List<Field> fields = new ArrayList<Field>();

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public List<Field> getFields() {
		return fields;
	}
	
	/*
    <field name="attributes" type="attributeSet">
      <attributeSet ref="1798XK">
        <health>0</health>
        <wealth>0</wealth>
        <brains>0</brains>
        <action>0</action>
        <instant>1</instant>
        <flags>15</flags>
        <absolute>1</absolute>
        <field name="healthOp" type="attributeOp"/>
        <field name="wealthOp" type="attributeOp"/>
        <field name="brainsOp" type="attributeOp"/>
        <field name="actionOp" type="attributeOp"/>
      </attributeSet>
    </field>
    
    <field name="coords">0.064373,51.494851,0 
0.075617,51.496187,0 
0.076389,51.495973,0 
0.080681,51.498164,0 
0.081367,51.497843,0 
0.082655,51.496721,0 
0.08317,51.496347,0 
0.082741,51.494904,0 
0.082054,51.494477,0 
0.081625,51.494263,0 
0.083342,51.49266,0 
0.085487,51.492713,0 
0.085917,51.4925,0 
0.086002,51.490896,0 
0.085058,51.490041,0 
0.085831,51.489667,0 
0.084801,51.487583,0 
0.086946,51.487155,0 
0.086432,51.485445,0 
0.08729,51.485231,0 
0.085745,51.482666,0 
0.085831,51.481329,0 
0.084801,51.4801,0 
0.07978,51.479993,0 
0.076818,51.479779,0 
0.073943,51.479726,0 
0.071669,51.479245,0 
0.069051,51.478443,0 
0.064888,51.477588,0 
0.058966,51.478496,0 
0.056305,51.480955,0 
0.052786,51.480314,0 
0.052185,51.486568,0 
0.04961,51.48785,0 
0.048409,51.490683,0 
0.047207,51.491698,0 
0.045877,51.49266,0 
0.045791,51.495198,0 </field> */
}
