package uk.ac.horizon.ug.exploding.engine;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import uk.ac.horizon.ug.exploding.db.Game;
import uk.ac.horizon.ug.exploding.db.GameConfig;
import uk.ac.horizon.ug.exploding.db.GameTime;
import uk.ac.horizon.ug.exploding.db.Member;
import uk.ac.horizon.ug.exploding.db.Message;
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

	protected Set<ZoneCache> zoneCache = new HashSet<ZoneCache>();
	
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
	/*
	protected double spawnRadius;
	
	public void setSpawnRadius(double spawnRadius)
	{
		this.spawnRadius = spawnRadius;
	}

	public double getSpawnRadius()
	{
		return this.spawnRadius;
	}
	
	protected int maxMembers;
	
	public int getMaxMembers()
	{
		return maxMembers;
	}

	public void setMaxMembers(int maxMembers)
	{
		this.maxMembers = maxMembers;
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
	*/
	
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

		// player movements
		Player player = new Player();
		session.getEventManagement().addIDataspaceObjectsListener(new PlayerListener(), player);
		
		// member events
		Member member = new Member();
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
		zoneCache.add(zc);
	}
	
	public Integer getZoneID(String contentGroupID, Position p)
	{
		if(p==null || contentGroupID==null)
		{
			return null;
		}

		// do 'real' zones first
		Iterator<ZoneCache> it = zoneCache.iterator();

		Integer gameZoneId = null;
		
		while(it.hasNext())
		{
			ZoneCache zc = it.next();
									
			if(zc.zone.getContentGroupID().equals(contentGroupID))
			{
				if(zc.contains(p.getLatitude(), p.getLongitude()))
				{
					// don't place in game zone if in a more specific zone
					if (zc.isGameZone())
						gameZoneId = zc.zone.getOrgId();
					else
						return zc.zone.getOrgId();
				}
			}
	    }

		// fallback to game zone
		return gameZoneId;
	}
	
	class MemberListener implements IDataspaceObjectsListener
	{
		MemberListener(){}
		
		public void objectsChanged(DataspaceObjectsEvent dose)
		{
			//logger.info("Engine::MemberListener triggered");
			
			ISession session = dataspace.getSession();
			session.begin(ISession.READ_WRITE);
			
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
							Member member2 = (Member) doe.getNewValue();							
							Member member = (Member) session.get(Member.class, member2.getID());
							
							if(member==null)
							{
								logger.info("member " + member2.getID() + " no longer available");
								continue;
							}
							
							if(member.getCarried()==false)
							{
								// check zones, consequences
								memberPlaced(session, member, false);
							}
						}
					}
					else if(doe.getRealChange() == DataspaceObjectEvent.OBJECT_MODIFIED)
					{
						if(doe.getNewValue()!=null && doe.getOldValue()!=null)
						{
							// carried to placed?
							Member member2 = (Member) doe.getNewValue();
							Member oldMember = (Member) doe.getOldValue();
							
							Member member = (Member) session.get(Member.class, member2.getID());
							
							if(member==null)
							{
								logger.info("member " + member2.getID() + " no longer available");
								continue;
							}
							
							// support first member as avatar (?)
							if(member.getCarried() == false)
							{
								if(oldMember.getCarried()==true)
								{
									// placed
									memberPlaced(session, member, false);
								}
								else
								{
									// moved?
									if(member.isSetPosition() && oldMember.isSetPosition())
									{
										if(ZoneCache.distanceBetweenPoints(member.getPosition(), oldMember.getPosition())>0)
										{
											memberPlaced(session, member, true);
										}
									}
								}
							}
						}
					}
					
				}
			}
			catch (Exception e)
			{
				logger.error("Engine::MemberListener error: " +e.toString(), new Throwable());
			}
			
			session.end();
		}			
	}
	
	class PlayerListener implements IDataspaceObjectsListener
	{
		PlayerListener(){}
		
		public void objectsChanged(DataspaceObjectsEvent dose)
		{
			//logger.info("Engine::PlayerListener triggered");
			
			try
			{
				Enumeration changes = dose.getDataspaceObjectEvents();
				
				while(changes.hasMoreElements())
				{
					DataspaceObjectEvent doe = (DataspaceObjectEvent) changes.nextElement();

					if(doe.getRealChange() == DataspaceObjectEvent.OBJECT_MODIFIED)
					{
						if(doe.getNewValue()!=null && doe.getOldValue()!=null)
						{
							// slave primary member
							ISession session = dataspace.getSession();
							session.begin(ISession.READ_WRITE);
							
							Player player = (Player) doe.getNewValue();
							
							QueryTemplate mqt = new QueryTemplate(Member.class);
							mqt.addConstraintEq("playerID", player.getID());
							mqt.addConstraintIsNull("parentMemberID");
							
							Object [] ms = session.match(mqt);
							
							if(ms.length>0)
							{
								Member member = (Member) ms[0];
								member.setPosition(player.getPosition());
								
								Game game = (Game) session.get(Game.class, member.getGameID());
								
								if(game!=null)						
								{
									member.setZone(getZoneID(game.getContentGroupID(), member.getPosition()));
								}
								session.update(member);					
							}
							
							session.end();
						}
					}
				}
			}
			catch (Exception e)
			{
				logger.error("Engine::PlayerListener error: " +e.toString(), new Throwable());
			}
		}			
	}	
	
	public boolean isSpawnable(ISession session, Member member, int maxMembers)
	{
		QueryTemplate mqt = new QueryTemplate(Member.class);
		mqt.addConstraintEq("playerID", member.getPlayerID());
			
		if(session.count(mqt)>=maxMembers)
		{
			logger.info("spawn refused for player " + member.getPlayerID() + " member " + member.getID() + " over quota");
			return false;
		}
		return true;
	}

	public boolean isKillable(ISession session, Member member)
	{
		if(member.isSetParentMemberID())
		{
			return true;
		}
		
		QueryTemplate mqt = new QueryTemplate(Member.class);
		mqt.addConstraintEq("playerID", member.getPlayerID());
		
		if(session.count(mqt)>1)
		{
			return true;
		}
		logger.info("kill refused for player " + member.getPlayerID() + " member " + member.getID() + " founder member");
		return false;
	}
	

	public void checkEvents()
	{		
		// for each active game, check for events since last tick
		
		ISession session = dataspace.getSession();
		session.begin(ISession.READ_WRITE);
		
		QueryTemplate gqt = new QueryTemplate(Game.class);
		gqt.addConstraintEq("state", Game.ACTIVE);
		
		Object [] gs = session.match(gqt);
		
		long now = System.currentTimeMillis();
		
		for(int i=0; i<gs.length; i++)
		{
			Game g = (Game) gs[i];
						
			if(g.getContentGroupID()!=null && g.getContentGroupID().length()>0
					&& g.getGameConfigID()!=null && g.getGameConfigID().length()>0)
			{
				GameTime gt = (GameTime) session.get(GameTime.class, g.getGameTimeID());
				
				if(gt==null)
				{
					continue;
				}
				
				GameConfig config = (GameConfig) session.get(GameConfig.class, g.getGameConfigID());
				
				if(config==null)
				{
					continue;
				}
				
				// real time elapsed
				long elapsed = now - this.lastEventCheckTime;
				float currentGameTime = (elapsed * this.timeRatio) + gt.getGameTime();

			   	QueryTemplate eq = new QueryTemplate(TimelineEvent.class);
		     	eq.addConstraintGt("startTime", gt.getGameTime());
		     	eq.addConstraintLe("startTime", currentGameTime);
		    	eq.addOrder("startTime");
		    	eq.addOrder("track");
		    	eq.addConstraintEq("enabled", 1);
		    	eq.addConstraintEq("contentGroupID", g.getContentGroupID());
		    	
		    	Object [] es = session.match(eq);
		    	
		    	for(int j=0; j<es.length; j++)
		    	{
		    		TimelineEvent e = (TimelineEvent) es[j];
		    		
		    		if(e.isSetTrack() && e.getTrack()==0)
		    		{
		    			// year event
			    		logger.info("Year event: " + e.getName());
		    			g.setYear(e.getName());
		    			session.update(g);
		    		}
		    		else
		    		{
		    			handleContentEvent(session, g, config, e);		    			
		    		}
		    	}
		    	
		    	logger.info("Done events from " + gt.getGameTime() + " to " + currentGameTime);
		    	
		    	gt.setGameTime(currentGameTime);

		    	// finish game?
		    	// 11100 ~= 2011
		    	//if(gt.getGameTime()>=11100)
		    	
		    	if(gt.getGameTime()>=config.getEndTime())
		    	{
		    		logger.info("Ending game " + g.getID() + " at " + gt.getGameTime() + " last year " + g.getYear());
		    		
		    		// last message		    	   	
	    	   		QueryTemplate pqt = new QueryTemplate(Player.class);
	    	   		pqt.addConstraintEq("gameID", g.getID());
		    	   		
	    	   		Object [] ps = session.match(pqt);
		    	   		
	    	   		for(int j=0; j<ps.length; j++)
	    	   		{
	    	   			Player p = (Player) ps[j];

		    		   	Message msg = new Message();
		    		   	msg.setGameTime(config.getEndTime());
		    		   	msg.setCreateTime(System.currentTimeMillis());
		    		   	msg.setYear(g.getYear());
	    		   		msg.setType(Message.MSG_TIMELINE_CONTENT_GLOBAL);
	    		   		
	    		   		QueryTemplate mqt = new QueryTemplate(Member.class);
	    		   		mqt.addConstraintEq("playerID", p.getID());
	    		   		
	    		   		int memberCount = session.count(mqt);

		   				if(config.isSetContextMsgEndTitle())
			   				msg.setTitle(ContextMessages.fillMembers(config.getContextMsgEndTitle(), memberCount));
		   				else
			   				msg.setTitle(ContextMessages.fillMembers(ContextMessages.MSG_END_TITLE, memberCount));

		   				if(config.isSetContextMsgEnd())
			   				msg.setDescription(ContextMessages.fillMembers(config.getContextMsgEnd(), memberCount));
		   				else
			   				msg.setDescription(ContextMessages.fillMembers(ContextMessages.MSG_END, memberCount));
		   				
		    		   	msg.setHandled(false);
		    			msg.setID(IDAllocator.getNewID(session, Message.class, "MSG", null));
		    		   	msg.setPlayerID(p.getID());
		    			session.add(msg);
		    	   	}
		    		
		    		g.setState(Game.ENDING);
		    		session.update(g);
		    		
		    	}
			}
		}

    	this.lastEventCheckTime = now;

		session.end();
	}
	
	
	public void memberPlaced(ISession session, Member member, boolean movement)
	{
		// FIXME members entering zones with active duration events?
		// FIXME there appear to be stat modifiers associated with Zones, this is not in the design doc.

		Game game = (Game) session.get(Game.class, member.getGameID());
		
		if(game==null)
		{
			return;
		}
		
		if(!Game.ACTIVE.equals(game.getState()))
		{
			return;
		}
		
		GameConfig config = (GameConfig) session.get(GameConfig.class, game.getGameConfigID());
		
		if(config==null)
		{
			return;
		}
		
		// client is now setting zone
		//member.setZone(this.getZoneID(game.getContentGroupID(), member.getPosition()));
		
	   	QueryTemplate mq = new QueryTemplate(Member.class);
	   	mq.addConstraintEq("gameID", game.getID());
	   	mq.addConstraintEq("carried", false);
	   	
	   	if(!member.isSetZone() || member.getZone()==0)
	   	{
	   		// they're in a "gap"
	   		return;
	   	}

   		mq.addConstraintEq("zone", member.getZone());

	   	Object [] ms = session.match(mq);
	   	
	   	/*
	   	 * FIXME don't trust this at the moment
	   	 */
	   	
	   	if(config.getEnableMultipleMemberZoneEffect()==1)
	   	{	   	
		   	if(movement==false)
		   	{	   	
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
			   	else if(ms.length>=9) // excluding ourselves
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
					   		if(other.getParentMemberID()==null || other.getParentMemberID().length()==0 || config.getEnableDeath()==0)
					   		{
					   			health = 1;
					   		}
					   		else
					   		{
				   				Message msg = new Message();
				   				msg.setID(IDAllocator.getNewID(session, Message.class, "MSG", null));
				   				msg.setCreateTime(System.currentTimeMillis());
				   				msg.setYear(game.getYear());
				   				msg.setType(Message.MSG_MEMBER_DIED);
				   				msg.setPlayerID(other.getPlayerID());
				   				msg.setHandled(false);
				   				
				   				String zoneName = null;
				   							   				
				   				if(other.isSetZone() && other.getZone()!=0)
				   				{
					   				QueryTemplate zqt = new QueryTemplate(Zone.class);
					   				zqt.addConstraintEq("orgId", other.getZone());
					   				
					   				Object [] zs = session.match(zqt);
					   				
					   				if(zs.length==1)
					   				{
					   					Zone z = (Zone) zs[0];
					   					zoneName = z.getName();
					   				}
				   				}
				   				
				   				if(config.isSetContextMsgDeathTitle())
					   				msg.setTitle(ContextMessages.fillZone(config.getContextMsgDeathTitle(), zoneName));
				   				else
					   				msg.setTitle(ContextMessages.fillZone(ContextMessages.MSG_DEATH_TITLE, zoneName));
				   				
				   				if(config.isSetContextMsgDeath())
					   				msg.setDescription(ContextMessages.fillZone(config.getContextMsgDeath(), zoneName));
				   				else
					   				msg.setDescription(ContextMessages.fillZone(ContextMessages.MSG_DEATH, zoneName));
				   			
				   				session.add(msg);
				   			
				   				logger.info("Player " + other.getPlayerID() + " member " + other.getID() + " killed by member " + member.getID() + " placement");
					   			
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
		   	}
	   	}
	   	
	   	
	   	if(config.getEnableAssimilation()==1)
	   	{
			
		   	int year = 0;
		   	
		   	try
		   	{	   	
		   		year = Integer.parseInt(game.getYear());
		   	}
		   	catch(NumberFormatException e)
		   	{
		   	}
		   	
			// proximity to others
		   	if(year==0 || year>=1914)
		   	{	   	
		   		if(this.isKillable(session, member))
		   		{		   		
				   	for(int i=0; i<ms.length; i++)
				   	{
				   		Member other = (Member) ms[i];
				   		
				   		if(other.getPlayerID().equals(member.getPlayerID()))
				   		{
				   			continue;
				   		}
				   					   		
				   		// cannot assimilate only member
				   		if(!this.isKillable(session, other))
				   		{
				   			continue;
				   		}	
				   		
				   		if(ZoneCache.distanceBetweenPoints(member.getPosition(), other.getPosition())<config.getProximityRadius())
				   		{
				   			if(member.getAction()>other.getAction())
				   			{
				   				logger.info("member " + other.getID() + " assimilated by " + member.getID() + " (triggered by " + member.getID() +")");
					   			// we assimilate them
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
					   			
				   				// message them
				   				Message msg = new Message();
				   				msg.setID(IDAllocator.getNewID(session, Message.class, "MSG", null));
				   				msg.setCreateTime(System.currentTimeMillis());
				   				msg.setYear(game.getYear());
				   				msg.setType(Message.MSG_MEMBER_ASSIMILATED);
				   				msg.setPlayerID(other.getPlayerID());
				   				msg.setHandled(false);
				   				
				   				String zoneName = null;
					   				
				   				if(other.isSetZone() && other.getZone()!=0)
				   				{
					   				QueryTemplate zqt = new QueryTemplate(Zone.class);
					   				zqt.addConstraintEq("orgId", member.getZone());
					   				
					   				Object [] zs = session.match(zqt);
					   				
					   				if(zs.length==1)
					   				{
					   					Zone z = (Zone) zs[0];
					   					zoneName = z.getName();
					   				}
				   				}
				   				
				   				if(config.isSetContextMsgAssimilatedTitle())
					   				msg.setTitle(ContextMessages.fillZone(config.getContextMsgAssimilatedTitle(), zoneName));
				   				else
					   				msg.setTitle(ContextMessages.fillZone(ContextMessages.MSG_ASSIMILATED_TITLE, zoneName));
				   				
				   				if(config.isSetContextMsgAssimilated())
					   				msg.setDescription(ContextMessages.fillZone(config.getContextMsgAssimilated(), zoneName));
				   				else
					   				msg.setDescription(ContextMessages.fillZone(ContextMessages.MSG_ASSIMILATED, zoneName));
			
				   				session.add(msg);
				   				
				   				// message us
				   				msg = new Message();
				   				msg.setID(IDAllocator.getNewID(session, Message.class, "MSG", null));
				   				msg.setCreateTime(System.currentTimeMillis());
				   				msg.setYear(game.getYear());
				   				msg.setType(Message.MSG_MEMBER_ASSIMILATED_OTHER);
				   				msg.setPlayerID(member.getPlayerID());
				   				msg.setHandled(false);
				   				
				   				zoneName = null;
				   				
				   				if(member.isSetZone() && member.getZone()!=0)
				   				{
					   				QueryTemplate zqt = new QueryTemplate(Zone.class);
					   				zqt.addConstraintEq("orgId", member.getZone());
					   				
					   				Object [] zs = session.match(zqt);
					   				
					   				if(zs.length==1)
					   				{
					   					Zone z = (Zone) zs[0];
					   					zoneName = z.getName();
					   				}
				   				}
				   				
				   				if(config.isSetContextMsgAssimilateTitle())
					   				msg.setTitle(ContextMessages.fillZone(config.getContextMsgAssimilateTitle(), zoneName));
				   				else
					   				msg.setTitle(ContextMessages.fillZone(ContextMessages.MSG_ASSIMILATE_TITLE, zoneName));
				   				
				   				if(config.isSetContextMsgAssimilate())
					   				msg.setDescription(ContextMessages.fillZone(config.getContextMsgAssimilate(), zoneName));
				   				else
					   				msg.setDescription(ContextMessages.fillZone(ContextMessages.MSG_ASSIMILATE, zoneName));
				   								   			
				   				session.add(msg);
				   				
				   				// assimilate
					   			other.setPlayerID(member.getPlayerID());
					   			other.setParentMemberID(member.getID());
				   				session.update(other);
				   				
				   				break;
				   				
				   			}
				   			else if(member.getAction()==other.getAction())
				   			{
				   				// do nothing, we are equal
				   				continue;
				   			}
				   			else
				   			{
				   				logger.info("member " + member.getID() + " assimilated by " + other.getID() + " (triggered by " + member.getID() +")");
				   				// they assimilate us
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
					   			session.update(other);
				   				
					   			// message us
				   				Message msg = new Message();
				   				msg.setID(IDAllocator.getNewID(session, Message.class, "MSG", null));
				   				msg.setCreateTime(System.currentTimeMillis());
				   				msg.setYear(game.getYear());
				   				msg.setType(Message.MSG_MEMBER_ASSIMILATED);
				   				msg.setPlayerID(member.getPlayerID());
				   				msg.setHandled(false);
				   				
				   				String zoneName = null;
				   				
				   				if(member.isSetZone() && member.getZone()!=0)
				   				{
					   				QueryTemplate zqt = new QueryTemplate(Zone.class);
					   				zqt.addConstraintEq("orgId", member.getZone());
					   				
					   				Object [] zs = session.match(zqt);
					   				
					   				if(zs.length==1)
					   				{
					   					Zone z = (Zone) zs[0];
					   					zoneName = z.getName();
					   				}
				   				}

				   				if(config.isSetContextMsgAssimilatedTitle())
					   				msg.setTitle(ContextMessages.fillZone(config.getContextMsgAssimilatedTitle(), zoneName));
				   				else
					   				msg.setTitle(ContextMessages.fillZone(ContextMessages.MSG_ASSIMILATED_TITLE, zoneName));
				   				
				   				if(config.isSetContextMsgAssimilated())
					   				msg.setDescription(ContextMessages.fillZone(config.getContextMsgAssimilated(), zoneName));
				   				else
					   				msg.setDescription(ContextMessages.fillZone(ContextMessages.MSG_ASSIMILATED, zoneName)); 	
				   			
				   				session.add(msg);
				   				
				   				// message them
				   				msg = new Message();
				   				msg.setID(IDAllocator.getNewID(session, Message.class, "MSG", null));
				   				msg.setCreateTime(System.currentTimeMillis());
				   				msg.setYear(game.getYear());
				   				msg.setType(Message.MSG_MEMBER_ASSIMILATED_OTHER);
				   				msg.setPlayerID(other.getPlayerID());
				   				msg.setHandled(false);
				   				
				   				zoneName = null;
				   				
				   				if(other.isSetZone() && other.getZone()!=0)
				   				{
					   				QueryTemplate zqt = new QueryTemplate(Zone.class);
					   				zqt.addConstraintEq("orgId", other.getZone());
					   				
					   				Object [] zs = session.match(zqt);
					   				
					   				if(zs.length==1)
					   				{
					   					Zone z = (Zone) zs[0];
					   					zoneName = z.getName();
					   				}
				   				}

				   				if(config.isSetContextMsgAssimilateTitle())
					   				msg.setTitle(ContextMessages.fillZone(config.getContextMsgAssimilateTitle(), zoneName));
				   				else
					   				msg.setTitle(ContextMessages.fillZone(ContextMessages.MSG_ASSIMILATE_TITLE, zoneName));
				   				
				   				if(config.isSetContextMsgAssimilate())
					   				msg.setDescription(ContextMessages.fillZone(config.getContextMsgAssimilate(), zoneName));
				   				else
					   				msg.setDescription(ContextMessages.fillZone(ContextMessages.MSG_ASSIMILATE, zoneName));
				   			
				   				session.add(msg);
				   				
				   				// assimilate	   				
					   			member.setPlayerID(other.getPlayerID());
					   			member.setParentMemberID(other.getID());
				   				session.update(member);
				   				
	
				   				break;
				   			}
				   		}
				   	}
			   	}
			}
	   	}
	   	
	   	// finally, does this mean anyone can author ?
	   	if(config.getEnableAuthoringQuota()==1)
	   		this.updateAuthorable(session, game);
	}

	
	public void handleContentEvent(ISession session, Game game, GameConfig config, TimelineEvent event)
	{		
	   	QueryTemplate mq = new QueryTemplate(Member.class);
	   	mq.addConstraintEq("gameID", game.getID());
	   	mq.addConstraintEq("carried", false);
	   	
	   	if(event.getZoneId()!=0)
	   	{
	   		mq.addConstraintEq("zone", event.getZoneId());
	   	}
	   	
	   	Object [] ms = session.match(mq);

		logger.info("Content event: " + event.getID() + " " + event.getName() + " (zone: " + event.getZoneId() + ") affects " + ms.length + " members");
	   	
	   	Set<String> playerIDs = new HashSet<String>();
	   	//Set<String> playerIDsBirth = new HashSet<String>();
	   	//Set<String> playerIDsDeath = new HashSet<String>();
	   	
	   	int deaths = 0;
	   	
	   	for(int i=0; i<ms.length; i++)
	   	{
	   		Member member = (Member) ms[i];
	   		
	   		// where is this player's primary member
	   		if(!playerIDs.contains(member.getPlayerID()))
	   		{   		
		   		if(event.getZoneId()!=0)
		   		{
		   			// CMG - we don't get a zone-specific message unless we 
		   			// have a Member in this zone with ParentMemberID set and
		   			// another member in this zone with it null ... eh??
		   			// I think we should get it (potentially) if we have any
		   			// member in this zone
			   		playerIDs.add(member.getPlayerID());

/*		   			// not global
		   			if(member.isSetParentMemberID())
		   			{
		   				// find root parent
		   				QueryTemplate pmqt = new QueryTemplate(Member.class);
		   				pmqt.addConstraintEq("playerID", member.getPlayerID());
		   				pmqt.addConstraintIsNull("parentMemberID");
		   				
		   				Object [] pms = session.match(pmqt);
		   				
		   				if(pms.length>0)
		   				{
		   					Member pm = (Member) pms[0];
		   					
		   					if(pm.isSetZone() && pm.getZone()==event.getZoneId())
		   					{
		   				   		// this *player* needs to be notified
		   				   		playerIDs.add(member.getPlayerID());
		   					}
		   				}
		   			}
*/
		   		}
		   		else
		   		{
		   			// add all players for global events at the end
		   		}
	   		}
	   		
	   		if(config.getEnableEventEffects()==1)
	   		{	   		
	   			if(event.getHealth()!=0)
	   			{
	   				int max = 10;
	   				int min = 0;
	   				
	   				if(event.isSetHealthMax() && event.getHealthMax()!=0)
	   					max = event.getHealthMax();
	   				if(event.isSetHealthMin())
	   					min = event.getHealthMin();
	
		   			int health = member.getHealth();
	   				
		   			if(health>=min && health<=max)
		   			{			   			
			   			health += event.getHealth();
			   					   			
			   			if(health>10)
			   			{
			   				health = 10;
			   			}
			   			else if(health<=0)
			   			{
				   			// initial member
			   				if(!this.isKillable(session, member) || config.getEnableDeath()!=1)
			   				{
				   				health = 1;
				   			}
				   			else
				   			{
				   			   	logger.info("Player " + member.getPlayerID() + " member " + member.getID() + " killed by " + event.getID());
				   			   		
				   				Message msg = new Message();
				   				msg.setID(IDAllocator.getNewID(session, Message.class, "MSG", null));
				   				msg.setCreateTime(System.currentTimeMillis());
				   				msg.setYear(game.getYear());
				   				msg.setType(Message.MSG_MEMBER_DIED);
				   				msg.setPlayerID(member.getPlayerID());
				   				msg.setHandled(false);
				   				
				   				String zoneName = null;
				   				
				   				if(member.isSetZone() && member.getZone()!=0)
				   				{
					   				QueryTemplate zqt = new QueryTemplate(Zone.class);
					   				zqt.addConstraintEq("orgId", member.getZone());
					   				
					   				Object [] zs = session.match(zqt);
					   				
					   				if(zs.length==1)
					   				{
					   					Zone z = (Zone) zs[0];
					   					zoneName = z.getName();
					   				}
				   				}
				   				
				   				if(config.isSetContextMsgDeathTitle())
					   				msg.setTitle(ContextMessages.fillZone(config.getContextMsgDeathTitle(), zoneName));
				   				else
					   				msg.setTitle(ContextMessages.fillZone(ContextMessages.MSG_DEATH_TITLE, zoneName));

				   				if(config.isSetContextMsgDeath())
					   				msg.setDescription(ContextMessages.fillZone(config.getContextMsgDeath(), zoneName));
				   				else
					   				msg.setDescription(ContextMessages.fillZone(ContextMessages.MSG_DEATH, zoneName)); 
				   				
				   				session.add(msg);

					   				
					   			deaths++;
				   				
				   				session.remove(member);
				   				continue;
				   			}
			   			}
			   			
			   			member.setHealth(health);
		   			}
	   			}
	   			
	   			if(event.getWealth()!=0)
	   			{
	   				int max = 10;
	   				int min = 0;
	   				
	   				if(event.isSetWealthMax() && event.getWealthMax()!=0)
	   					max = event.getWealthMax();
	   				if(event.isSetWealthMin())
	   					min = event.getWealthMin();
	
		   			int wealth = member.getWealth();
	   				
		   			if(wealth>=min && wealth<=max)
		   			{	
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
	   			}
	   			
	   			if(event.getAction()!=0)
	   			{
	   				int max = 10;
	   				int min = 0;
	   				
	   				if(event.isSetActionMax() && event.getActionMax()!=0)
	   					max = event.getActionMax();
	   				if(event.isSetActionMin())
	   					min = event.getActionMin();
	
		   			int action = member.getAction();
	   				
		   			if(action>=min && action<=max)
		   			{	
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
	   			}
   			
	   			if(event.getBrains()!=0)
	   			{
	   				int max = 10;
	   				int min = 0;
	   				
	   				if(event.isSetBrainsMax() && event.getBrainsMax()!=0)
	   					max = event.getBrainsMax();
	   				if(event.isSetBrainsMin())
	   					min = event.getBrainsMin();
	
		   			int brains = member.getBrains();
	   				
		   			if(brains>=min && brains<=max)
		   			{	
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
	   			}
   			
   			
	   			// create offspring
	   			if(member.getHealth()>=8 && config.getEnableOffspring()==1)
	   			{
		   			boolean spawnable = this.isSpawnable(session, member, config.getMaxMembers());
	   				
	   				if(member.getWealth()>=8 && member.getAction()>=8)
	   				{
	   					// FIXME reduce wealth too?!
	   					member.setWealth(6);
		   				member.setHealth(6);
		   				member.setAction(6);
		   				
		   				Player p = (Player) session.get(Player.class, member.getPlayerID());
		   				
		   				if(p!=null)
		   				{
		   					// player notified by Player object change
	   						logger.info("Player " + member.getPlayerID() + " member " + member.getID() + " given quota inc by " + event.getID());
		   					if(spawnable)
		   					{
		   						p.setNewMemberQuota(p.getNewMemberQuota()+1);
		   					}
		   				}
		   				
		   				// CMG don't think this should continue - misses the .update(member)
	   					//continue;
	   				}
	   				else
	   				{	
		   			   	logger.info("Player " + member.getPlayerID() + " member " + member.getID() + " spawned by " + event.getID());
	
		   				member.setHealth(6);
		   				
		   			   	if(spawnable)
		   			   	{		
			   				Member offspring = new Member();
			   				offspring.setID(IDAllocator.getNewID(session, Member.class, "M", null));
			   				offspring.setCarried(false);
			   				offspring.setGameID(member.getGameID());
			   				offspring.setPlayerID(member.getPlayerID());
			   				offspring.setParentMemberID(member.getID());
			   				
			   				// random health 2-5
			   				offspring.setHealth(random.nextInt(4)+2);
			   				
			   				offspring.setColourRef(member.getColourRef());
			   				offspring.setLimbData(member.getLimbData());
			   				
			   				// FIXME - server generated offspring names, how are they generated?
			   				offspring.setName(member.getName() + random.nextInt(1000));
			   				
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
			   				double xoffset = (random.nextDouble()*config.getSpawnRadius()*2)-config.getSpawnRadius();
			   				double yoffset = (random.nextDouble()*config.getSpawnRadius()*2)-config.getSpawnRadius();
			   				
			   				Position p = new Position();
			   				p.setElevation(0.0);
			   				p.setLongitude(member.getPosition().getLongitude()+xoffset);
			   				p.setLatitude(member.getPosition().getLatitude()+yoffset);
			   				
			   				offspring.setPosition(p);
			   				
			   				// still need server zones for this?
			   				offspring.setZone(this.getZoneID(game.getContentGroupID(), p));
			   				
			   				session.add(offspring);
		   				
		   				
			   				Message msg = new Message();
			   				msg.setID(IDAllocator.getNewID(session, Message.class, "MSG", null));
			   				msg.setCreateTime(System.currentTimeMillis());
			   				msg.setYear(game.getYear());
			   				msg.setType(Message.MSG_MEMBER_CREATED);
			   				msg.setPlayerID(member.getPlayerID());
			   				msg.setHandled(false);
			   				
			   				String zoneName = null;
			   				
			   				if(member.isSetZone() && member.getZone()!=0)
			   				{
				   				QueryTemplate zqt = new QueryTemplate(Zone.class);
				   				zqt.addConstraintEq("orgId", member.getZone());
				   				
				   				Object [] zs = session.match(zqt);
				   				
				   				if(zs.length==1)
				   				{
				   					Zone z = (Zone) zs[0];
				   					zoneName = z.getName();
				   				}
			   				}
			   				
			   				if(config.isSetContextMsgBirthTitle())
				   				msg.setTitle(ContextMessages.fillZone(config.getContextMsgBirthTitle(), zoneName));
			   				else
				   				msg.setTitle(ContextMessages.fillZone(ContextMessages.MSG_BIRTH_TITLE, zoneName));

			   				if(config.isSetContextMsgBirth())
				   				msg.setDescription(ContextMessages.fillZone(config.getContextMsgBirth(), zoneName));
			   				else
				   				msg.setDescription(ContextMessages.fillZone(ContextMessages.MSG_BIRTH, zoneName)); 
			   			
			   				session.add(msg);
	
		   			   	}
	   				}
	   			}
   			
	   			session.update(member);
		
	   		}
	   	}
	   	
	   	// all players notified on global events
	   	if(event.getZoneId()==0)
	   	{	   		
	   		QueryTemplate pqt = new QueryTemplate(Player.class);
	   		pqt.addConstraintEq("gameID", game.getID());
	   		
	   		Object [] ps = session.match(pqt);
	   		
	   		for(int i=0; i<ps.length; i++)
	   		{
	   			Player p = (Player) ps[i];
	   			playerIDs.add(p.getID());
	   		}
	   	}	   	

	   	Iterator<String> it = playerIDs.iterator();
	   	
	   	while(it.hasNext())
	   	{
	   		String playerID = it.next();
	   		
		   	Message msg = new Message();
		   	msg.setGameTime(event.getStartTime());
		   	msg.setCreateTime(System.currentTimeMillis());
		   	msg.setYear(game.getYear());
		   	
		   	if(event.getZoneId()==0)
		   	{
		   		msg.setType(Message.MSG_TIMELINE_CONTENT_GLOBAL);
		   	}
		   	else if (config!=null && config.isSetEnableLocalMessagePriority() && config.getEnableLocalMessagePriority()==1)
		   	{
		   		// option to elevate priority of timeline messages over global messages
		   		msg.setType(Message.MSG_PRIORITY_TIMELINE_CONTENT);
		   	}
		   	else
		   	{
		   		msg.setType(Message.MSG_TIMELINE_CONTENT);
		   	}
		   	
		   	msg.setTitle(event.getName());
		   	msg.setDescription(event.getDescription());
		   	msg.setHandled(false);
			
		   	msg.setID(IDAllocator.getNewID(session, Message.class, "MSG", null));
		   	msg.setPlayerID(playerID);
		   	logger.info("Player " + msg.getPlayerID() + " notified of event " + event.getID());
			session.add(msg);
	   	}

	   	// health scare in a particular, all players
	   	
	   	if(deaths>4 && event.getZoneId()!=0)
	   	{
	   		String titleText = null;
	   		String descText = null;
	   		String zoneName = null;
   				
   			if(event.isSetZoneId() && event.getZoneId()!=0)
   			{
	   			QueryTemplate zqt = new QueryTemplate(Zone.class);
	   			zqt.addConstraintEq("orgId", event.getZoneId());
	   			
	   			Object [] zs = session.match(zqt);
	   			
	   			if(zs.length==1)
	   			{
	   				Zone z = (Zone) zs[0];
	   				zoneName = z.getName();
	   			}
   			}
   				
   			if(config.isSetContextMsgScareTitle())
   	   			titleText = ContextMessages.fillZone(config.getContextMsgScareTitle(), zoneName);
   			else
   	   			titleText = ContextMessages.fillZone(ContextMessages.MSG_SCARE_TITLE, zoneName);
   			
   			if(config.isSetContextMsgScare())
   				descText = ContextMessages.fillZone(config.getContextMsgScare(), zoneName);
   			else
   				descText = ContextMessages.fillZone(ContextMessages.MSG_SCARE, zoneName);
   			
	   		
	   		QueryTemplate pqt = new QueryTemplate(Player.class);
		   	pqt.addConstraintEq("gameID", game.getID());
		   		
		   	Object [] ps = session.match(pqt);
		   		
		   	for(int i=0; i<ps.length; i++)
		   	{
		   		Player p = (Player) ps[i];
		   		
			   	Message msg = new Message();
			   	msg.setCreateTime(System.currentTimeMillis());
			   	msg.setYear(game.getYear());
			   	msg.setType(Message.MSG_MEMBER_DIED);
			   	
			   	msg.setTitle(titleText);
			   	msg.setDescription(descText);
			   	msg.setHandled(false);
				
			   	msg.setID(IDAllocator.getNewID(session, Message.class, "MSG", null));
			   	msg.setPlayerID(p.getID());
			   	logger.info("Player " + msg.getPlayerID() + " notified of health scare by " + event.getID());
				session.add(msg);
		   	}
	   	}
	   		
	   	if(config.getEnableAuthoringQuota()==1)
	   		this.updateAuthorable(session, game);
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
}
