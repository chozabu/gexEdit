package net.chozabu.gexEdit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData.Region;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
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
	Shape shape;
	FixtureDef fixtureDef;
	float scale = 1;

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
		setupSprite(x,y,rad,rad, region);
		
		bodyDef = new BodyDef();
		bodyDef.type = bType;
		bodyDef.position.set(x, y);

		setShapeCircle(rad);
		setupFixture();
	}
	public void setShapePoly(float w,float h){
		if (shape!=null)shape.dispose();
		PolygonShape poly = new PolygonShape();
		poly.setAsBox(w, h);
		shape=poly;
	}
	public void setShapeCircle(float rad){
		if (shape!=null)shape.dispose();
		CircleShape circle = new CircleShape();
		circle.setRadius(rad);
		shape=circle;
	}
	public void setupFixture(){
		fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;//TODO throw error or something if null?
		fixtureDef.density = 0.5f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.6f;
	}
	public void setupSprite(float x, float y, float w,float h, TextureRegion region){		//sprite = pSprite;
		sprite = new Sprite(region, 0, 0, 64, 64);
		sprite.setSize(w*2,h*2);
		sprite.setPosition(x-sprite.getWidth()/2, y-sprite.getHeight()/2);
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
	}
	public void setDefBox(float x, float y, float w,float h, TextureRegion region, BodyType bType){		//sprite = pSprite;
		setupSprite(x,y,w,h, region);

		bodyDef = new BodyDef();
		bodyDef.type = bType;
		bodyDef.position.set(x, y);
		bodyDef.angle=0f;
		
		setShapePoly(w,h);
		setupFixture();
	}

	public void createFromDef(World world){		//sprite = pSprite;
		if (body!=null)
			world.destroyBody(body);
		body = world.createBody(bodyDef);
		//body.applyTorque(1000);
		body.setUserData(this);
		//body.getUserData();
		// Create our fixture and attach it to the body
		fixture = body.createFixture(fixtureDef);
		//sprite.setScale(1*0.5f);
	}

	public void createCircle(World world, float rad,float x, float y, TextureRegion region, BodyType bType) {
		setDefCircle(rad,x, y, region, bType);
		createFromDef(world);
	}
	public void createBox(World world, float x, float y, float w, float h, TextureRegion region, BodyType bType) {
		setDefBox(x, y, w,h, region, bType);
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
	public void setDefAngle(float angle) {
		bodyDef.angle=angle;
		sprite.setRotation(angle/MathUtils.degreesToRadians);
	}
	public void setDefSize(float dist) {
		sprite.setScale(dist*0.5f);
		scaleShape(dist,shape);
		
	}
	public void setLiveSize(float dist) {
		sprite.setScale(dist*0.5f);
		Fixture f = body.getFixtureList().get(0);
		Shape s = f.getShape();
		scaleShape(dist,s);
		
	}
	void scaleShape(float dist, Shape shapeIn){
		//sprite.setScale(dist*0.5f);
		Shape.Type sType = shapeIn.getType();
		if (sType == Shape.Type.Circle){
			CircleShape cs = (CircleShape)shapeIn;
			cs.setRadius(dist);
		}else if (sType == Shape.Type.Polygon){
			PolygonShape ps = (PolygonShape)shapeIn;
			ps.setAsBox(dist, dist);
			/*//TODO use something like this to scale any poly :) (box only has one vertex?)
			 * int vCount = ps.getChildCount();
			Vector2[] verts = new Vector2[vCount];
			Vector2 cVert = Vector2.Zero;
			for (int i = 0; i < vCount;i++){
				ps.getVertex(i, cVert);
				cVert.x*=1.1;//TODO dont use 1.1 here!
				verts[i]=cVert;
			}
			System.out.println(vCount);
			ps.set(verts);*/
		}
			
	}
		
}
