package net.chozabu.gexEdit;


public class CamControl implements InputTool  {
	public MyGdxGame root;
	float lastX, lastY;
	
	public CamControl (MyGdxGame rootIn){
		root=rootIn;
	}

	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		//float xP =(screenX-root.pixWidth/2)/root.PPM-root.camera.position.x;
		//float yP =(-screenY+root.pixHeight/2)/root.PPM-root.camera.position.y;
		lastX =(screenX-root.pixWidth/2)/root.PPM*root.camera.zoom;
		lastY =(-screenY+root.pixHeight/2)/root.PPM*root.camera.zoom;
		// TODO Auto-generated method stub
		return false;
	}

	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		//float xP =(screenX-root.pixWidth/2)/root.PPM-root.camera.position.x;
		//float yP =(-screenY+root.pixHeight/2)/root.PPM-root.camera.position.y;
		// TODO Auto-generated method stub
		return false;
	}

	public boolean touchDragged(int screenX, int screenY, int pointer) {
		float xP =(screenX-root.pixWidth/2)/root.PPM*root.camera.zoom;
		float yP =(-screenY+root.pixHeight/2)/root.PPM*root.camera.zoom;
		float nx = xP-lastX;
		float ny = yP-lastY;
		root.camera.translate(-nx,-ny);
		root.camera.update();
		lastX=xP;
		lastY=yP;
		return false;
	}

	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean scrolled(int amount) {
		/*float am = amount*0.1f;
		root.camera.zoom += am;
		root.camera.update();
		root.PPM = root.pixWidth/root.camWidth/root.camera.zoom;
		return false;*/
		return false;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}
