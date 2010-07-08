package uk.ac.horizon.ug.exploding.engine;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import uk.ac.horizon.ug.exploding.db.Game;
import uk.ac.horizon.ug.exploding.db.Member;
import uk.ac.horizon.ug.exploding.db.Player;
import uk.ac.horizon.ug.exploding.db.Position;
import uk.ac.horizon.ug.exploding.db.TimelineEvent;
import uk.ac.horizon.ug.exploding.db.Zone;

import equip2.core.DataspaceObjectEvent;
import equip2.core.DataspaceObjectsEvent;
import equip2.core.IDataspace;
import equip2.core.IDataspaceObjectsListener;
import equip2.core.ISession;
import equip2.core.QueryTemplate;
import equip2.spring.db.IDAllocator;

public class Engine
{
	static Logger logger = Logger.getLogger(Engine.class.getName());

	// FIXME - zones are game / content group specific
	protected Map<Integer, ZoneCache> zoneCache = new HashMap<Integer, ZoneCache>();
	
	protected Random random = new Random();
	
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
	protected double spawnRadius;
	
	public void setSpawnRadius(double spawnRadius)
	{
		this.spawnRadius = spawnRadius;
	}

	public double getSpawnRadius()
	{
		return this.spawnRadius;
	}
	
	// proximity radius
	protected double proximityRadius;
	
	public void setProximityRadius(double proximityRadius)
	{
		this.proximityRadius = proximityRadius;
	}

