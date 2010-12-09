<!-- configuration.jsp - different for each application !! -->

<%@ page import="java.util.*" %>
<%@ page import="equip2.core.objectsupport.IStructuredObjectHelper" %>
<%@ page import="equip2.core.objectsupport.IObjectHelper" %>
<%@ page import="equip2.core.objectsupport.IObjectHelperRegistry" %>
<%@ page import="equip2.core.objectsupport.IElement" %>
<%@ page import="equip2.naming.InitialContext" %>
<%@ page import="equip2.spring.PropertyGroupBean" %>
<%@ page import="equip2.spring.EnumTypeBean" %>
<%@ page import="equip2.spring.LinkedClassBean" %>
<%@ page import="org.apache.log4j.Logger" %>

<%!
  static Logger logger = Logger.getLogger("equip2.db.jsp.configuration");
  // declarations
  static Map _listProperties;
  static Map _propertyGroups;
  static Map _otherProperties;
  static Map _fixedProperties;
  static Map _defaultFixedProperties;
  static Map _linkedClasses;
  static Map _setValuedProperties;
  static Map _arrayValuedProperties;
  static Map _niceNames;
  static Map _niceListHeadings;
  static Map _enumProperties;
  static Map _bitFlagProperties;
  static Map _maxLengthProperties;
  static Map _textAreaProperties;
  static String [] _allClassNames;
  static {
  
  	_allClassNames = new String[] {
   		// all class names to appear on forms go here
		"uk.ac.horizon.ug.exploding.db.ClientConversation",
		"uk.ac.horizon.ug.exploding.db.ContentGroup",
		"uk.ac.horizon.ug.exploding.db.Game",
		"uk.ac.horizon.ug.exploding.db.GameConfig",
		"uk.ac.horizon.ug.exploding.db.GameTime",
		"uk.ac.horizon.ug.exploding.db.Member",
		"uk.ac.horizon.ug.exploding.db.Message",
		"uk.ac.horizon.ug.exploding.db.MessageToClient",
		"uk.ac.horizon.ug.exploding.db.Player",
		"uk.ac.horizon.ug.exploding.db.TimelineEvent",
		"uk.ac.horizon.ug.exploding.db.Zone"
  	};
 
    _listProperties = new HashMap();
    // Map<String(classname),String[](propertyname)>
    // Player list views show 
	_listProperties.put(uk.ac.horizon.ug.exploding.db.ClientConversation.class.getName(), 
		new String[] { "active", "clientID", "gameID", "playerID" });
	_listProperties.put(uk.ac.horizon.ug.exploding.db.MessageToClient.class.getName(), 
			new String[] { "conversationID", "clientID", "gameID", "time", "type" });
	_listProperties.put(uk.ac.horizon.ug.exploding.db.Message.class.getName(), 
			new String[] { "playerID", "type", "title", "description", "createTime", "handled" });
	_listProperties.put(uk.ac.horizon.ug.exploding.db.Player.class.getName(), 
			new String[] { "gameID", "name", "positionUpdateTime" });
	_listProperties.put(uk.ac.horizon.ug.exploding.db.Member.class.getName(), 
			new String[] { "gameID", "playerID", "zone", "parentMemberID", "carried" });
	/*
	_listProperties.put(mrl.compliant.db.Player.class.getName(), 
		new String[] { "name", "stateID", "active", "phoneID" });
	_listProperties.put(mrl.compliant.db.PerformerRole.class.getName(), 
		new String[] { "name" });
	_listProperties.put(mrl.compliant.db.ScriptAction.class.getName(), 
		new String[] { "name", "actionType" });
	_listProperties.put(mrl.compliant.db.State.class.getName(), 
		new String[] { "name" });
	_listProperties.put(mrl.compliant.db.StateEvent.class.getName(), 
		new String[] { "date", "handled", "playerID", "stateID", "performerRoleID" });				
	_listProperties.put(mrl.asterisk.db.CallAction.class.getName(), 
		new String[] { "name" });		
	_listProperties.put(mrl.asterisk.db.CallScript.class.getName(), 
		new String[] { "name" });
	_listProperties.put(mrl.asterisk.db.Call.class.getName(), 
		new String[] { "active", "dateCreated", "phoneID", "callState", "originating" });
	_listProperties.put(mrl.asterisk.db.CallEvent.class.getName(), 
		new String[] { "date", "callState", "callID", "callActionID" });											
	_listProperties.put(mrl.asterisk.db.Phone.class.getName(), 
		new String[] { "name", "phoneNumber" });	
		*/		
		
	_propertyGroups = new HashMap();
	// Map<String(classname),PropertyGroupBean[](propertygroups)>	
	// ....

     _otherProperties = new HashMap();
	// ....

    _fixedProperties = new HashMap();
	// Map<String(classname),String[](propertynames)>
	// ....
    _defaultFixedProperties = makeMap(new String[] {"ID"});

    _linkedClasses = new HashMap();
	// Map<String(classname),LinkedClassBean[](linkedclasses)>   
	// Players are referred to Topics (creatorPlayerID) and Comments (creatorPlayerID and moderatorPlayerID):
	//	_linkedClasses.put(mrl.compliant.db.Player.class.getName(),
	//	new LinkedClassBean[] { 
	//		new LinkedClassBean(mrl.asterisk.db.Phone.class.getName(), "phoneID", 
	//		true, "name", false),
	//		new LinkedClassBean(equip2.webapptutorial.db.Comment.class.getName(), "creatorPlayerID", 
	//			true, "dateCreated", false),
	//		new LinkedClassBean(equip2.webapptutorial.db.Comment.class.getName(), "creatorPlayerID", 
	//			true, "dateStatusChange", false)
	//});
	//	_linkedClasses.put(mrl.asterisk.db.CallScript.class.getName(),
	//	new LinkedClassBean[] {
	//	    new LinkedClassBean(mrl.asterisk.db.CallAction.class.getName(), "actionIDs",
	//		    true, "name", false)
	//});
	// ....

    // properties whose type is set (of id)
    _setValuedProperties = new HashMap();
	// ....

	// properties whose type is set (of id)
    _arrayValuedProperties = new HashMap();
    
	//_arrayValuedProperties.put(mrl.asterisk.db.CallScript.class.getName(),
	//        makeMap(new String[] { "actionIDs" }));

	//_arrayValuedProperties.put(mrl.compliant.db.PerformerRole.class.getName(),
	//        makeMap(new String[] { "scriptActionIDs", "stateIDs" }));

	//_arrayValuedProperties.put(mrl.compliant.db.ScriptAction.class.getName(),
	//        makeMap(new String[] { "childScriptActionIDs" }));

    // properties whose type is an int which is really an enumeration
    _enumProperties = new HashMap();
	
	/* Map map;
    // Map<String(classname),Map<String(propertyname),EnumTypeBean[](enumvalues)>>
    // Player status & role
    EnumTypeBean [] scriptActionEnum = new EnumTypeBean[] {
   	       new EnumTypeBean(0, "SCRIPT_ACTION_CALLSCRIPT"), 
   		   new EnumTypeBean(1, "SCRIPT_ACTION_STATE"),
   		   new EnumTypeBean(2, "SCRIPT_ACTION_PREF"),
   	       new EnumTypeBean(3, "SCRIPT_ACTION_ANNOTATION") };
    map = new HashMap();
    map.put("actionType", scriptActionEnum);
    _enumProperties.put(mrl.compliant.db.ScriptAction.class.getName(), map);
 
	EnumTypeBean [] stateActionEnum = new EnumTypeBean[] {
   	       new EnumTypeBean(0, "STATE_ACTION_NOOP"), 
   		   new EnumTypeBean(1, "STATE_ACTION_NORMAL"),
   		   new EnumTypeBean(2, "STATE_ACTION_SLOW") };
    map = new HashMap();
    map.put("actionOnTimeout", stateActionEnum);
    map.put("actionOnState", stateActionEnum);
    _enumProperties.put(mrl.compliant.db.State.class.getName(), map);
    */
    
	// ....

    _bitFlagProperties = new HashMap();
	// ....

	// nice names
    _niceNames = new HashMap();    
	// Map<String(classname),Map<String(propertyname),String(displayname)>>
   	//_niceNames.put(mrl.asterisk.db.CallAction.class.getName(),
	//	makeMap2(new String[] {"action","play - record - record-start - record-stop - transfer" }));
	// ....
       
	// nice list headings
    _niceListHeadings = new HashMap();    
	// Map<String(classname),Map<String(propertyname),String(displayname)>>
	// ....
       
	// max length properties: classname -> Map: propertyname -> Integer
    _maxLengthProperties = new HashMap();    
	// Map<String(classname),Map<String(propertyname),String(length)>>
    //_maxLengthProperties.put(equip2.webapptutorial.db.Topic.class.getName(),
	 //        makeMap2(new String[] { "title", "100" }));
	// ....
         
	// text area properties: classname -> Map: propertyname -> Integer (rows)
    _textAreaProperties = new HashMap();    
	// Map<String(classname),Map<String(propertyname),String(length)>>
	// ....
   }
   static Map makeMap(String[] ss) {
     Map map = new HashMap();
     for (int i=0;i<ss.length; i++)
       map.put(ss[i], ss[i]);
     return map;
   }
   static Map makeMap2(String[] ss) {
	     Map map = new HashMap();
	     for (int i=0;i<ss.length; i+=2)
	       map.put(ss[i], ss[i+1]);
	     return map;
	   }
   static Map makeMap(String[] ss, String [] ss2) {
     Map map = new HashMap();
     for (int i=0;i<ss.length; i++)
       map.put(ss[i], ss2[i]);
     return map;
   }
