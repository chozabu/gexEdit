package net.chozabu.gexEdit;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class InputModule implements InputProcessor {
	public MyGdxGame root;
	DrawingControl drawingControl;
	CamControl camControl;
	BoxAddingControl boxAddingControl;
	DragControl dragControl;
	LiveResizeControl liveResizeControl;
	

	public enum Modes {
		Camera, Drawing, Transform, Interact, Box,Circle,Resize//Select
	};
	
	Modes mode;
	
	
	public void setInfo (MyGdxGame rootIn){
		root=rootIn;
		mode = Modes.Interact;
		drawingControl = new DrawingControl();
		drawingControl.setInfo(root);
		camControl = new CamControl();
		camControl.setInfo(root);
		boxAddingControl = new BoxAddingControl(root);
		dragControl = new DragControl(root);
		liveResizeControl = new LiveResizeControl(root);
	}

	@Override
	public boolean keyDown(int keycode) {
		

		if (mode == Modes.Box)
			boxAddingControl.keyDown(keycode);
		if (mode == Modes.Interact)
			dragControl.keyDown(keycode);
		
		if (keycode == Keys.R)
			root.resetAll();

		if (keycode == Keys.TAB){
			root.runStep = !root.runStep;
			//root.resetAll();
			//root.render();
			//root.delPBs();
		}

		if (keycode == Keys.S)
			root.bType = BodyType.StaticBody;
		if (keycode == Keys.D)
			root.bType = BodyType.DynamicBody;

		//set mode (drawing tool)//TODO make "tool" interface to set currentTool
		if (keycode == Keys.Z)
			mode = Modes.Interact;//running, transform when static?
		if (keycode == Keys.X)
			mode = Modes.Interact;
			//mode = Modes.Select;
		if (keycode == Keys.C)
			mode = Modes.Camera;//either
		if (keycode == Keys.V)
			mode = Modes.Drawing;//static, mostly
		if (keycode == Keys.B)
			mode = Modes.Box;//static, mostly
		if (keycode == Keys.N)
			mode = Modes.Resize;
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

		if (mode == Modes.Drawing)
			drawingControl.touchDown(xP,yP,pointer,button);
		if (mode == Modes.Camera)
			camControl.touchDown(screenX,screenY,pointer,button);
		if (mode == Modes.Box)
			boxAddingControl.touchDown(screenX,screenY,pointer,button);
		if (mode == Modes.Interact){
			dragControl.touchDown(screenX,screenY,pointer,button);
		}
		if (mode == Modes.Resize)
			liveResizeControl.touchDown(screenX,screenY,pointer,button);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Vector3 worldPos = new Vector3(screenX, screenY,0);
		root.camera.unproject(worldPos);
		float xP = worldPos.x;
		float yP = worldPos.y;
		if (mode == Modes.Drawing)
		drawingControl.touchUp(xP,yP,pointer,button);
		if (mode == Modes.Box)
		boxAddingControl.touchUp(screenX,screenY,pointer, button);
		if (mode == Modes.Interact){
			dragControl.touchUp(screenX,screenY,pointer, button);
		}
		if (mode == Modes.Resize)
			liveResizeControl.touchUp(screenX,screenY,pointer,button);
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		Vector3 worldPos = new Vector3(screenX, screenY,0);
		root.camera.unproject(worldPos);
		float xP = worldPos.x;
		float yP = worldPos.y;
		if (mode == Modes.Drawing)
		drawingControl.touchDragged(xP,yP,pointer);
		if (mode == Modes.Camera)
		camControl.touchDragged(screenX,screenY,pointer);
		if (mode == Modes.Box)
		boxAddingControl.touchDragged(screenX,screenY,pointer);
		if (mode == Modes.Interact){
			dragControl.touchDragged(screenX,screenY,pointer);
		}
		if (mode == Modes.Resize)
			liveResizeControl.touchDragged(screenX,screenY,pointer);
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