	public double getProximityRadius()
	{
		return this.proximityRadius;
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
							// placed or carried?
							Member member = (Member) doe.getNewValue();
							
							if(member.getCarried()==false)
							{
								// check zones, consequences
								memberPlaced(member);
							}
						}
					}
					else if(doe.getRealChange() == DataspaceObjectEvent.OBJECT_MODIFIED)
					{
						if(doe.getNewValue()!=null && doe.getOldValue()!=null)
						{
							// carried to placed?
							Member member = (Member) doe.getNewValue();
							Member oldMember = (Member) doe.getOldValue();
							
							if(oldMember.getCarried()==true && member.getCarried()==false)
							{
								// check zones, consequences
								memberPlaced(member);
							}
							
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
	
	
	public void memberPlaced(Member member)
	{
		ISession session = dataspace.getSession();
		session.begin(ISession.READ_WRITE);

		// FIXME members entering zones with active duration events?
		// FIXME there appear to be stat modifiers associated with Zones, this is not in the design doc.

		member.setZone(this.getZoneID(member.getPosition()));
		
	   	QueryTemplate mq = new QueryTemplate(Member.class);
	   	mq.addConstraintEq("gameID", member.getGameID());
	   	mq.addConstraintEq("carried", false);
	   	
	   	if(member.getZone()==0)
	   	{
	   		// they're in a "gap"
	   		//	FIXME - does "assimilation" work across zone boundaries?
	   		session.end();
	   		return;
	   	}

   		mq.addConstraintEq("zone", member.getZone());

   		// FIXME - can't remember, need to check...
   		// - will this match the original member now in this zone within this session?
	   	Object [] ms = session.match(mq);
	   	
	   	if(ms.length==0)
	   	{
	   		// increase wealth
	   		// FIXME - does "community member in a new region" mean they've not been here before?
   			int wealth = member.getWealth();
   			wealth += 3;
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
	   	else if(ms.length>=10)
	   	{
	   		// -2 health, +2 knowledge
	   		// -2 wealth, +2 participation
	   		// +2 participation
	   		// FIXME this presumably *doesn't* mean +4 participation?!
	   		
	   		for(int i=0; i<ms.length; i++)
	   		{
	   			Member other = (Member) ms[i];

	   			int health = other.getHealth();
	   			health += -2;
		   					   			
		   		if(health>10)
		   		{
		   			health = 10;
		   		}
		   		else if(health<=0)
		   		{
			   		// initial member
			   		if(other.getParentMemberID()==null || other.getParentMemberID().length()==0)
			   		{
			   			health = 1;
			   		}
			   		else
			   		{
			   			// FIXME kill this member
			   			session.remove(other);
			   			continue;
			   		}
		   		}
		   			
		   		other.setHealth(health);		   			
	   			
	   			int wealth = other.getWealth();
		   		wealth += -2;
		   		if(wealth>10)
		   		{
		   			wealth = 10;
		   		}
		   		else if(wealth<0)
		   		{
		   			wealth = 0;
		   		}
		   		other.setWealth(wealth);
		   		
	   			int action = other.getAction();
	   			action += 2;
	   			if(action>10)
	   			{
	   				action = 10;
	   			}
	   			else if(action<0)
	   			{
	   				action = 0;
	   			}
	   			other.setAction(action);
	   			
	   			session.update(other);
	   		}
	   	}
		
		// proximity to others
	   	// FIXME - does "assimilate" mean kill? or steal? Let's assume it means kill members in the same zone, for now
	   	// can we / should we "assimilate" multiple members? just the first one?
	   	
	   	for(int i=0; i<ms.length; i++)
	   	{
	   		Member other = (Member) ms[i];
	   		
	   		if(other.getPlayerID().equals(member.getPlayerID()))
	   		{
	   			continue;
	   		}
	   		
	   		// FIXME - sort by proximity?
	   		if(ZoneCache.distanceBetweenPoints(member.getPosition(), other.getPosition())<proximityRadius)
	   		{
	   			if(member.getAction()>other.getAction())
	   			{
		   			int action = member.getAction();
		   			action += 3;
		   			if(action>10)
		   			{
		   				action = 10;
		   			}
		   			else if(action<0)
		   			{
		   				action = 0;
		   			}
		   			member.setAction(action);	

	   				// FIXME - notify players
	   				session.remove(other);
	   				break;
	   			}
	   			else if(member.getAction()==other.getAction())
	   			{
	   				// do nothing, we are equal
	   				continue;
	   			}
	   			else
	   			{
	   				// they kill us, after all that.
	   				// FIXME - does it make sense to kill the member responsible for the stats changes?

		   			int action = other.getAction();
		   			action += 3;
		   			if(action>10)
		   			{
		   				action = 10;
		   			}
		   			else if(action<0)
		   			{
		   				action = 0;
		   			}
		   			other.setAction(action);
		   			
	   				// FIXME - notify players
	   				session.remove(member);
	   				break;
	   			}
	   		}
	   	}
	   	
	   	// finally, does this mean anyone can author ?
	   	Game game = (Game) session.get(Game.class, member.getGameID());
	   	
	   	if(game!=null)
	   	{
		   	this.updateAuthorable(session, game);	   		
	   	}		
		
		session.end();
	}
	
	// FIXME - I don't understand *Player* participation points yet
	public void updateAuthorable(ISession session, Game game)
	{
	   	// has this changed a player's ability to author events?
	   	QueryTemplate pqt = new QueryTemplate(Player.class);
	   	pqt.addConstraintEq("gameID", game.getID());
	   	
	   	Object [] ps = session.match(pqt);
	   	
	   	for(int i=0; i<ps.length; i++)
	   	{
	   		Player p = (Player) ps[i];
	   		
	   		QueryTemplate mqt = new QueryTemplate(Member.class);
	   		mqt.addConstraintEq("playerID", p.getID());
	   		mqt.addConstraintGe("brains", 8);
	   		
	   		int count = session.count(mqt);
	   		
	   		// FIXME - notify client of change
	   		if(count>0 && p.getCanAuthor()==false)
	   		{
	   			p.setCanAuthor(true);
	   			session.update(p);
	   		}
	   		else if(count==0 && p.getCanAuthor()==true)
	   		{
	   			p.setCanAuthor(false);
	   			session.update(p);
	   		}
	   	}
	}

	
	public void handleEvent(ISession session, Game game, TimelineEvent event)
	{
		// FIXME tell affected players about the event?!
		
	   	QueryTemplate mq = new QueryTemplate(Member.class);
	   	mq.addConstraintEq("gameID", game.getID());
	   	mq.addConstraintEq("carried", false);
	   	
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
	   			// FIXME min/max ranges?!
	   			// - there are none in gameState.xml still
	   			
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
	   			
	   			// create offspring
	   			if(member.getHealth()>=8)
	   			{
	   				if(member.getWealth()>=8)
	   				{
	   					// FIXME reduce wealth too?!
	   					member.setWealth(4);
		   				member.setHealth(4);
		   				
	   					// FIXME trigger creation process?!
	   					continue;
	   				}
	   				else
	   				{	   			
		   				member.setHealth(4);
		   					
		   				Member offspring = new Member();
		   				offspring.setID(IDAllocator.getNewID(session, Member.class, "M", null));
		   				offspring.setCarried(false);
		   				offspring.setGameID(member.getGameID());
		   				offspring.setPlayerID(member.getPlayerID());
		   				offspring.setParentMemberID(member.getID());
		   				offspring.setHealth(4);
		   				
		   				offspring.setWealth(member.getWealth() + ((random.nextInt(2)>0) ? -1 : 1));
		   				if(offspring.getWealth()>10)
			   			{
			   				offspring.setWealth(10);
			   			}
			   			else if(offspring.getWealth()<0)
			   			{
			   				offspring.setWealth(0);
			   			}
		   				
		   				offspring.setBrains(member.getBrains() + ((random.nextInt(2)>0) ? -1 : 1));
		   				if(offspring.getBrains()>10)
			   			{
			   				offspring.setBrains(10);
			   			}
			   			else if(offspring.getBrains()<0)
			   			{
			   				offspring.setBrains(0);
			   			}
	
		   				offspring.setAction(member.getAction() + ((random.nextInt(2)>0) ? -1 : 1));
		   				if(offspring.getAction()>10)
			   			{
			   				offspring.setAction(10);
			   			}
			   			else if(offspring.getAction()<0)
			   			{
			   				offspring.setAction(0);
			   			}

		   				// FIXME - should be metres rather than degrees
		   				double xoffset = (random.nextDouble()*spawnRadius*2)-spawnRadius;
		   				double yoffset = (random.nextDouble()*spawnRadius*2)-spawnRadius;
		   				
		   				Position p = new Position();
		   				p.setElevation(0.0);
		   				p.setLongitude(member.getPosition().getLongitude()+xoffset);
		   				p.setLatitude(member.getPosition().getLatitude()+yoffset);
		   				
		   				offspring.setPosition(p);
		   				offspring.setZone(this.getZoneID(p));
		   				
		   				session.add(offspring);
	   				}
	   			}
	   			
	   			session.update(member);
	   		}
	   	}
	
	   	this.updateAuthorable(session, game);
	}
}
