package net.chozabu.gexEdit;

import com.badlogic.gdx.InputProcessor;

public class DrawingControl {
	public MyGdxGame root;
	//public float PPM;
	private SoftBody creationObject;

	float lastX, lastY;
	float firstX, firstY;
	public void setInfo (MyGdxGame rootIn){
		root=rootIn;
	}

	public boolean touchDown(float xP, float yP, int pointer, int button) {
		// TODO Auto-generated method stub
		lastX = xP;
		lastY = yP;
		firstX = xP;
		firstY = yP;
		//root.quickAddObj(xP,yP);
		creationObject = root.quickNewSoft();
		creationObject.queCircle(1, xP, yP);
		return false;
	}

	public boolean touchUp(float xP, float yP, int pointer, int button) {
		if(creationObject != null){
			creationObject.createFromDef();
		}
		// TODO Auto-generated method stub
		return false;
	}

	public boolean touchDragged(float xP, float yP, int pointer) {
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
		}
		return false;
	}

}
