
package uk.ac.horizon.ug.exploding.orchestration;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
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

import uk.ac.horizon.ug.exploding.db.Game;
import uk.ac.horizon.ug.exploding.db.Member;
import uk.ac.horizon.ug.exploding.db.Player;

import equip2.core.IDataspace;
import equip2.core.ISession;
import equip2.core.QueryTemplate;
import equip2.core.marshall.impl.DefaultContext;
import equip2.core.marshall.impl.DefaultUnmarshallXmlRegistry;
import equip2.core.objectsupport.IObjectHelper;
import equip2.core.objectsupport.IObjectHelperRegistry;
import equip2.naming.InitialContext;
import equip2.spring.db.IDAllocator;


public class UploadController extends SimpleFormController
{		
	static Logger logger = Logger.getLogger(UploadController.class.getName());

	protected IDataspace dataspace;

	public void setDataspace(IDataspace dataspace)
	{
		this.dataspace = dataspace;
	}

	public IDataspace getDataspace()
	{
		return this.dataspace;
	}
	
    public ModelAndView upload(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
    	ModelAndView mav = new ModelAndView();
		
		Map model = mav.getModel();
		
		if (mav.getViewName() != null)
		{
			return mav;
		}
    	
		ISession session = dataspace.getSession();
		session.begin(ISession.READ_ONLY);
		
		QueryTemplate gqt = new QueryTemplate(uk.ac.horizon.ug.exploding.db.Game.class);
		gqt.addOrder("timeCreated");
		
		Object [] gs = session.match(gqt);
		
		Vector<uk.ac.horizon.ug.exploding.db.Game> games = new Vector<uk.ac.horizon.ug.exploding.db.Game>();
		
		for(int i=0; i<gs.length; i++)
		{
			uk.ac.horizon.ug.exploding.db.Game dbg = (uk.ac.horizon.ug.exploding.db.Game) gs[i];
			games.add(dbg);
		}
		
		model.put("games", games);

		session.end();
		
		mav.setViewName("/orchestration/upload");
    	return mav;
    }
    
    // stolen from dbuploadcontroller
    protected static String readXmlBlock(Reader isr) throws IOException, EOFException 
    {
	StringBuffer buf = new StringBuffer();
	boolean inBlock = false;
	while(true) 
	{
	    int c = isr.read();
	    if (c<0)
		throw new EOFException("Reading xml block");
	    if (!inBlock && Character.isWhitespace((char)c))
		continue;
	    buf.append((char)c);
	    if (c=='<') 
	    {
		if (inBlock)
		    throw new IOException("Nested < in block "+buf+"..."); 
		inBlock = true;
	    }
	    if (c=='>')
		return buf.toString();
	}
	// not reached
    }
    
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception
	{
		FileUploadBean bean = (FileUploadBean) command;
		byte[] file = bean.getFile();
		
    	String gameID = request.getParameter("gameID");
		
		if(file!=null && bean.getFilename()!=null && bean.getFilename().length()!=0)
		{
			ISession session = dataspace.getSession();
			session.begin(ISession.READ_WRITE);

			logger.info("bulkupload onsubmit");
			
			Vector<Player> players = new Vector<Player>();
			Vector<Member> members = new Vector<Member>();
			
		    try 
		    {
			    InitialContext ctx = new InitialContext();
			    IObjectHelperRegistry registry = (IObjectHelperRegistry)ctx.lookup(IObjectHelperRegistry.JNDI_DEFAULT_NAME);
			    
				logger.debug("Starting binary upload");
				Reader isr = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(file), "UTF-8"));
				// xml file header
				//System.out.println("Read header");
				String header = readXmlBlock(isr);
				if (!header.startsWith("<?xml"))
				    throw new IOException("File "+file+" does not start with XML header");
				System.out.println("Header: "+header);
				System.out.println("Read <objects>");
				String objects = readXmlBlock(isr);
				if (!objects.startsWith("<objects"))
				    throw new IOException("File "+file+" does not have <objects> element");
				int dpix = objects.indexOf("defaultPackage=");

				DefaultUnmarshallXmlRegistry unmarshallRegistry = new DefaultUnmarshallXmlRegistry();
				DefaultContext unmarshallContext = new DefaultContext(registry, unmarshallRegistry);

				while(true) 
				{
				    //System.out.println("Read object");
				    isr.mark(1000);
				    String next = readXmlBlock(isr);
				    if (next.startsWith("</objects"))
					break; // done
				    if (next.startsWith("<!--"))
					continue; // skip comment
				    // read object
				    isr.reset();
				
				    Object object = unmarshallContext.unmarshall(isr);

				    if(object instanceof Member)
				    {
				    	members.add((Member)object);
				    }
				    else if(object instanceof Player)
				    {
				    	players.add((Player)object);
				    }
				}
		    }
		    catch (Exception e) 
		    {
		    	logger.error("error uploading", e);
		    }
		    
		    if(gameID!=null && gameID.length()>0)
		    {
		    	Game game = (Game) session.get(Game.class, gameID);
		    	
		    	Map<String, String> memberIDs = new HashMap<String, String>();
		    	Map<String, String> playerIDs = new HashMap<String, String>();
		    	
		    	if(game!=null)
		    	{
		    		// rewrite member and player ids
		    		for(Player p: players)
		    		{
		    			String newID = IDAllocator.getNewID(session, Player.class, "P", null);
		    			playerIDs.put(p.getID(), newID);
		    			p.setID(newID);
		    			p.setGameID(game.getID());
		    			
		    			session.add(p);
		    		}
		    		
		    		for(Member m: members)
		    		{
		    			String playerID = playerIDs.get(m.getPlayerID());
		    			m.setPlayerID(playerID);
		    			
		    			if(m.isSetParentMemberID())
		    			{
		    				String newParentMemberID = memberIDs.get(m.getParentMemberID());
		    				if(newParentMemberID==null)
		    				{
		    					newParentMemberID = IDAllocator.getNewID(session, Member.class, "M", null);
		    					memberIDs.put(m.getParentMemberID(), newParentMemberID);
		    				}
		    				m.setParentMemberID(newParentMemberID);
		    			}
		    			
	    				String newID = memberIDs.get(m.getID());
	    				if(newID==null)
	    				{
	    					newID = IDAllocator.getNewID(session, Member.class, "M", null);
	    					memberIDs.put(m.getID(), newID);
	    				}
	    				m.setID(newID);
	    				m.setGameID(game.getID());
	    				
	    				session.add(m);		    			
		    		}
		    	}
		    }
		
			session.end();
		}      

		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/orchestration/upload.html");
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

