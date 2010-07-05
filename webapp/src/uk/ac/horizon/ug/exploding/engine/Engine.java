package uk.ac.horizon.ug.exploding.engine;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import uk.ac.horizon.ug.exploding.db.Game;
import uk.ac.horizon.ug.exploding.db.Member;
import uk.ac.horizon.ug.exploding.db.Position;
import uk.ac.horizon.ug.exploding.db.TimelineEvent;
import uk.ac.horizon.ug.exploding.db.Zone;

import equip2.core.DataspaceObjectEvent;
import equip2.core.DataspaceObjectsEvent;
import equip2.core.IDataspace;
import equip2.core.IDataspaceObjectsListener;
import equip2.core.ISession;
import equip2.core.QueryTemplate;

public class Engine
{
	static Logger logger = Logger.getLogger(Engine.class.getName());

	protected Map<Integer, ZoneCache> zoneCache = new HashMap<Integer, ZoneCache>();
	
	// FIXME - persist this, sysvars
	protected long lastEventCheckTime;
	
	protected IDataspace dataspace;

	public void setDataspace(IDataspace dataspace)
	{
		this.dataspace = dataspace;
	}

	public IDataspace getDataspace()
	{
		return this.dataspace;
	}
	
	// milliseconds to gametime ratio
	protected float timeRatio;
	
	public void setTimeRatio(float timeRatio)
	{
		this.timeRatio = timeRatio;
	}

	public float getTimeRatio()
	{
		return this.timeRatio;
	}
	
	// new member radius
	protected float spawnRadius;
	
	public void setSpawnRadius(float spawnRadius)
	{
		this.spawnRadius = spawnRadius;
	}

	public float getSpawnRadius()
	{
		return this.spawnRadius;
	}
	
	public Engine()
	{
		
	}
	
	public void startup()
	{
		logger.info("engine startup");
		
		lastEventCheckTime = System.currentTimeMillis();
		
		// cache zones
		ISession session = dataspace.getSession();
		session.begin(ISession.READ_ONLY);
		
		Zone zone = new Zone();
		
		Object [] zones = session.match(zone);
		
		session.end();
		
		for(int i=0;i<zones.length;i++)
		{
			cacheZone((Zone)zones[i]);
		}			

		session = dataspace.getSession();		
		session.getEventManagement().addIDataspaceObjectsListener(new ZoneListener(), zone);

		// member events
		Member member = new Member();
		session = dataspace.getSession();
		session.getEventManagement().addIDataspaceObjectsListener(new MemberListener(), member);
	}
	
	class ZoneListener implements IDataspaceObjectsListener
	{
		ZoneListener(){}
		
		public void objectsChanged(DataspaceObjectsEvent dose)
		{
			logger.info("Engine::ZoneListener triggered");
			
			try
			{
				Enumeration changes = dose.getDataspaceObjectEvents();
				
				while(changes.hasMoreElements())
				{
					DataspaceObjectEvent doe = (DataspaceObjectEvent) changes.nextElement();
					
					if(doe.getRealChange() == DataspaceObjectEvent.OBJECT_ADDED
							|| doe.getRealChange() == DataspaceObjectEvent.OBJECT_MODIFIED)
					{
						if(doe.getNewValue()!=null)
						{
							cacheZone((Zone)doe.getNewValue());
						}
					}
				}
			}
			catch (Exception e)
			{
				logger.error("Engine::ZoneListener error: " +e.toString(), new Throwable());
			}
		}			
	}
	
	public void cacheZone(Zone zone)
	{
		logger.info("Engine::cacheZone triggered");
		ZoneCache zc = new ZoneCache(zone);
		zoneCache.put(zone.getOrgId(), zc);
	}
	
	public Integer getZoneID(Position p)
	{		
		Iterator<Entry<Integer, ZoneCache>> it = zoneCache.entrySet().iterator();
		
		while(it.hasNext())
		{
			Map.Entry<Integer, ZoneCache> pairs = (Map.Entry<Integer, ZoneCache>)it.next();
			if(pairs.getValue().contains(p.getLatitude(), p.getLongitude()))
			{
				return pairs.getKey();
			}
	    }

		return null;
	}
	
	class MemberListener implements IDataspaceObjectsListener
	{
		MemberListener(){}
		
