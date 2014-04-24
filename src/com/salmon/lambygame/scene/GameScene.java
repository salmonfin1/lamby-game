package com.salmon.lambygame.scene;

import java.io.IOException;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andlabs.andengine.extension.physicsloader.PhysicsEditorLoader;
import org.andlabs.andengine.extension.physicsloader.PhysicsEditorLoader.BodyChangedListener;

import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.salmon.lambygame.GameActivity;
import com.salmon.lambygame.base.BaseScene;
import com.salmon.lambygame.manager.SceneManager;
import com.salmon.lambygame.manager.SceneManager.SceneType;
import com.salmon.lambygame.object.Player;



public class GameScene extends BaseScene implements IOnSceneTouchListener, BodyChangedListener {

	private HUD gameHUD;
	private Text scoreText;
	private int score = 0;
	private PhysicsWorld physicsWorld;
	
	private static final String TAG_ENTITY = "entity";
	private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
	private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";
	    
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM1 = "platform1";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM2 = "platform2";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM3 = "platform3";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_HILLS = "hills";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COIN = "coin";
	
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER = "player";
	private Sprite hillSprite;
	private Player player;
    
    private Text gameOverText;
    private boolean gameOverDisplayed = false;
    private boolean isTouched = false;
    private String buttonPressed = null;
	
	@Override
	public void createScene() {
		createBackground();
		createHUD();
		createPhysics();
		loadLevel(1);
		createGameOverText();
		createControllers();
		setOnSceneTouchListener(this);
		
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadMenuScene(engine);
		
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene() {
		camera.setHUD(null);
		camera.setCenter(400, 240);
		camera.setChaseEntity(null);
	}
	
	private void createBackground() {
		setBackground(new Background(Color.BLUE));
	}
	
	private void createHUD() {
		gameHUD = new HUD();
		scoreText = new Text(20, 420, resourcesManager.font, "Score 0123456789", new TextOptions(HorizontalAlign.LEFT),vbom);
		scoreText.setText("Score: 0");
		gameHUD.attachChild(scoreText);
		camera.setHUD(gameHUD);
	}
	
	private void addToScore(int i) {
		score += i;
		scoreText.setText("Score: " + score);
	}
	
	private void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, SensorManager.GRAVITY_EARTH), false);
//		physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);
	}
	
	private void loadLevel(int levelID)	{
	    camera.setBounds(0, 0, 1024, 1024);
	    camera.setBoundsEnabled(true);
	    hillSprite = new Sprite(0,300 , resourcesManager.hill_region, vbom);
	    
	    this.attachChild(hillSprite);
	    PhysicsEditorLoader loader = new PhysicsEditorLoader();
	    loader.setAssetBasePath("xml/");
        	try {
        		loader.load(GameActivity.context, physicsWorld, "level1.xml", hillSprite, false, false);
        		player = new Player(60,100,vbom,camera,physicsWorld) {
            		@Override
            		public void onDie() {
            			if(!gameOverDisplayed) {
            				displayGameOverText();
            			}
            		}
            	};
            	this.attachChild(player);
            	loader.reset();
            	loader.load(GameActivity.context, physicsWorld, "lamb.xml", player, true, false);
            	player.setBody(loader.getBody("player"));
            	
        	} catch (IOException e) { 
        		e.printStackTrace();
        	}
    }

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		return false;
	}
	
	private void createGameOverText() {
		gameOverText = new Text(0, 0, resourcesManager.font, "Game Over!", vbom);
	}
	
	private void displayGameOverText() {
		camera.setChaseEntity(null);
		gameOverText.setPosition(camera.getCenterX(),camera.getCenterY());
		attachChild(gameOverText);
		gameOverDisplayed = true;
	}
	
//	private ContactListener contactListener() {
//	    ContactListener contactListener = new ContactListener() {
//	        public void beginContact(Contact contact) {
//	            final Fixture x1 = contact.getFixtureA();
//	            final Fixture x2 = contact.getFixtureB();
//
//	            if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null) {
//	                if (x2.getBody().getUserData().equals("player")) {
//	                    player.increaseFootContacts();
//	                }
//	            }
//	        }
//
//	        public void endContact(Contact contact) {
//	            final Fixture x1 = contact.getFixtureA();
//	            final Fixture x2 = contact.getFixtureB();
//
//	            if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null) {
//	                if (x2.getBody().getUserData().equals("player")) {
//	                    player.decreaseFootContacts();
//	                }
//	            }
//	        }
//
//	        public void preSolve(Contact contact, Manifold oldManifold) {
//
//	        }
//
//	        public void postSolve(Contact contact, ContactImpulse impulse) {
//
//	        }
//	    };
//	    return contactListener;
//	}
	
	private void createControllers() {
		final ButtonSprite breathFire = new ButtonSprite(20, 60, resourcesManager.buttons.getTextureRegion(0), resourcesManager.buttons.getTextureRegion(1), this.vbom, new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,	float pTouchAreaLocalY) {}
		}) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				buttonPressed = "breathFire";
				if (pSceneTouchEvent.isActionDown()) {
					isTouched = true;
				} 
				if (pSceneTouchEvent.isActionUp()) {
					isTouched = false;
				}
				return true;
			}
		};
		this.registerTouchArea(breathFire);
		breathFire.setPosition(10, 300);
		gameHUD.attachChild(breathFire);
		final ButtonSprite jump = new ButtonSprite(20,60, resourcesManager.buttons.getTextureRegion(0), resourcesManager.buttons.getTextureRegion(1), this.vbom, new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				player.jump();
			}
		});
		this.registerTouchArea(jump);
		jump.setPosition(300,300);
		gameHUD.attachChild(jump);
		final ButtonSprite right = new ButtonSprite(20, 60, resourcesManager.buttons.getTextureRegion(0), resourcesManager.buttons.getTextureRegion(1), this.vbom, new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				
			}
		}) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				buttonPressed = "right";
				if (pSceneTouchEvent.isActionDown()) {
					isTouched = true;
				} 
				if (pSceneTouchEvent.isActionUp()) {
					isTouched = false;
				}
				return true;
			}
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				if (isTouched) {
					if("right".equals(buttonPressed)) {
						player.runRight();
					} else if("breathFire".equals(buttonPressed)) {
						player.breathFire();
					}
				} else {
					player.stopRunning();
					player.stopAnimation(0);
				}
				super.onManagedUpdate(pSecondsElapsed);
			}
		};
		this.registerTouchArea(right);
		right.setPosition(600, 300);
		gameHUD.attachChild(right);
		camera.setHUD(gameHUD);
		
	}

	@Override
	public IAreaShape onBodyChanged(String pBodyName) {
		if (pBodyName.equals("multi_fixture_asset")) {
			return hillSprite;
		} else if (pBodyName.equals("player")) {
			return player;
		}
		return null;
	}
}
