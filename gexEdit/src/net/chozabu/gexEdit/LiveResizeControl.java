package net.chozabu.gexEdit;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;

public class LiveResizeControl implements InputTool {
	MyGdxGame root;

	float lastX, lastY;

    /** a hit body **/
    protected Body hitBody = null;
    
    @Override
    public void update(){
    	
    }
    
	public LiveResizeControl(MyGdxGame rootIn){
		root = rootIn;
        //BodyDef bodyDef = new BodyDef();
        //groundBody = root.getWorld().createBody(bodyDef);
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
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
    PhysObject resMe;
    @Override public boolean touchDown (int x, int y, int pointer, int button) {
            // translate the mouse coordinates to world coordinates
            root.camera.unproject(testPoint.set(x, y, 0));
            // ask the world which bodies are within the given
            // bounding box around the mouse pointer
            hitBody = null;
            
            root.getWorld().QueryAABB(callback, testPoint.x - 0.0001f, testPoint.y - 0.0001f, testPoint.x + 0.0001f, testPoint.y + 0.0001f);
            System.out.println(hitBody);
            //if (hitBody == groundBody) hitBody = null;

            // ignore kinematic bodies, they don't work with the mouse joint
            if (hitBody != null && hitBody.getType() == BodyType.KinematicBody) return false;

            // if we hit something we create a new mouse joint
            // and attach it to the hit body.
            if (hitBody != null) {
                    /*MouseJointDef def = new MouseJointDef();
                    def.bodyA = groundBody;
                    def.bodyB = hitBody;
                    def.collideConnected = true;
                    def.target.set(testPoint.x, testPoint.y);
                    def.maxForce = 1000.0f * hitBody.getMass();

                    mouseJoint = (MouseJoint)root.getWorld().createJoint(def);*/
                    hitBody.setAwake(true);
                    resMe = (PhysObject)hitBody.getUserData();
            }

            return false;
    }

    /** another temporary vector **/
    Vector2 target = new Vector2(); 
    float minObjSize = 1;
    @Override public boolean touchDragged (int x, int y, int pointer) {
    		if (resMe == null)return false;
            // if a mouse joint exists we simply update
            // the target of the joint based on the new
            // mouse coordinates
            /*if (mouseJoint != null) {
                    root.camera.unproject(testPoint.set(x, y, 0));
                    mouseJoint.setTarget(target.set(testPoint.x, testPoint.y));
            }*/
			Vector3 worldPos = new Vector3(x, y,0);
			root.camera.unproject(worldPos);
			Vector3 diff = worldPos.cpy().sub(testPoint);
			float dist = diff.len();
			if (dist > minObjSize){
				resMe.setLiveSize(dist);
				//hitBody.setDefSize(dist);
	
				//pObj.setDefAngle((float)Math.atan2(diff.y, diff.x));
				//pObj.setDefAngle((float)Math.atan2(diff.y, diff.x));
			}
            return false;
    }

    @Override public boolean touchUp (int x, int y, int pointer, int button) {
    		if(root.frozen)
    			resMe.defFromScale();
    			
            // if a mouse joint exists we simply destroy it
            /*if (mouseJoint != null) {
                    root.getWorld().destroyJoint(mouseJoint);
                    mouseJoint = null;
            }*/
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

}
