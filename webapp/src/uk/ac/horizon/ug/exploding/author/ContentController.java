
package uk.ac.horizon.ug.exploding.author;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.validation.BindException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import equip2.core.IDataspace;
import equip2.core.ISession;
import equip2.core.QueryTemplate;
import equip2.spring.db.IDAllocator;


public class ContentController extends SimpleFormController
{		
	static Logger logger = Logger.getLogger(ContentController.class.getName());

	protected IDataspace dataspace;

	public void setDataspace(IDataspace dataspace)
	{
		this.dataspace = dataspace;
	}

	public IDataspace getDataspace()
	{
		return this.dataspace;
	}
	
	protected XStream xstream;
	
	public ContentController()
	{
		xstream = new XStream(new DomDriver());
		
		xstream.alias("healthOp", uk.ac.horizon.ug.exploding.author.model.HealthOp.class);
		xstream.alias("wealthOp", uk.ac.horizon.ug.exploding.author.model.WealthOp.class);
		xstream.alias("actionOp", uk.ac.horizon.ug.exploding.author.model.ActionOp.class);
		xstream.alias("brainsOp", uk.ac.horizon.ug.exploding.author.model.BrainsOp.class);
		
		xstream.alias("gameState", uk.ac.horizon.ug.exploding.author.model.GameState.class);
		xstream.aliasAttribute(uk.ac.horizon.ug.exploding.author.model.GameState.class, "version", "version");
		
		xstream.alias("timeEvent", uk.ac.horizon.ug.exploding.author.model.TimeEvent.class);
		xstream.aliasAttribute(uk.ac.horizon.ug.exploding.author.model.TimeEvent.class, "ref", "ref");

		xstream.alias("zone", uk.ac.horizon.ug.exploding.author.model.Zone.class);
		xstream.aliasAttribute(uk.ac.horizon.ug.exploding.author.model.Zone.class, "ref", "ref");

		xstream.alias("indexList", uk.ac.horizon.ug.exploding.author.model.IndexList.class);
		xstream.aliasAttribute(uk.ac.horizon.ug.exploding.author.model.IndexList.class, "ref", "ref");

		xstream.alias("attributeSet", uk.ac.horizon.ug.exploding.author.model.AttributeSet.class);
		xstream.aliasAttribute(uk.ac.horizon.ug.exploding.author.model.AttributeSet.class, "ref", "ref");
		xstream.addImplicitCollection(uk.ac.horizon.ug.exploding.author.model.AttributeSet.class,"fields", "field", uk.ac.horizon.ug.exploding.author.model.Field.class);

		xstream.addImplicitCollection(uk.ac.horizon.ug.exploding.author.model.Zone.class,"fields", "field", uk.ac.horizon.ug.exploding.author.model.Field.class);
		xstream.registerConverter(new uk.ac.horizon.ug.exploding.author.model.FieldConverter());
	}
	
	
    public ModelAndView content(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
    	ModelAndView mav = new ModelAndView();
		
		Map model = mav.getModel();
		
		if (mav.getViewName() != null)
		{
			return mav;
		}
    	
		ISession session = dataspace.getSession();
		session.begin(ISession.READ_ONLY);
		
		QueryTemplate cqt = new QueryTemplate(uk.ac.horizon.ug.exploding.db.ContentGroup.class);
		
		Object [] cgs = session.match(cqt);
		
		Vector<uk.ac.horizon.ug.exploding.db.ContentGroup> contentGroups = new Vector<uk.ac.horizon.ug.exploding.db.ContentGroup>();
		
		for(int i=0; i<cgs.length; i++)
		{
			uk.ac.horizon.ug.exploding.db.ContentGroup dbc = (uk.ac.horizon.ug.exploding.db.ContentGroup) cgs[i];
			contentGroups.add(dbc);
		}

		session.end();
		
		model.put("contentGroups", contentGroups);

		mav.setViewName("/author/content");
    	return mav;
    }
	
    
    public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
    	ModelAndView mav = new ModelAndView();
    	HttpSession httpSession = request.getSession();
    	String contentGroupID = request.getParameter("contentGroupID");

    	if(contentGroupID!=null && contentGroupID.length()>0)
    	{
    		logger.info("deleting " + contentGroupID);
    		
    		ISession session = dataspace.getSession();
    		session.begin(ISession.READ_WRITE);

    		uk.ac.horizon.ug.exploding.db.ContentGroup cg =
    			(uk.ac.horizon.ug.exploding.db.ContentGroup) session.get(uk.ac.horizon.ug.exploding.db.ContentGroup.class, contentGroupID);
    		
    		if(cg!=null)
    		{
    			session.remove(cg);
    		}
    		
    		QueryTemplate zqt = new QueryTemplate(uk.ac.horizon.ug.exploding.db.Zone.class);
    		zqt.addConstraintEq("contentGroupID", contentGroupID);
    		
    		Object [] zs = session.match(zqt);

    		for(int i=0; i<zs.length; i++)
    		{
    			uk.ac.horizon.ug.exploding.db.Zone dbz = (uk.ac.horizon.ug.exploding.db.Zone) zs[i];
    			session.remove(dbz);
    		}
    		
    		QueryTemplate tqt = new QueryTemplate(uk.ac.horizon.ug.exploding.db.TimelineEvent.class);
    		tqt.addConstraintEq("contentGroupID", contentGroupID);
    		
    		Object [] ts = session.match(tqt);

    		for(int i=0; i<ts.length; i++)
    		{
    			uk.ac.horizon.ug.exploding.db.TimelineEvent dbt = (uk.ac.horizon.ug.exploding.db.TimelineEvent) ts[i];
    			session.remove(dbt);
    		}

    		session.end();
    	}
    	
