package hkust.comp3111h.ballcraft.client;


import java.util.ArrayList;

import org.jbox2d.common.Vec2;


public class GameInput {
	
	public Vec2 acceleration;
	
	private ArrayList<Skill> activeSkills = null;
	
	public GameInput()
	{
		activeSkills = new ArrayList<Skill>();
	}
	
	public String serialize() {
		return null;
	}
	
	public static GameInput decodeSerializedInput(String serialized) {
		return null;
	}
	
	public ArrayList<Skill> getSkills() {
		return activeSkills;
	}
	
	public void addSkill(Skill skill) {
		activeSkills.add(skill);
	}
	
	public void clearSkill() {
		activeSkills.clear();
	}
	
	public String toSerializedString() {
		String serialized = "";
		serialized += acceleration.x + "," + acceleration.y;
		return serialized;
	}
	
}
