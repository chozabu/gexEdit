package net.chozabu.gexEdit;

import java.util.List;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class InputModule implements InputTool {
	public MyGdxGame root;
	DrawingControl drawingControl;
	PaintingControl paintingControl;
	CamControl camControl;
	BoxAddingControl boxAddingControl;
	DragControl dragControl;
	LiveResizeControl liveResizeControl;
	List<InputTool> tools;
	InputTool cTool;
	
	boolean button1Down = false;
	
	
	public void setInfo (MyGdxGame rootIn){
		root=rootIn;
		//mode = Modes.Interact;
		drawingControl = new DrawingControl(root);
		paintingControl = new PaintingControl(root);
		camControl = new CamControl(root);
		boxAddingControl = new BoxAddingControl(root);
		dragControl = new DragControl(root);
		liveResizeControl = new LiveResizeControl(root);
		//cTool = paintingControl;
		cTool = drawingControl;
	}

	@Override
	public boolean keyDown(int keycode) {
		

		if (cTool == boxAddingControl)
			boxAddingControl.keyDown(keycode);
		if (cTool == dragControl)
			dragControl.keyDown(keycode);
		
		if (keycode == Keys.R){
			root.resetAll();
			root.frozen = true;
		}

		if (keycode == Keys.TAB){
			if (root.frozen)
				root.frozen = false;
			else
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
		if (keycode == Keys.Z){
			cTool = dragControl;
			//mode = Modes.Interact;//running, transform when static?
		}
		if (keycode == Keys.X){
			cTool = paintingControl;
			//mode = Modes.Painting;
		}
			//mode = Modes.Select;
		if (keycode == Keys.C){
			cTool = camControl;
			//mode = Modes.Camera;//either
		}
		if (keycode == Keys.V){
			cTool = drawingControl;
			//mode = Modes.Drawing;//static, mostly
		}
		if (keycode == Keys.B){
			cTool = boxAddingControl;
			//mode = Modes.Box;//static, mostly
		}
		if (keycode == Keys.N){
			cTool = liveResizeControl;
			//mode = Modes.Resize;
		}
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
		if (button == 1){
			camControl.touchDown(screenX,screenY,pointer,button);
			button1Down = true;
	}
		else
			cTool.touchDown(screenX,screenY,pointer,button);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == 1){
			camControl.touchUp(screenX,screenY,pointer,button);
			button1Down = false;
		}
		else
			cTool.touchUp(screenX,screenY,pointer, button);
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (button1Down)
			camControl.touchDragged(screenX,screenY,pointer);
		else
			cTool.touchDragged(screenX,screenY,pointer);
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		camControl.scrolled(amount);
		return false;
	}

	@Override
	public void update() {
		cTool.update();
	}

}
