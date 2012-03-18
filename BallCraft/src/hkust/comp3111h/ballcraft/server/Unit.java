package hkust.comp3111h.ballcraft.server;

import hkust.comp3111h.ballcraft.graphics.Drawable;

import java.util.ArrayList;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

public abstract class Unit implements Drawable {

	protected Body body;
	
	private ServerGameState gameState = ServerGameState.getStateInstance();
	
	public Unit() {
	}
	
	public static Unit fromSerializedString(String serialized) {
		String [] parts = serialized.split(":");
		if (parts[0].equals("ball")) {
			String [] vals = parts[1].split(",");
			float x = Float.valueOf(vals[0]);
			float y = Float.valueOf(vals[1]);
			float radius = Float.valueOf(vals[2]);
			return new Ball(radius, 0, 0, new Vec2(x, y));
		} else if (parts[0].equals("wall")) {
			// TODO
		}
		return null;
	}
	
	public Unit(float size, float mass, float friction, Vec2 position) {

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC; // dynamic means it is subject to forces
		bodyDef.position.set(position.x, position.y);
		body = ServerGameState.world.createBody(bodyDef);
		CircleShape dynamicBox = new CircleShape();
		dynamicBox.m_radius = size;
		
		FixtureDef fixtureDef = new FixtureDef(); // fixture def that we load up with the following info:
		fixtureDef.shape = dynamicBox; // ... its shape is the dynamic box (2x2 rectangle)
		fixtureDef.density = mass; // ... its density is 1 (default is zero)
		fixtureDef.friction = friction; // ... its surface has some friction coefficient
		body.createFixture(fixtureDef); // bind the dense, friction-laden fixture to the body
		
		gameState.addUnit(this);
	}
	
	public void move() 
	{
		// TODO: change function name and body
		/*
		unitdata.position.x = body.getPosition().x * 10;
		unitdata.position.y = body.getPosition().y * 10;
		*/
	}
	
	public void applyForce(Vec2 force) {
		// TODO
	}
	
	public abstract String toSerializedString();
	
}