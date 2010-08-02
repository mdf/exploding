/**
 * 
 */
package uk.ac.horizon.ug.exploding.spectatorreplay;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import equip2.core.DataspaceObjectEvent;
import equip2.core.IDataspace;
import equip2.core.ISession;
import equip2.core.QueryTemplate;
import equip2.core.impl.DefaultDataspace;
import equip2.core.logging.LogEntry;
import equip2.core.logging.LogHeader;
import equip2.core.marshall.impl.DefaultContext;
import equip2.core.marshall.impl.HessianUnmarshallRegistry;
import equip2.core.objectsupport.impl.DefaultObjectHelperRegistry;
import equip2.core.objectsupport.impl.j2se.J2SEObjectHelperRegistry;
import uk.ac.horizon.ug.exploding.clientapi.ClientSubscriptionManager;
import uk.ac.horizon.ug.exploding.db.Game;
import uk.ac.horizon.ug.exploding.db.Member;
import uk.ac.horizon.ug.exploding.db.Message;
import uk.ac.horizon.ug.exploding.db.Player;
import uk.ac.horizon.ug.exploding.spectator.SpectatorController;

/**
 * @author cmg
 *
 */
public class SpectatorReplayController {
	static Logger logger = Logger.getLogger(SpectatorReplayController.class.getName());

	private SpectatorController spectatorController;
	
	public SpectatorController getSpectatorController() {
		return spectatorController;
	}

	public void setSpectatorController(SpectatorController spectatorController) {
		this.spectatorController = spectatorController;
	}

	private File logfile;
	private InputStream inputStream;
	private DefaultContext unmarshallContext;
	private long logTime;
	private String game;
	
	private IDataspace dataspace;
	
    public ModelAndView control(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        ModelAndView mav = new ModelAndView();
        
        populateModel(mav.getModel());
        
		mav.setViewName("/spectatorreplay/control");
        
        return mav;
    }        

    public ModelAndView configure(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        ModelAndView mav = new ModelAndView();

        String g = request.getParameter("game");
        
        if (g!=null && g.length()>0) {
        	logger.info("Set game to "+g);        	
        	game = g;
        }

        String file = request.getParameter("logfile");
        
        if (file!=null && file.length()>0) {
        	logger.info("Set log file to "+file);        	
        	logfile = new File(file);
        	if (logfile.exists() && logfile.canRead() && logfile.isFile()) {
        		initialiseDataspace();
        	}
        	else
        		logger.error("File cannot be read: "+file);
        }
        
        populateModel(mav.getModel());
        
        
		mav.setViewName("/spectatorreplay/control");
        
        return mav;
    }
    
    private LogEntry nextLogEntry;

	private void initialiseDataspace() throws IOException {
		// TODO Auto-generated method stub
		dataspace = DefaultDataspace.newInstance();
		
		if (spectatorController!=null)
			spectatorController.setDataspace(dataspace);
		logger.info("created new dataspace");
		
		inputStream = new BufferedInputStream(new FileInputStream(logfile));
		
	    HessianUnmarshallRegistry unmarshallRegistry = new HessianUnmarshallRegistry();
	    DefaultObjectHelperRegistry registry = new J2SEObjectHelperRegistry();
		unmarshallContext = new DefaultContext(registry, unmarshallRegistry);
		
		LogHeader header = (LogHeader)unmarshallContext.unmarshall(inputStream);
		System.err.println("Start time: "+header.getStartTime()+" ("+new Date(header.getStartTime())+")");
		System.err.println("Metadata: "+header.getMetadata());
		System.err.println("Checkpoint: "+header.getCheckpoint().length+" objects");

		logTime = header.getStartTime();
		
		ISession session = dataspace.getSession();
		session.begin();
		Object checkpoint [] = header.getCheckpoint();
		int count2 = 0;
		for (int ci=0; ci<checkpoint.length; ci++) {
			if (processChange(session, header.getStartTime(), null, checkpoint[ci], DataspaceObjectEvent.LISTENER_ADDED))
				count2++;
		}
		session.end();
		System.err.println("Processed checkpoint "+count2+"/"+checkpoint.length+" handled");
		Object o = unmarshallContext.unmarshall(inputStream);
		if (o instanceof LogEntry) 
			nextLogEntry = (LogEntry)o;
		else
			nextLogEntry = null;
	}