    	mav.setViewName("redirect:/author/content.html");
    	return mav;
    }
    
    
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception
	{
		FileUploadBean bean = (FileUploadBean) command;
		byte[] file = bean.getFile();
		
		if(file!=null && bean.getFilename()!=null && bean.getFilename().length()!=0)
		{
			logger.info("on submit");

			ISession session = dataspace.getSession();
			session.begin(ISession.READ_WRITE);

			uk.ac.horizon.ug.exploding.author.model.GameState gameState = null;
			
			try
			{
				ByteArrayInputStream in = new ByteArrayInputStream(file);

				gameState = (uk.ac.horizon.ug.exploding.author.model.GameState) xstream.fromXML(in);
				
			}
			catch(Exception e)
			{
				logger.error(e);
			}
			
			if(gameState!=null)
			{
				uk.ac.horizon.ug.exploding.db.ContentGroup cg = new uk.ac.horizon.ug.exploding.db.ContentGroup();
				
				cg.setID(IDAllocator.getNewID(session, uk.ac.horizon.ug.exploding.db.ContentGroup.class, "CG", null));
				cg.setName(bean.getFilename());
				cg.setLocation(gameState.location);
				cg.setVersion(gameState.version);
				cg.setStartYear(gameState.startYear);
				cg.setEndYear(gameState.endYear);
		
				session.add(cg);

				for(uk.ac.horizon.ug.exploding.author.model.Zone z : gameState.zones)
				{
					uk.ac.horizon.ug.exploding.db.Zone zone = new uk.ac.horizon.ug.exploding.db.Zone();
					zone.setID(IDAllocator.getNewID(session, uk.ac.horizon.ug.exploding.db.Zone.class, "ZO", null));
					zone.setContentGroupID(cg.getID());
					
					zone.setOrgId(z.id);
					zone.setName(z.name);
					zone.setPolygon(z.polygon);
					zone.setRadius(z.radius);
					zone.setRef(z.ref);
					
					// this is a mess, and why artists should not design data models
					
					if(z.polygon != null && z.polygon == 1)
					{
						Vector<uk.ac.horizon.ug.exploding.db.Position> ps
							= new Vector<uk.ac.horizon.ug.exploding.db.Position>();
						
						for(uk.ac.horizon.ug.exploding.author.model.Field f : z.fields)
						{						
							if("attributes".equals(f.name))
							{
								// ignore zone attributes for now?!
							}
							else if("coords".equals(f.name))
							{
								for(uk.ac.horizon.ug.exploding.author.model.Coordinate c : f.coordinates)
								{
									uk.ac.horizon.ug.exploding.db.Position p = new uk.ac.horizon.ug.exploding.db.Position();
									p.setElevation(c.elevation);
									p.setLatitude(c.latitude);
									p.setLongitude(c.longitude);
									ps.add(p);
								}
							}
						}

						zone.setCoordinates(ps.toArray(new uk.ac.horizon.ug.exploding.db.Position[ps.size()]));
					}
					
					session.add(zone);
				}
				
				for(uk.ac.horizon.ug.exploding.author.model.TimeEvent te : gameState.timeEvents)
				{
					uk.ac.horizon.ug.exploding.db.TimelineEvent t = new uk.ac.horizon.ug.exploding.db.TimelineEvent();
					t.setID(IDAllocator.getNewID(session, uk.ac.horizon.ug.exploding.db.TimelineEvent.class, "TE", null));
					t.setContentGroupID(cg.getID());
					t.setOrgId(te.id);
					t.setName(te.name);
					t.setAbsolute(te.absolute);
					t.setDescription(te.description);
					t.setEnabled(te.enabled);
					t.setEndTime((float)te.endTime);
					t.setStartTime((float)te.startTime);
					t.setRgb(te.rgb);
					t.setTrack(te.track);
					t.setZoneId(te.zone);
					
					if(te.indexList != null && te.indexList.attributeSet != null)
					{
						t.setAction(te.indexList.attributeSet.action);
						t.setBrains(te.indexList.attributeSet.brains);
						t.setFlags(te.indexList.attributeSet.flags);
						t.setHealth(te.indexList.attributeSet.health);
						t.setWealth(te.indexList.attributeSet.wealth);
						t.setInstant(te.indexList.attributeSet.instant);
						
						if(te.indexList.attributeSet.healthOp != null)
						{
							logger.info("healthOp" + t.getID());
							t.setHealthMin(te.indexList.attributeSet.healthOp.minimum);
							t.setHealthMax(te.indexList.attributeSet.healthOp.maximum);
						}
						
						if(te.indexList.attributeSet.wealthOp != null)
						{
							logger.info("wealthOp");
							t.setWealthMin(te.indexList.attributeSet.wealthOp.minimum);
							t.setWealthMax(te.indexList.attributeSet.wealthOp.maximum);
						}
						
						if(te.indexList.attributeSet.actionOp != null)
						{
							logger.info("actionOp");
							t.setActionMin(te.indexList.attributeSet.actionOp.minimum);
							t.setActionMax(te.indexList.attributeSet.actionOp.maximum);
						}
						
						if(te.indexList.attributeSet.brainsOp != null)
						{
							logger.info("brainsOp");
							t.setBrainsMin(te.indexList.attributeSet.brainsOp.minimum);
							t.setBrainsMax(te.indexList.attributeSet.brainsOp.maximum);
						}						
					}
					
					session.add(t);
				}
			}
		
			session.end();
		}      

		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/author/content.html");
		return mav;
	}
	

    protected void onBind(HttpServletRequest request, Object command, BindException errors)
	{
    	FileUploadBean bean = (FileUploadBean) command;
    	MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
    	MultipartFile multipart = multiRequest.getFile("file");
    	bean.setFilename(multipart.getOriginalFilename());
	}

    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws ServletException 
	{
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
	}    
}

