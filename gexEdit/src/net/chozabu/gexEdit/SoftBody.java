package net.chozabu.gexEdit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;

public class SoftBody {
	static float minDis;
	List<PhysObject> physObject;
	World world;
	TextureRegion region;
	boolean isLoop;

	public void draw(SpriteBatch batch) {
        Iterator<PhysObject> it=physObject.iterator();
        while(it.hasNext())
        {
        	PhysObject cObj=(PhysObject)it.next();
        	cObj.draw(batch);
          //System.out.println("Value :"+value);
        }
		/*for (Iterator<Body> iter = body; iter.hasNext();) {
            Body body = iter.next();
            Transform transform = body.getTransform();
            sprite.setPosition(transform.getPosition().x-sprite.getWidth()/2, transform.getPosition().y-sprite.getHeight()/2);
            sprite.setRotation(transform.getRotation()*MathUtils.radiansToDegrees);
        }*/
        //Transform transform = body.getTransform();
        //sprite.setPosition(transform.getPosition().x-sprite.getWidth()/2, transform.getPosition().y-sprite.getHeight()/2);
        //System.out.println(transform.getRotation()*MathUtils.radiansToDegrees);
	}
	public void setup(World pworld, TextureRegion pregion){
		region = pregion;
		world = pworld;
		physObject=new ArrayList<PhysObject>();
		isLoop = false;
	}
	
	private float lastX, lastY;
	
	public void condAddAt(float xP,float yP){
		float hardRad = 2.f;
		float xd=xP-lastX;
		float yd=yP-lastY;
		float disSqr = xd*xd+yd*yd;
		if (disSqr>2*2){
			float dis = (float)Math.sqrt((double)disSqr);
			xd/=dis;
			yd/=dis;
			xP=lastX+xd*hardRad;
			yP=lastY+yd*hardRad;
		}
		if (disSqr>hardRad*hardRad){//TODO unhardcode this
			queCircle(1, xP, yP);
		}
	}
	
	public void queCircle(float rad,float x, float y) {
        PhysObject pObj = new PhysObject();
        pObj.setDefCircle(1.f,x,y, region,BodyType.DynamicBody);
        physObject.add(pObj);
		lastX = x;
		lastY = y;
        
	}
	
	public void joinCentre(Body ba, Body bb){
		
	}

	public void reset(){
		createFromDef();
	}
	public void destroyBody(){
        Iterator<PhysObject> it=physObject.iterator();
        Vector2 avgPos = new Vector2(0,0);
        while(it.hasNext())
        {
        	PhysObject cObj=(PhysObject)it.next();
        	cObj.destroyBody(world);
        }
	}
	public void createFromDef(){
        Iterator<PhysObject> it=physObject.iterator();
        Vector2 avgPos = new Vector2(0,0);
        while(it.hasNext())
        {
        	PhysObject cObj=(PhysObject)it.next();
        	cObj.createFromDef(world);
        	avgPos.add(cObj.body.getPosition());
        	//if (cObj.body == null)
        }
        avgPos.div(physObject.size());
        
        PhysObject lastObj = null;
        //physObject.get(physObject.size()-1);
        //System.out.println(lastObj);
        PhysObject lastObj2 = null;//physObject.get(physObject.size()-2);
        if (isLoop){
        	lastObj = physObject.get(physObject.size()-1);
        	lastObj2 = physObject.get(physObject.size()-2);
        }
        //lastObj2.createFromDef(world);
        it=physObject.iterator();
        int index = 0; int olen = physObject.size();
        while(it.hasNext())
        {
        	PhysObject cObj=(PhysObject)it.next();
        	//if (cObj.body == null)
        	//cObj.createFromDef(world);
        	if (lastObj!=null){
        		//DistanceJointDef mjd = new DistanceJointDef();
        		WeldJointDef mjd = new WeldJointDef();
        		mjd.initialize(cObj.body, lastObj.body, cObj.body.getPosition());//,lastObj.body.getPosition());
        		//mjd.initialize(cObj.body, lastObj.body, cObj.body.getPosition(),lastObj.body.getPosition());
        		//mjd.dampingRatio=0.f;
        		//mjd.frequencyHz=5f;
        		//world.createJoint(mjd);
        		//mjd.initialize(cObj.body, lastObj.body, cObj.body.getPosition());//,lastObj.body.getPosition());
        		world.createJoint(mjd);
        		
        		
        		
        		PhysObject poR = physObject.get((index+olen/2)%olen);
        		DistanceJointDef mjdR = new DistanceJointDef();
        		//PrismaticJointDef mjdR = new PrismaticJointDef();
        		mjdR.initialize(cObj.body, poR.body, cObj.body.getPosition(),poR.body.getPosition());
        		mjdR.dampingRatio=0.5f;
        		mjdR.frequencyHz=5;
        		//Vector2 axis=cObj.body.getPosition().cpy().sub(poR.body.getPosition());
        		//mjdR.initialize(cObj.body, poR.body, cObj.body.getPosition(),axis.nor());
        		world.createJoint(mjdR);
            	if (lastObj2!=null){
            		DistanceJointDef mjd2 = new DistanceJointDef();
            		mjd2.initialize(cObj.body, lastObj2.body, cObj.body.getPosition(),lastObj2.body.getPosition());
            		mjd2.dampingRatio=0.5f;
            		mjd2.frequencyHz=5f;
            		DistanceJoint tj = (DistanceJoint)world.createJoint(mjd2);
            		//tj.setLength(tj.getLength()*0.9f);
            	}
        	}
        	lastObj2 = lastObj;
        	lastObj = cObj;
        	
          //System.out.println("Value :"+value);
        	index++;
        }
		
	}
	public void makeLoop() {
		isLoop = true;
		
	}

}