	private boolean processChange(ISession session, long startTime, Object oldValue, Object newValue,
			int change) {
		if (game!=null) {
			Object o = newValue!=null  ? newValue : oldValue;
			if (o==null)
				return false;
		
			if (o instanceof Game) {
				Game g = (Game)o;
				if (!game.equals(g.getID())) {
					logger.info("Discard game event "+oldValue+" -> "+newValue+": game='"+game+"'");
					return false;				
				}
				else
					logger.info("Process game event "+oldValue+" -> "+newValue+": game='"+game+"'");
			} else if (o instanceof Player) {
				Player p = (Player)o;
				if (!game.equals(p.getGameID()))
					return false;				
			} else if (o instanceof Message) {
				Message m = (Message)o;
				Player p = (Player)session.get(Player.class, m.getPlayerID());
				if (p==null) {
					//logger.warn("Unknown player "+m.getPlayerID());
					return false;
				}
				if (!game.equals(p.getGameID()))
					return false;								
			} else if (o instanceof Member) {
				Member m = (Member)o;
				Player p = (Player)session.get(Player.class, m.getPlayerID());
				if (p==null) {
					//logger.warn("Unknown player "+m.getPlayerID());
					return false;
				}
				if (!game.equals(p.getGameID()))
					return false;								
			}
			else
				return false;
		}
		if (oldValue==null && newValue!=null) {
			session.add(newValue);
			return true;
		} else if (oldValue!=null && newValue==null) {
			session.remove(oldValue);			
			return true;
		} else if (oldValue!=null && newValue!=null) {
			session.update(newValue);
			return true;
		}
		return false;
	}

    public ModelAndView advance(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        ModelAndView mav = new ModelAndView();

        String advanceTo = request.getParameter("advanceTo");
        
        if (advanceTo!=null && advanceTo.length()>0) {
        	logger.info("Advance log file to "+advanceTo);
        	long adv = Long.parseLong(advanceTo);
        	if (adv>logTime) 
        		advanceToTime(adv);
        }
        else {
            String advanceBy = request.getParameter("advanceBy");
            if (advanceBy!=null && advanceBy.length()>0) {
            	logger.info("Advance log file by "+advanceBy);
            	long adv = Long.parseLong(advanceBy);
            	if (adv>0)
            		advanceToTime(logTime+adv);
            }
        }
        
        populateModel(mav.getModel());

		mav.setViewName("/spectatorreplay/control");
        
        return mav;
    }
    

	private void populateModel(Map map) {
		// TODO Auto-generated method stub
        map.put("logfile", logfile);
        map.put("status", getStatus());
        map.put("nextEvent", nextLogEntry);
        map.put("game", game);
		if (dataspace!=null) {
			ISession session = dataspace.getSession();
			session.begin();
			map.put("games", session.match(new QueryTemplate(Game.class)));
			map.put("players", session.match(new QueryTemplate(Player.class)));
			session.end();
		}
		else {
			map.put("games", new Object[0]);
			map.put("players", new Object[0]);
		}
	}

	private void advanceToTime(long time) throws IOException {
		// TODO Auto-generated method stub
		int count = 0, count2 = 0;
		ISession session = dataspace.getSession();
		session.begin();

		while (nextLogEntry!=null && nextLogEntry.getTime()<=time) {
			count++;
			
			if (!nextLogEntry.getFailed() && nextLogEntry.isSetTime()) {
				int change = 0;
				if (nextLogEntry.isSetChange()) 
					change = nextLogEntry.getChange();
				else if (nextLogEntry.getOldValue()==null)
					change = DataspaceObjectEvent.OBJECT_ADDED;
				else if (nextLogEntry.getNewValue()==null)
					change = DataspaceObjectEvent.OBJECT_REMOVED;
				else
					change = DataspaceObjectEvent.OBJECT_MODIFIED;
				if (processChange(session, nextLogEntry.getTime(), nextLogEntry.getOldValue(), nextLogEntry.getNewValue(), change))
					count2++;
				else 
					logger.debug("Ignore (game="+game+"): "+nextLogEntry.getOldValue()+" -> "+nextLogEntry.getNewValue());
			}
			else
				System.err.println("Entry: "+nextLogEntry.getTime()+","+nextLogEntry.getChange()+","+nextLogEntry.getFailed()+","+nextLogEntry.getSessionNumber()+","+nextLogEntry.getOldValue()+","+nextLogEntry.getNewValue());

			try {
				Object o = unmarshallContext.unmarshall(inputStream);
				if (o instanceof LogEntry) 
					nextLogEntry = (LogEntry)o;
				else
					nextLogEntry = null;
			}
			catch (Exception e) {
				nextLogEntry = null;
				logger.warn("Error reading log - presume end", e);
			}
		}
		
		session.end();
		logger.info("Advance to time "+time+": processed "+count2+"/"+count+" events");
		if (time>logTime)
			logTime = time;
	}

	private String getStatus() {
		if(logfile==null)
			return "Log file not set";
    	if (!logfile.exists() || !logfile.canRead() || !logfile.isFile()) 
    		return "Log file cannot be read";
		if (dataspace==null)
			return "No dataspace";
		if (nextLogEntry==null)
			return "No next event (done)";
		
		return "Now "+new Date(logTime)+"; Next event in "+(nextLogEntry.getTime()-logTime)+"ms";
	}        

}
