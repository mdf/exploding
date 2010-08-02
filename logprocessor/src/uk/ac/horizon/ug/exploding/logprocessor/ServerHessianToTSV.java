/**
 * 
 */
package uk.ac.horizon.ug.exploding.logprocessor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import uk.ac.horizon.ug.exploding.db.Game;
import uk.ac.horizon.ug.exploding.db.Member;
import uk.ac.horizon.ug.exploding.db.Message;
import uk.ac.horizon.ug.exploding.db.Player;
import uk.ac.horizon.ug.exploding.db.Position;

import equip2.core.DataspaceObjectEvent;
import equip2.core.logging.LogEntry;
import equip2.core.logging.LogHeader;
import equip2.core.marshall.impl.DefaultContext;
import equip2.core.marshall.impl.HessianUnmarshallRegistry;
import equip2.core.objectsupport.impl.DefaultObjectHelperRegistry;
import equip2.core.objectsupport.impl.j2se.J2SEObjectHelperRegistry;

/**
 * @author cmg
 *
 */
public class ServerHessianToTSV {

	static String GAME_NAME = "game_name";
	static String PLAYER_NAME = "player_name";
	static String LATITUDE = "latitude";
	static String LONGITUDE = "longitude";
	static String MESSAGE_TYPE = "message_type";
	static String MESSAGE_TITLE = "message_title";
	static String MESSAGE_TEXT = "message_text";
	static String MESSAGE_YEAR = "message_year";
	static String GAME_YEAR = "game_year";
	static String MEMBER_ID = "member_id";
	static String MEMBER_NAME = "member_name";
	static String MEMBER_ATTRIBS = "member_attribs";
	static String MEMBER_LATITUDE = "member_latitude";
	static String MEMBER_LONGITUDE = "member_latitude";
	static String MEMBER_CARRIED = "member_carried";
	static String STANDARD_HEADINGS [] =  new String[] {
		"time", "pretty_time", "game_id", "player_id", "pretty_event", "event" 
	};
	static String EXTRA_HEADINGS [] = new String[] {
		GAME_NAME, GAME_YEAR, PLAYER_NAME, LATITUDE, LONGITUDE, 
		MESSAGE_TYPE, MESSAGE_TITLE, MESSAGE_TEXT, MESSAGE_YEAR,
		MEMBER_ID, MEMBER_NAME, MEMBER_ATTRIBS, MEMBER_LATITUDE, MEMBER_LONGITUDE, MEMBER_CARRIED
	};
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length==0) {
			System.err.println("Usage: java "+ServerHessianToTSV.class.getName()+" <hessian-server-logfile> ...");
			System.exit(-1);
		}
		try {
		    HessianUnmarshallRegistry unmarshallRegistry = new HessianUnmarshallRegistry();
		    DefaultObjectHelperRegistry registry = new J2SEObjectHelperRegistry();
			DefaultContext unmarshallContext = new DefaultContext(registry, unmarshallRegistry);
			
			for (int fi=0; fi<args.length; fi++) {
				File logfile = new File(args[fi]);
				System.err.println("Process log file "+logfile);
				InputStream fis = new BufferedInputStream(new FileInputStream(logfile));
				
				LogHeader header = (LogHeader)unmarshallContext.unmarshall(fis);
				System.err.println("Start time: "+header.getStartTime()+" ("+new Date(header.getStartTime())+")");
				System.err.println("Metadata: "+header.getMetadata());
				System.err.println("Checkpoint: "+header.getCheckpoint().length+" objects");
				
				clearCache();
				
				Object checkpoint [] = header.getCheckpoint();
				for (int ci=0; ci<checkpoint.length; ci++) 
					processChange(header.getStartTime(), null, checkpoint[ci], DataspaceObjectEvent.LISTENER_ADDED);
				System.err.println("Processed checkpoint");
				int count = 0;
				while (true) {
					Object o = unmarshallContext.unmarshall(fis);
					if (o instanceof LogEntry) {
						LogEntry entry = (LogEntry)o;
						if (!entry.getFailed() && entry.isSetTime()) {
							int change = 0;
							if (entry.isSetChange()) 
								change = entry.getChange();
							else if (entry.getOldValue()==null)
								change = DataspaceObjectEvent.OBJECT_ADDED;
							else if (entry.getNewValue()==null)
								change = DataspaceObjectEvent.OBJECT_REMOVED;
							else
								change = DataspaceObjectEvent.OBJECT_MODIFIED;
							processChange(entry.getTime(), entry.getOldValue(), entry.getNewValue(), change);
						}
						else
							System.err.println("Entry: "+entry.getTime()+","+entry.getChange()+","+entry.getFailed()+","+entry.getSessionNumber()+","+entry.getOldValue()+","+entry.getNewValue());
					}
					else
						System.err.println("Read: "+o);
					count++;
					//debug
					//if (count>10)
					//	break;
				}
			}
		}
		catch (Exception e) {
			System.err.println("ERROR: "+e);
			e.printStackTrace(System.err);
		}
	}

	static Map<String,PrintWriter> outputFiles = new HashMap<String,PrintWriter>();
	static Map<String,Player> playerCache;
	static void clearCache() {
		playerCache = new HashMap<String,Player>();
	}
	
	static void processChange(long time, Object oldValue, Object newValue, int change) throws IOException {
		HashMap<String,Object> values = new HashMap<String,Object>();
		if (newValue instanceof Game) {
			Game ng = (Game)newValue;
			Game og = (Game)oldValue;
			values.put(GAME_NAME, ng.getName());
			values.put(GAME_YEAR, ng.getYear());
			if (og==null) {				
				writeLine(time, ng.getID(), null, "Game "+ng.getID()+" '"+ng.getName()+"' created ("+ng.getState()+")", "game:added:"+ng.getState(), values);
			}
			else {
				if (!ng.getState().equals(og.getState()))
					writeLine(time, ng.getID(), null, "Game "+ng.getID()+" '"+ng.getName()+"' changed to "+ng.getState(),"game:changed:"+ng.getState(), values);				
				if (ng.isSetYear() && !ng.getYear().equals(og.getYear())) {
					writeLine(time, ng.getID(), null, "Game "+ng.getID()+" '"+ng.getName()+"' now year "+ng.getYear(), "game:year", values);
				}
			}
		}
		else if (newValue instanceof Player) {
			Player np = (Player)newValue;
			Player op = (Player)oldValue;
			if (np!=null) 
				values.put(PLAYER_NAME, np.getName());

			if (op==null) {
				writeLine(time, np.getGameID(), np.getID(), "Player "+np.getID()+" '"+np.getName()+"' created in game "+np.getGameID(), "player:added", values);
			}
			Position pos = np.getPosition();
			if (pos!=null && (op==null || op.getPosition()==null || np.getPositionUpdateTime()>op.getPositionUpdateTime())) {
				values.put(LATITUDE, pos.getLatitude());
				values.put(LONGITUDE, pos.getLongitude());
				writeLine(time, np.getGameID(), np.getID(), "Player "+np.getID()+" '"+np.getName()+"' moves to "+pos.getLatitude()+","+pos.getLongitude(), "player:move", values);
			}
			if (np!=null)
				playerCache.put(np.getID(), np);
		}
		else if (newValue instanceof Message) {
			Message nm = (Message)newValue;
			Message om = (Message)oldValue;
			if (nm.getHandled()) {
				// only handled messages...
				Player p = playerCache.get(nm.getPlayerID());
				if (p==null)
				{
					System.err.println("Message "+nm.getID()+" for unknown player "+nm.getPlayerID());
					return;
				}
				values.put(PLAYER_NAME, p.getName());
				values.put(MESSAGE_TYPE, nm.getType());
				values.put(MESSAGE_TITLE, nm.getTitle());
				values.put(MESSAGE_TEXT, nm.getDescription());
				values.put(MESSAGE_YEAR, nm.getYear());
				writeLine(time, p.getGameID(), nm.getPlayerID(), "Player "+p.getID()+" '"+p.getName()+"' sent "+nm.getType()+" '"+nm.getTitle()+"' ("+nm.getYear()+") ...", "player:message", values);
			}
		}
		else if (newValue instanceof Member) {
			Member nm = (Member)newValue;
			Member om = (Member)oldValue;
			Member m = nm!=null ? nm : om;
			String attribs =  "H:"+m.getHealth()+",W:"+m.getWealth()+",K:"+m.getBrains()+",P:"+m.getAction();
			values.put(MEMBER_ATTRIBS, attribs);
			Position pos = m.getPosition();
			if (pos!=null) {
				values.put(MEMBER_LATITUDE, pos.getLatitude());
				values.put(MEMBER_LONGITUDE, pos.getLongitude());
			}
			values.put(MEMBER_CARRIED, m.getCarried());
			values.put(MEMBER_ID, m.getID());
			values.put(MEMBER_NAME, m.getName());
			Player p = playerCache.get(nm.getPlayerID());
			if (p==null)
			{
				System.err.println("Member "+nm.getID()+" for unknown player "+nm.getPlayerID());
				return;
			}
			values.put(PLAYER_NAME, p.getName());
			if (om==null) {
				if (nm.getCarried()) 
					writeLine(time, nm.getGameID(), nm.getPlayerID(), "Player "+p.getID()+" '"+p.getName()+"' creates member "+nm.getID()+" with "+attribs, "player:create_member", values);
				else
					writeLine(time, nm.getGameID(), nm.getPlayerID(), "Server creates member "+nm.getID()+" with "+attribs+" for player "+p.getID()+" '"+p.getName()+"': ", "server:create_member", values);					
			} else if (nm==null) {
				writeLine(time, nm.getGameID(), nm.getPlayerID(), "Server deletes member "+nm.getID()+" with "+attribs+" for player "+p.getID()+" '"+p.getName()+"'", "server:delete_member", values);									
			}
			else {
				if (nm.getCarried() && !om.getCarried()) 
					writeLine(time, nm.getGameID(), nm.getPlayerID(), "Player "+p.getID()+" '"+p.getName()+"' carries member "+nm.getID(), "player:carry_member", values);
				else if (!nm.getCarried() && om.getCarried()) 
					writeLine(time, nm.getGameID(), nm.getPlayerID(), "Player "+p.getID()+" '"+p.getName()+"' places member "+nm.getID(), "player:place_member", values);
				else {
					String delta = (nm.getPosition().equals(om.getPosition()) ? "Pos," : "")+
					"H:"+(nm.getHealth()>om.getHealth() ? "+" : "")+(nm.getHealth()-om.getHealth())+
					",W:"+(nm.getWealth()>om.getWealth() ? "+" : "")+(nm.getWealth()-om.getWealth())+
					",K:"+(nm.getBrains()>om.getBrains() ? "+" : "")+(nm.getBrains()-om.getBrains())+
					",P:"+(nm.getAction()>om.getAction() ? "+" : "")+(nm.getAction()-om.getAction());
					writeLine(time, nm.getGameID(), nm.getPlayerID(), "Server changes member "+nm.getID()+" by "+delta+" leaving "+attribs+" for player "+p.getID()+" '"+p.getName()+"'", "server:change_member", values);
				}
			}
		}
	}

	private static void writeLine(long time, String gameId, String playerId,
			String pretty_event, String event, Map<String,Object> values) throws IOException {
		StringBuilder sb = new StringBuilder();
		if (playerId==null)
			playerId = "";
		sb.append(time+"\t"+new Date(time)+"\t"+gameId+"\t"+playerId+"\t"+pretty_event+"\t"+event);
		for (int hi=0; hi<EXTRA_HEADINGS.length; hi++) {
			sb.append("\t");
			if (values.containsKey(EXTRA_HEADINGS[hi]))
				sb.append(escapeString(values.get(EXTRA_HEADINGS[hi])));
		}
		for (String key : values.keySet()) {
			boolean found = false;
			for (int hi=0; !found && hi<EXTRA_HEADINGS.length; hi++) {
				if (EXTRA_HEADINGS[hi].equals(key))
					found = true;
			}
			if (!found)
				throw new RuntimeException("Unknown heading '"+key+"'");
		}
		if (gameId==null) {
			System.err.println("Null game: "+sb.toString());
			return;
		}
		//System.out.println();
		PrintWriter pw = outputFiles.get(gameId);
		if (pw==null) {
			File outfile = new File(gameId+".log");
			System.err.println("Starting output file "+outfile);
			pw = new PrintWriter(new FileWriter(outfile));
			outputFiles.put(gameId, pw);		
			
			for (int hi=0; hi<STANDARD_HEADINGS.length; hi++) {
				pw.print(STANDARD_HEADINGS[hi]);
				pw.print("\t");
			}
			for (int hi=0; hi<EXTRA_HEADINGS.length; hi++) {
				pw.print(EXTRA_HEADINGS[hi]);
				if (hi+1 < EXTRA_HEADINGS.length)
					pw.print("\t");
			}
			pw.println();
			pw.flush();
		}
		sb.append("\n");
		pw.print(sb.toString());
		pw.flush();
	}

	private static String escapeString(Object object) {
		if (object==null)
			return "";
		String s = object.toString();
		StringBuffer sb = new StringBuffer();
		for (int ci=0; ci<s.length(); ci++) {
			char c= s.charAt(ci);
			if (c=='\t') 
				sb.append("\\t");
			else if (c=='\n')
				sb.append("\\n");
			else if (Character.isISOControl(c)) 
				sb.append("?");
			else
				sb.append(c);		
		}
		return sb.toString();
	}
}