		public void objectsChanged(DataspaceObjectsEvent dose)
		{
			logger.info("Engine::MemberListener triggered");
			
			try
			{
				Enumeration changes = dose.getDataspaceObjectEvents();
				
				while(changes.hasMoreElements())
				{
					DataspaceObjectEvent doe = (DataspaceObjectEvent) changes.nextElement();
					
					if(doe.getRealChange() == DataspaceObjectEvent.OBJECT_ADDED)
					{
						if(doe.getNewValue()!=null)
						{
							cacheZone((Zone)doe.getNewValue());
						}
					}
					else if(doe.getRealChange() == DataspaceObjectEvent.OBJECT_MODIFIED)
					{
						if(doe.getNewValue()!=null)
						{
							cacheZone((Zone)doe.getNewValue());
						}
					}
				}
			}
			catch (Exception e)
			{
				logger.error("Engine::MemberListener error: " +e.toString(), new Throwable());
			}
		}			
	}	

	public void checkEvents()
	{		
		// for each active game, check for events since last tick
		
		ISession session = dataspace.getSession();
		session.begin(ISession.READ_WRITE);
		
		QueryTemplate gqt = new QueryTemplate(Game.class);
		gqt.addConstraintEq("active", true);
		
		Object [] gs = session.match(gqt);
		
		long now = System.currentTimeMillis();
		
		for(int i=0; i<gs.length; i++)
		{
			Game g = (Game) gs[i];
			
			// FIXME members entering active duration events
			
			if(g.getContentGroupID()!=null && g.getContentGroupID().length()>0)
			{
				// real time elapsed
				long elapsed = now - this.lastEventCheckTime;
				float currentGameTime = (elapsed * this.timeRatio) + g.getGameTime();

			   	QueryTemplate eq = new QueryTemplate(TimelineEvent.class);
		     	eq.addConstraintGt("startTime", g.getGameTime());
		     	eq.addConstraintLe("startTime", currentGameTime);
		    	eq.addOrder("startTime");
		    	
		    	Object [] es = session.match(eq);
		    	
		    	for(int j=0; j<es.length; j++)
		    	{
		    		TimelineEvent e = (TimelineEvent) es[j];
		    		handleEvent(session, g, e);
		    	}
		    	
		    	logger.info(g.getGameTime() + " " + currentGameTime);
		    	
		    	g.setGameTime(currentGameTime);
		    	session.update(g);	
			}
		}

    	this.lastEventCheckTime = now;

		session.end();
	}
	
	
	public void placeMember(ISession session, Game game, Member member)
	{
		
	}

	
	public void handleEvent(ISession session, Game game, TimelineEvent event)
	{
		// FIXME tell affected players about the event?!
		
	   	QueryTemplate mq = new QueryTemplate(Member.class);
	   	mq.addConstraintEq("gameID", game.getID());
	   	
	   	if(event.getZoneId()!=0)
	   	{
	   		mq.addConstraintEq("zone", event.getZoneId());
	   	}
	   	
	   	Object [] ms = session.match(mq);
	   	
	   	for(int i=0; i<ms.length; i++)
	   	{
	   		Member member = (Member) ms[i];
	   		
	   		if(event.getAbsolute()==1)
	   		{
	   			// FIXME absolute events?!   			
	   		}
	   		else
	   		{
	   			if(event.getHealth()!=0)
	   			{
		   			int health = member.getHealth();
		   			health += event.getHealth();
		   			if(health>10)
		   			{
		   				health = 10;
		   			}
		   			else if(health<=0)
		   			{
			   			// initial member
			   			if(member.getParentMemberID()==null || member.getParentMemberID().length()==0)
			   			{
			   				health = 1;
			   			}
			   			else
			   			{
			   				// FIXME kill this member
			   				session.remove(member);
			   				continue;
			   			}
		   			}		   			
	   			
		   			member.setHealth(health);		   			
	   			}
	   			
	   			if(event.getWealth()!=0)
	   			{
		   			int wealth = member.getWealth();
		   			wealth += event.getWealth();
		   			if(wealth>10)
		   			{
		   				wealth = 10;
		   			}
		   			else if(wealth<0)
		   			{
		   				wealth = 0;
		   			}
		   			member.setWealth(wealth);		   			
	   			}
	   			
	   			if(event.getAction()!=0)
	   			{
		   			int action = member.getAction();
		   			action += event.getAction();
		   			if(action>10)
		   			{
		   				action = 10;
		   			}
		   			else if(action<0)
		   			{
		   				action = 0;
		   			}
		   			member.setAction(action);		   			
	   			}
	   			
	   			if(event.getBrains()!=0)
	   			{
		   			int brains = member.getBrains();
		   			brains += event.getBrains();
		   			if(brains>10)
		   			{
		   				brains = 10;
		   			}
		   			else if(brains<0)
		   			{
		   				brains = 0;
		   			}
		   			member.setBrains(brains);		   			
	   			}
	   			
	   			session.update(member);
	   		}
	   	}
	   	
	   	// consequences
		
		logger.info(event.getStartTime());
	}
}