%>

<%
  // This is now standard stuff, configured by the above!
  
  request.setAttribute("allclassnames", _allClassNames);
  // requestobject is in request scope
  Object requestobject = request.getAttribute("requestobject");
  if (requestobject != null && !(requestobject instanceof equip2.core.QueryTemplate)) {
    String classname = requestobject.getClass().getName();
    // names of properties in list view
    request.setAttribute("listpropertynames", _listProperties.get(classname));
    request.setAttribute("alllistpropertynames", _listProperties);
    request.setAttribute("linkedclasses", _linkedClasses.get(classname));
    request.setAttribute("setvaluedpropertynames", _setValuedProperties.get(classname));
    request.setAttribute("arrayvaluedpropertynames", _arrayValuedProperties.get(classname));
    request.setAttribute("enumproperties", _enumProperties.get(classname));
    request.setAttribute("allenumproperties", _enumProperties);
    request.setAttribute("bitflagproperties", _bitFlagProperties.get(classname));
    request.setAttribute("allbitflagproperties", _bitFlagProperties);
    request.setAttribute("nicenames", _niceNames.get(classname));
    request.setAttribute("allnicenames", _niceNames);
    request.setAttribute("nicelistheadings", _niceListHeadings.get(classname));
    request.setAttribute("allnicelistheadings", _niceListHeadings);
    request.setAttribute("maxlengthproperties", _maxLengthProperties.get(classname));
    request.setAttribute("textareaproperties",_textAreaProperties.get(classname));

    // names of fixed properties 
    if (_fixedProperties.containsKey(classname))
      request.setAttribute("fixedpropertynames", _fixedProperties.get(classname));
    else
      request.setAttribute("fixedpropertynames", _defaultFixedProperties);
    
    // property groups
    PropertyGroupBean [] propertygroups = (PropertyGroupBean[])_propertyGroups.get(classname);
    request.setAttribute("propertygroups", propertygroups);
  
    PropertyGroupBean otherProperties = (PropertyGroupBean)_otherProperties.get(classname);
    if (otherProperties==null) {
      synchronized (_otherProperties) {
        otherProperties = new PropertyGroupBean("Other properties");
        Set v = new TreeSet();
        // actual class - for checking component properties
        Class clazz = null;
        try {
	    clazz = Class.forName(classname);
	} catch (Exception ex) {
	    logger.error("Loading class "+classname, ex);
	}
	// registry - for checking component properties
	IObjectHelperRegistry registry = null;
	try {
	    registry = (IObjectHelperRegistry)(new InitialContext().lookup(IObjectHelperRegistry.JNDI_DEFAULT_NAME));
	} catch (Exception rnfe) {
	    logger.error("No object helper registry registered", rnfe);
	}
	// properties...
        IStructuredObjectHelper shelper = (IStructuredObjectHelper)request.getAttribute("objecthelper");
        Enumeration ee = shelper.getIElements(requestobject);
        while(ee.hasMoreElements()) {
          IElement ie = (IElement)ee.nextElement();
          String pname = ie.getKey().toString();
          boolean found = false;
          // complex type?
          if (clazz!=null && registry!=null && pname.length()>0 && Character.isLetter(pname.charAt(0))) {
              String getterName = "get"+Character.toUpperCase(pname.charAt(0))+pname.substring(1);
              try {
                  java.lang.reflect.Method m = clazz.getDeclaredMethod(getterName, new Class[0]);
                  Class rc = m.getReturnType();
                  IObjectHelper phelper = registry.getClassHelper(rc);
                  //logger.info("class "+clazz+" property "+pname+" getter "+getterName+" has return type "+rc+" and helper "+phelper+
                  //  (phelper instanceof IStructuredObjectHelper ? " (structured)" : " (unstructured)"));
                  if (phelper instanceof IStructuredObjectHelper && !java.util.Date.class.isAssignableFrom(rc) && !(phelper instanceof equip2.core.objectsupport.impl.ArrayObjectHelper)) {
                    IStructuredObjectHelper sphelper = (IStructuredObjectHelper)phelper;
		    Object pv = null;
                    try {
                      pv = rc.newInstance();
		      Enumeration ee2 = sphelper.getIElements(pv);
		      while(ee2.hasMoreElements()) {
			  IElement ie2 = (IElement)ee2.nextElement();
			  String pname2 = pname+"."+ie2.getKey().toString();
			  boolean found2 = false;
			  for (int i=0; !found && propertygroups!=null && i<propertygroups.length; i++) 
			    for (int j=0; !found && j<propertygroups[i].getProperties().length; j++)
			      if (pname2.equals(propertygroups[i].getProperties()[j]))
				found2 = true;
	      
			  if (!found2) { 
			    v.add(pname2);
			  }
		      }
		      // done as complex
                      continue;

                    } catch (Exception nie) {
                      logger.error("Could not make template instance of complex property type "+rc, nie);
                      continue;
                    }
                  }
              } catch (Exception nsme) {
                  logger.warn("could not find expected getter method "+getterName+" for class "+classname+" property "+pname, nsme);
              }
          }
          // carry on
          for (int i=0; !found && propertygroups!=null && i<propertygroups.length; i++) 
            for (int j=0; !found && j<propertygroups[i].getProperties().length; j++)
              if (pname.equals(propertygroups[i].getProperties()[j]))
	        found = true;
	      
          if (!found) { 
            v.add(pname);
          }
        }
        otherProperties.setProperties((String[])v.toArray(new String[v.size()]));
        if (v.size()>0) {
          if (propertygroups==null) 
            propertygroups = new PropertyGroupBean[1];
          else {
            PropertyGroupBean newpropertygroups [] = new PropertyGroupBean[propertygroups.length+1];
            for (int i=0; i<propertygroups.length; i++)
              newpropertygroups[i] = propertygroups[i];
            propertygroups = newpropertygroups;
          }
          propertygroups[propertygroups.length-1] = otherProperties;
          _propertyGroups.put(classname, propertygroups);
          request.setAttribute("propertygroups", propertygroups);
        }
      }//sync
    }
  }
%>
