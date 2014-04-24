package com.salmon.lambygame.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.salmon.lambygame.manager.ResourcesManager;

public abstract class Player extends AnimatedSprite {
	
	private Body body;
	private int footContacts = 0;
	
	public Player(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld) {
        super(pX, pY, ResourcesManager.getInstance().player_region, vbo);
        camera.setChaseEntity(this);
    }
	
	public abstract void onDie();

	public void jump() {
		body.setLinearVelocity(new Vector2(body.getLinearVelocity().x,-12));
	}
		
	public void runRight() {
		animate(new long[] {5,5}, 0, 1, true);
		body.setLinearVelocity(new Vector2(5,body.getLinearVelocity().y));
	}
	
	public void stopRunning() {
		body.setLinearVelocity(new Vector2(0,body.getLinearVelocity().y));
	}
	
	public void breathFire() {
		 animate(new long[] {10,10}, 2, 3, true);
	}
	
	public void setBody(Body body) {
		this.body = body;
	}

}


