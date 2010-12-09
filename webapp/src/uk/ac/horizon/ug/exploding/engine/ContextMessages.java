package uk.ac.horizon.ug.exploding.engine;

public class ContextMessages
{
	public static final String MSG_BIRTH = "Congratulations, there has been a birth<zone>, welcome your new community member.";

	public static final String MSG_BIRTH_TITLE = "Congratulations, there has been a birth in your community<zone>";

	public static final String MSG_DEATH = "Sadly you have lost a community member<zone>. You may need to move the rest of the community in the area to protect their health.";

	public static final String MSG_DEATH_TITLE = "Sadly you have lost a community member, check the health of the other members<zone>";

	public static final String MSG_END = "Congratulations! Your community has survived for 120 years in Woolwich, you have <members> remaining.  Please return to the Tramshed as quickly as possible.";
	
	public static final String MSG_END_TITLE = "Please return to the Tramshed as quickly as possible.";

	public static final String MSG_SCARE = "There has been a health scare<zone>, check the health of your community members";
	
	public static final String MSG_SCARE_TITLE = "There has been a health scare<zone>, check the health of your community members";
	
	public static final String MSG_ASSIMILATED = "One of your community members has joined another community<zone>, the other community was stronger, they intermarried, you stay in touch but it is time to say goodbye and let them go out into the world without you.";

	public static final String MSG_ASSIMILATED_TITLE = "One of your community members has joined another community<zone>";
	
	public static final String MSG_ASSIMILATE = "Another player’s community members have joined your community<zone>, welcome the new members and look after them as if they were your own.";
	
	public static final String MSG_ASSIMILATE_TITLE = "Another player’s community members have joined your community<zone>";

	
	public static String fillMembers(String message, int members)
	{
		if(members==1)
		{
			return message.replace("<members>", members + " member");
		}
		else
		{
			return message.replace("<members>", members + " members");			
		}
	}
	
	public static String fillZone(String message, String zone)
	{
		if(zone!=null && zone.length()>0)
		{
			return message.replace("<zone>", " in " + zone);
		}
		else
		{
			return message.replace("<zone>", "");			
		}
	}
}
