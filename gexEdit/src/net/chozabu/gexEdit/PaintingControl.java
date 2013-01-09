package net.chozabu.gexEdit;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class PaintingControl implements InputTool{
	
	public MyGdxGame root;
	public DeformableMesh deformableMesh;
	//public float PPM;
	//private SoftBody creationObject;
	

	private Sprite sprite;

	public void setupSprite(float w,float h, TextureRegion region){		//sprite = pSprite;
	}
	
	public PaintingControl (MyGdxGame rootIn){
		root=rootIn;
		deformableMesh = new DeformableMesh();
		setupSprite(0.4f, 0.4f, root.region);
	}
	
	public List<Vector2> tcAT(float x, float y){
		List<Vector2> testCircle = new ArrayList<Vector2>();
		float sAngle = 0;//MathUtils.random()*MathUtils.PI*2;
		float radius = 2f;//1.f+MathUtils.random();
		for(float angle=sAngle;angle<MathUtils.PI*2+sAngle;angle+=MathUtils.PI/2f){
			float xp = MathUtils.cos(angle)*radius+x;
			float yp = MathUtils.sin(angle)*radius+y;
			testCircle.add(new Vector2(xp,yp));
			//System.out.println("TCP = "+new Vector2(xp,yp));
		}
		return testCircle;
	}
	
	void addCircle(Vector2 pos, float radius){
		//loop through SBs, find first touching = cSB
		//count intersections - should be multiple of 2
	}

    @Override
    public void update(){
    	
    }

    @Override
	public boolean touchDown(int x, int y, int pointer, int button) {
    	Vector3 testPoint = Vector3.Zero;
    	root.camera.unproject(testPoint.set(x, y, 0));
    	deformableMesh.addLoop(tcAT(testPoint.x, testPoint.y));
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
	public boolean touchDragged(int x, int y, int pointer) {
    	Vector3 testPoint = Vector3.Zero;
    	root.camera.unproject(testPoint.set(x, y, 0));
    	deformableMesh.addLoop(tcAT(testPoint.x, testPoint.y));
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
