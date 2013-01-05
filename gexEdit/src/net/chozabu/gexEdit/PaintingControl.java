package net.chozabu.gexEdit;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class PaintingControl implements InputTool{
	public MyGdxGame root;
	//public float PPM;
	//private SoftBody creationObject;
	public PaintingControl (MyGdxGame rootIn){
		root=rootIn;
	}
	
	void addCircle(Vector2 pos, float radius){
		//loop through SBs, find first touching = cSB
		//count intersections - should be multiple of 2
	}

    @Override
    public void update(){
    	
    }

    @Override
	public boolean touchDown(int xI, int yI, int pointer, int button) {
		/*Vector3 testPoint = new Vector3(xI,yI,0);
		root.camera.unproject(testPoint);
		float xP=testPoint.x;
		float yP=testPoint.y;
		lastX = xP;
		lastY = yP;
		firstX = xP;
		firstY = yP;
		//root.quickAddObj(xP,yP);
		creationObject = root.quickNewSoft();
		creationObject.queCircle(1, xP, yP);*/
		return false;
	}

    @Override
	public boolean touchUp(int xI, int yI, int pointer, int button) {
		/*Vector3 testPoint = new Vector3(xI,yI,0);
		root.camera.unproject(testPoint);
		float xP=testPoint.x;
		float yP=testPoint.y;
		if(creationObject != null){
			creationObject.createFromDef();
		}*/
		return false;
	}

    @Override
	public boolean touchDragged(int xI, int yI, int pointer) {
		/*Vector3 testPoint = new Vector3(xI,yI,0);
		root.camera.unproject(testPoint);
		float xP=testPoint.x;
		float yP=testPoint.y;
		if (creationObject == null) return false;
		creationObject.condAddAt(xP,yP);
		float xd=xP-firstX;
		float yd=yP-firstY;
		if (xd*xd+yd*yd<2 && creationObject.physObject.size()>2 ){
			creationObject.makeLoop();
			creationObject.createFromDef();
			creationObject = null;
			lastX = xP;
			lastY = yP;
		}*/
		return false;
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
