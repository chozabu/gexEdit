package net.chozabu.gexEdit;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData.Region;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

public class PhysObject {
	//body
	//fixture
	Fixture fixture;
	Body body;
	private Sprite sprite;
	BodyDef bodyDef;
	FixtureDef fixtureDef;

	public void draw(SpriteBatch batch) {
		if (body == null) {
			sprite.draw(batch);
			//System.out.println("no body");
			return;
		}
        Transform transform = body.getTransform();
        sprite.setPosition(transform.getPosition().x-sprite.getWidth()/2, transform.getPosition().y-sprite.getHeight()/2);
        //System.out.println(transform.getRotation()*MathUtils.radiansToDegrees);
        sprite.setRotation(transform.getRotation()*MathUtils.radiansToDegrees);
        sprite.draw(batch);
        //sprite.setPosition(transform.getPosition().x-sprite.getWidth()/2+5, transform.getPosition().y-sprite.getHeight()/2);
        //sprite.draw(batch);
	}
	public void setDefCircle(float rad,float x, float y, TextureRegion region, BodyType bType){		//sprite = pSprite;
		sprite = new Sprite(region, 0, 0, 64, 64);
		//sprite.setPosition(-sprite.getWidth()/2, -sprite.getHeight()/2);
		//sprite.setPosition(x,y);
		sprite.setSize(rad*2,rad*2);
		sprite.setPosition(x-sprite.getWidth()/2, y-sprite.getHeight()/2);
		//sprite.setPosition(x,y);
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
		// First we create a body definition
		bodyDef = new BodyDef();
		// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
		bodyDef.type = bType;
		// Set our body's starting position in the world
		bodyDef.position.set(x, y);
		
		// Create a circle shape and set its radius to 6
		CircleShape circle = new CircleShape();
		circle.setRadius(rad);

		// Create a fixture definition to apply our shape to
		fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.5f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.6f; // Make it bounce a little bit
	}
	public void setDefSquare(float x, float y, float w,float h, TextureRegion region, BodyType bType){		//sprite = pSprite;
		sprite = new Sprite(region, 0, 0, 64, 64);
		//sprite.setPosition(-sprite.getWidth()/2, -sprite.getHeight()/2);
		//sprite.setPosition(x,y);
		sprite.setSize(w,h);
		sprite.setPosition(x-sprite.getWidth()/2, y-sprite.getHeight()/2);
		//sprite.setPosition(x,y);
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
		// First we create a body definition
		bodyDef = new BodyDef();
		// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
		bodyDef.type = bType;
		// Set our body's starting position in the world
		bodyDef.position.set(x, y);
		
		// Create a circle shape and set its radius to 6
		PolygonShape poly = new PolygonShape();
		poly.setAsBox(w, h);

		// Create a fixture definition to apply our shape to
		fixtureDef = new FixtureDef();
		fixtureDef.shape = poly;
		fixtureDef.density = 0.5f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.6f; // Make it bounce a little bit
	}

	public void createFromDef(World world){		//sprite = pSprite;
		if (body!=null)
			world.destroyBody(body);
		body = world.createBody(bodyDef);
		//body.applyTorque(1000);
		//body.setUserData("asd");
		//body.getUserData();
		// Create our fixture and attach it to the body
		fixture = body.createFixture(fixtureDef);
	}

	public void createCircle(World world, float rad,float x, float y, TextureRegion region, BodyType bType) {
		setDefCircle(rad,x, y, region, bType);
		createFromDef(world);
	}
	public void createSquare(World world, float x, float y, float w, float h, TextureRegion region, BodyType bType) {
		setDefSquare(x, y, w,h, region, bType);
		createFromDef(world);
	}
	public void reset(World world){
		//world.destroyBody(body);
		createFromDef(world);
	}
	public void destroyBody(World world){
		world.destroyBody(body);
		body = null;
	}
	public void rmBody(World world){
		world.destroyBody(body);
	}
}
