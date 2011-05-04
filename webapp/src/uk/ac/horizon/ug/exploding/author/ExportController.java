package uk.ac.horizon.ug.exploding.author;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import uk.ac.horizon.ug.exploding.db.ContentGroup;
import uk.ac.horizon.ug.exploding.db.GameConfig;
import uk.ac.horizon.ug.exploding.db.TimelineEvent;
import uk.ac.horizon.ug.exploding.db.Zone;
import uk.ac.horizon.ug.exploding.spectator.Constants;
import uk.ac.horizon.ug.exploding.spectator.EquipObjectView;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import equip2.core.IDataspace;
import equip2.core.ISession;
import equip2.core.QueryTemplate;
import equip2.spring.db.IDAllocator;

public class ExportController extends SimpleFormController {
	static Logger logger = Logger.getLogger(ExportController.class.getName());

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

	public ExportController() {
		xstream = new XStream(new XppDriver()) {
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new FieldPrefixStrippingMapper(next);
            }	
		};

		xstream.alias("GameExport", uk.ac.horizon.ug.exploding.author.GameExport.class);
		xstream.alias("Zone", uk.ac.horizon.ug.exploding.db.Zone.class);
		xstream.alias("Position", uk.ac.horizon.ug.exploding.db.Position.class);
		xstream.alias("Event", uk.ac.horizon.ug.exploding.db.TimelineEvent.class);
		xstream.alias("Config", uk.ac.horizon.ug.exploding.db.GameConfig.class);
		xstream.alias("Content", uk.ac.horizon.ug.exploding.db.ContentGroup.class);
		//xstream.aliasField("Content", uk.ac.horizon.ug.exploding.author.GameExport.class, "content");
		//xstream.aliasField("Config", uk.ac.horizon.ug.exploding.author.GameExport.class, "config");
	}
	
    /**
     * A sample mapper strips the underscore prefix of field names in the XML
     */
    private static class FieldPrefixStrippingMapper extends MapperWrapper {
        public FieldPrefixStrippingMapper(Mapper wrapped) {
            super(wrapped);
        }

        public String serializedMember(Class type, String memberName) {
            if (memberName.startsWith("_")) {
                // _blah -> blah
                memberName = memberName.substring(1); // chop off leading char (the underscore)
            }
            return super.serializedMember(type, memberName);
        }

        public String realMember(Class type, String serialized) {
            String fieldName = super.realMember(type, serialized);
            // Not very efficient or elegant, but enough to get the point across.
            // Luckily the CachingMapper will ensure this is only ever called once per field per class.
            try {
                type.getDeclaredField("_" + fieldName);
                return "_" + fieldName;
            } catch (NoSuchFieldException e) {
            	return fieldName;
            }
        }
    }


	public ModelAndView export_form(HttpServletRequest request, HttpServletResponse response) throws ServletException
	{
		ModelAndView mav = new ModelAndView();

		Map model = mav.getModel();
		
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
		
		model.put("contentGroups", contentGroups);
		
		QueryTemplate gcqt = new QueryTemplate(uk.ac.horizon.ug.exploding.db.GameConfig.class);
		
		Object [] gcs = session.match(gcqt);
		
		Vector<uk.ac.horizon.ug.exploding.db.GameConfig> gameConfigs = new Vector<uk.ac.horizon.ug.exploding.db.GameConfig>();
		
		for(int i=0; i<gcs.length; i++)
		{
			uk.ac.horizon.ug.exploding.db.GameConfig dbgc = (uk.ac.horizon.ug.exploding.db.GameConfig) gcs[i];
			gameConfigs.add(dbgc);
		}
		
		model.put("gameConfigs", gameConfigs);

		session.end();
		
		mav.setViewName("/author/export_form");
    	return mav;
	}
		
	public ModelAndView export(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		ModelAndView mav = new ModelAndView();

		Map model = mav.getModel();
		
		String contentGroupId = request.getParameter("contentGroupId");
		if (contentGroupId == null)
			return null;

		GameExport export = new GameExport();
		
		ISession session = dataspace.getSession();
		session.begin(ISession.READ_ONLY);

		export.setContent((ContentGroup) session.get(ContentGroup.class, contentGroupId));
		{
			QueryTemplate zqt = new QueryTemplate(uk.ac.horizon.ug.exploding.db.Zone.class);
			zqt.addConstraintEq("contentGroupID", contentGroupId);
			Object zones[] = session.match(zqt);
			export.setZones(new Zone[zones.length]);
			for (int zi=0; zi<zones.length; zi++) 
				export.getZones()[zi] = (Zone)zones[zi];
		}
		{
			QueryTemplate eqt = new QueryTemplate(uk.ac.horizon.ug.exploding.db.TimelineEvent.class);
			eqt.addConstraintEq("contentGroupID", contentGroupId);
			Object events[] = session.match(eqt);
			export.setEvents(new TimelineEvent[events.length]);
			for (int zi=0; zi<events.length; zi++) 
				export.getEvents()[zi] = (TimelineEvent)events[zi];
		}
		
		String gameConfigId = request.getParameter("gameConfigId");
		if (gameConfigId != null)
			export.setConfig((GameConfig)session.get(GameConfig.class, gameConfigId));

		session.end();

		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");		
		Writer w = new BufferedWriter(response.getWriter());
		w.append("<?xml version=\"1.0\"?>\n");
		xstream.toXML(export, w);
		w.close();
		
		return null;
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

	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception
	{
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/author/import");
		
		FileUploadBean bean = (FileUploadBean) command;
		byte[] file = bean.getFile();

		GameExport export = null;
		try
		{
			ByteArrayInputStream in = new ByteArrayInputStream(file);
			export = (uk.ac.horizon.ug.exploding.author.GameExport) xstream.fromXML(in);

			logger.debug("import "+export+"...");
		}
		catch(Exception e)
		{
			logger.error(e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error in submitted XML: "+e);
			return null;
		}
		
		ISession session = dataspace.getSession();
		session.begin(ISession.READ_WRITE);
		// TODO ...
		if (export.getConfig()!=null) {
			GameConfig config = export.getConfig();
			config.setID(IDAllocator.getNewID(session, GameConfig.class, "GC", null));
			session.add(config);

			mav.getModel().put("gameConfig", config);
		}
		if (export.getContent()!=null) {
			ContentGroup content = export.getContent();
			content.setID(IDAllocator.getNewID(session, GameConfig.class, "CG", null));
			session.add(content);
			
			Zone zones[] = export.getZones();
			for (int i=0; zones!=null && i<zones.length; i++) {
				zones[i].setContentGroupID(content.getID());
				zones[i].setID(IDAllocator.getNewID(session, Zone.class, "ZO", null));
				session.add(zones[i]);
			}
			
			TimelineEvent events[] = export.getEvents();
			for (int i=0; events!=null && i<events.length; i++) {
				events[i].setContentGroupID(content.getID());
				events[i].setID(IDAllocator.getNewID(session, TimelineEvent.class, "TE", null));
				session.add(events[i]);
			}
			mav.getModel().put("contentGroup", content);
		}
		
		session.end();
		
		return mav;
	}

}
