package uk.ac.horizon.ug.exploding.orchestration;

import java.util.Vector;

import uk.ac.horizon.ug.exploding.db.Member;

public class PlayerBean
{
	protected String id;
	
	protected String name;
	
	protected int health;
	
	protected int wealth;
	
	protected int action;
	
	protected int brains;
	
	protected int memberCount;
	
	protected double latitude;
	
	protected double longitude;
	
	protected String founderMember;
	
	protected String founderZone;
	
	protected int newMemberQuota;
	
	protected boolean canAuthor;
	
	protected Vector<Member> members;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getWealth() {
		return wealth;
	}

	public void setWealth(int wealth) {
		this.wealth = wealth;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public int getBrains() {
		return brains;
	}

	public void setBrains(int brains) {
		this.brains = brains;
	}

	public int getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(int memberCount) {
		this.memberCount = memberCount;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getFounderMember() {
		return founderMember;
	}

	public void setFounderMember(String founderMember) {
		this.founderMember = founderMember;
	}

	public String getFounderZone() {
		return founderZone;
	}

	public void setFounderZone(String founderZone) {
		this.founderZone = founderZone;
	}

	public int getNewMemberQuota() {
		return newMemberQuota;
	}

	public void setNewMemberQuota(int newMemberQuota) {
		this.newMemberQuota = newMemberQuota;
	}

	public boolean isCanAuthor() {
		return canAuthor;
	}

	public void setCanAuthor(boolean canAuthor) {
		this.canAuthor = canAuthor;
	}

	public Vector<Member> getMembers() {
		return members;
	}

	public void setMembers(Vector<Member> members) {
		this.members = members;
	}	
}
