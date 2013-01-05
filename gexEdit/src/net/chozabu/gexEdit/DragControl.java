package net.chozabu.gexEdit;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;

public class DragControl implements InputTool {
	MyGdxGame root;

    /** ground body to connect the mouse joint to **/
    protected Body groundBody;

    /** our mouse joint **/
    protected MouseJoint mouseJoint = null;

    /** a hit body **/
    protected Body hitBody = null;
    
	public DragControl(MyGdxGame rootIn){
		root = rootIn;
        BodyDef bodyDef = new BodyDef();
        groundBody = root.getWorld().createBody(bodyDef);
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		if(mouseJoint==null)return false;
		root.getWorld().destroyJoint(mouseJoint);
		mouseJoint = null;
		
		if (keycode == Keys.ENTER)
			disposeObject();
		if (keycode == Keys.BACKSPACE)
			disposeBody();
		return false;
	}
    public boolean disposeObject() {
    	if(hitBody == null) return false;
    	PhysObject pObj = (PhysObject)hitBody.getUserData();
    	if(root.physObject.contains(pObj)){
    		root.physObject.remove(pObj);
    		pObj.dispose();
    	}else{
    		SoftBody sb = root.getSB(pObj.body);
    		root.softBody.remove(sb);
    		sb.dispose();
    	}
    	return true;
    }
    public boolean disposeBody() {
    	if(hitBody == null) return false;
    	PhysObject pObj = (PhysObject)hitBody.getUserData();
    	World world = root.getWorld();
    	pObj.destroyBody(world);

    	if(root.physObject.contains(pObj)){
    		pObj.destroyBody(world);
    	}else{
    		SoftBody sb = root.getSB(pObj.body);
    		//root.softBody.remove(sb);
    		sb.destroyBody();
    	}
    	return true;
    }

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}
    Vector3 testPoint = new Vector3();
    QueryCallback callback = new QueryCallback() {
            @Override public boolean reportFixture (Fixture fixture) {
                    // if the hit point is inside the fixture of the body
                    // we report it
                    if (fixture.testPoint(testPoint.x, testPoint.y)) {
                            hitBody = fixture.getBody();
                            return false;
                    } else
                            return true;
            }
    };
    BodyType oldType;
    @Override public boolean touchDown (int x, int y, int pointer, int button) {
            // translate the mouse coordinates to world coordinates
            root.camera.unproject(testPoint.set(x, y, 0));
            // ask the world which bodies are within the given
            // bounding box around the mouse pointer
            hitBody = null;
            
            root.getWorld().QueryAABB(callback, testPoint.x - 0.0001f, testPoint.y - 0.0001f, testPoint.x + 0.0001f, testPoint.y + 0.0001f);
            //if (hitBody == groundBody) hitBody = null;

            // ignore kinematic bodies, they don't work with the mouse joint
            if (hitBody != null && hitBody.getType() == BodyType.KinematicBody) return false;
            
            //hitBody.getFixtureList().get(0).
            // if we hit something we create a new mouse joint
            // and attach it to the hit body.
            if (hitBody != null) {
                oldType = hitBody.getType();
                hitBody.setType(BodyType.DynamicBody);
	            MouseJointDef def = new MouseJointDef();
	            def.bodyA = groundBody;
	            def.bodyB = hitBody;
	            def.collideConnected = true;
	            def.target.set(testPoint.x, testPoint.y);
	            def.maxForce = 1000.0f * hitBody.getMass();
	
	            mouseJoint = (MouseJoint)root.getWorld().createJoint(def);
	            hitBody.setAwake(true);
            }

            return false;
    }

    /** another temporary vector **/
    Vector2 target = new Vector2(); 

    @Override public boolean touchDragged (int x, int y, int pointer) {
            // if a mouse joint exists we simply update
            // the target of the joint based on the new
            // mouse coordinates
            if (mouseJoint != null) {
                    root.camera.unproject(testPoint.set(x, y, 0));
                    mouseJoint.setTarget(target.set(testPoint.x, testPoint.y));
            }
            return false;
    }

    @Override public boolean touchUp (int x, int y, int pointer, int button) {
            // if a mouse joint exists we simply destroy it
            if (mouseJoint != null) {
                    root.getWorld().destroyJoint(mouseJoint);
                    mouseJoint = null;
                    hitBody.setType(oldType);//make static again (if static before)
                    if (root.frozen){
                    	PhysObject cpb = (PhysObject)hitBody.getUserData();
                    	SoftBody sb = root.getSB(cpb.body);
                    	if (sb!=null){
                    		sb.defFromPos();
                    	}else
                    		cpb.defFromPos();
                    }
            }
            return false;
    }

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}
