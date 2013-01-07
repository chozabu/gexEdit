package net.chozabu.gexEdit;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector3;

public class BoxAddingControl implements InputTool {
	MyGdxGame root;

	public enum Modes {
		Circle,Box,TestPoly
	};
	Modes mode = Modes.Circle;
	public BoxAddingControl(MyGdxGame rootIn){
		root = rootIn;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.NUM_1)
			mode = Modes.Circle;
		if (keycode == Keys.NUM_2)
			mode = Modes.Box;
		if (keycode == Keys.NUM_3)
			mode = Modes.TestPoly;
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
	PhysObject pObj;
	Vector3 touchStart;
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Vector3 worldPos = new Vector3(screenX, screenY,0);
		root.camera.unproject(worldPos);
		touchStart = worldPos.cpy();
        pObj = new PhysObject();
        if (mode == Modes.Box)
        	pObj.setDefBox( worldPos.x,worldPos.y,2,2, root.regionBox,root.bType);
        else if (mode == Modes.Circle)
        	pObj.setDefCircle(2, worldPos.x,worldPos.y, root.region,root.bType);
        else
        	pObj.setDefCircle(2, worldPos.x,worldPos.y, root.regionBox,root.bType);
        root.physObject.add(pObj);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		pObj.createFromDef(root.getWorld());
		return false;
	}
	float minObjSize = 1;
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		Vector3 worldPos = new Vector3(screenX, screenY,0);
		root.camera.unproject(worldPos);
		Vector3 diff = worldPos.cpy().sub(touchStart);
		float dist = diff.len();
		if (dist > minObjSize){
			pObj.setDefSize(dist);

			pObj.setDefAngle((float)Math.atan2(diff.y, diff.x));
			//pObj.setDefAngle((float)Math.atan2(diff.y, diff.x));
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
