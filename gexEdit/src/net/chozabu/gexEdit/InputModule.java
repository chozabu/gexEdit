package net.chozabu.gexEdit;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

public class InputModule implements InputProcessor {
	public MyGdxGame root;
	DrawingControl drawingControl;
	CamControl camControl;
	

	public enum Modes {
		Camera, Drawing, Transform, Interact, Select
	};
	
	Modes mode;
	
	
	public void setInfo (MyGdxGame rootIn){
		root=rootIn;
		mode = Modes.Drawing;
		drawingControl = new DrawingControl();
		drawingControl.setInfo(root);
		camControl = new CamControl();
		camControl.setInfo(root);
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.R)
			root.resetAll();

		if (keycode == Keys.TAB){
			root.runStep = !root.runStep;
			//root.resetAll();
			//root.render();
			//root.delPBs();
		}
		
		if (keycode == Keys.Z)
			mode = Modes.Interact;
		if (keycode == Keys.X)
			mode = Modes.Select;
		if (keycode == Keys.C)
			mode = Modes.Camera;
		if (keycode == Keys.V)
			mode = Modes.Drawing;
		//creationObject.createFromDef();
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
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Vector3 worldPos = new Vector3(screenX, screenY,0);
		root.camera.unproject(worldPos);
		float xP = worldPos.x;
		float yP = worldPos.y;
		//creationObject = new SoftBody();
		//float xP =(screenX-root.pixWidth/2)/root.PPM-root.camera.position.x;
		//float yP =(-screenY+root.pixHeight/2)/root.PPM-root.camera.position.y;

		if (mode == Modes.Drawing)
		drawingControl.touchDown(xP,yP,pointer,button);
		if (mode == Modes.Camera)
		camControl.touchDown(screenX,screenY,pointer,button);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Vector3 worldPos = new Vector3(screenX, screenY,0);
		root.camera.unproject(worldPos);
		float xP = worldPos.x;
		float yP = worldPos.y;
		// TODO Auto-generated method stub
		//float xP =(screenX-root.pixWidth/2)/root.PPM-root.camera.position.x;
		//float yP =(-screenY+root.pixHeight/2)/root.PPM-root.camera.position.y;
		if (mode == Modes.Drawing)
		drawingControl.touchUp(xP,yP,pointer,button);
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		Vector3 worldPos = new Vector3(screenX, screenY,0);
		root.camera.unproject(worldPos);
		float xP = worldPos.x;
		float yP = worldPos.y;
		//float xP =(screenX-root.pixWidth/2)/root.PPM-root.camera.position.x;
		//float yP =(-screenY+root.pixHeight/2)/root.PPM-root.camera.position.y;
		if (mode == Modes.Drawing)
		drawingControl.touchDragged(xP,yP,pointer);
		if (mode == Modes.Camera)
		camControl.touchDragged(screenX,screenY,pointer);
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		float am = amount*0.1f;
		root.camera.zoom += am;
		root.camera.update();
		//root.camWidth = root.camera.viewportWidth;
		//System.out.println(root.camWidth);
		//root.PPM = root.pixWidth/root.camWidth/root.camera.zoom;
		// TODO Auto-generated method stub
		return false;
	}

}
