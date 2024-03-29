package net.chozabu.gexEdit;

//import java.io.Console;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class MyGdxGame implements ApplicationListener {
	public OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture texture, textureBox;
    TextureRegion region, regionBox;//default texture region
    private World mWorld;
    
    boolean runStep = true;
    boolean frozen = true;
    float gravity = -9.81f;
    
    InputModule inputModule;
    public float PPM;//pixels per meter

    private Box2DDebugRenderer debugRenderer;
    //PhysObject[] physObject;
    public float camWidth;
    public float camHeight;
    public int pixWidth;
    public int pixHeight;
    AssetManager assetManager;
    
    List<PhysObject> physObject;
    List<SoftBody> softBody;
    
    public BodyType bType = BodyType.DynamicBody;
	private Sprite sprite;
    
	
	/* (non-Javadoc)
	 * @see com.badlogic.gdx.ApplicationListener#create()
	 */
	@Override
	public void create() {	
		pixWidth = Gdx.graphics.getWidth();
		pixHeight = Gdx.graphics.getHeight();

		physObject=new ArrayList<PhysObject>();
		softBody=new ArrayList<SoftBody>();
		
		debugRenderer = new Box2DDebugRenderer();
		//assetManager = new AssetManager();
        mWorld = new World(new Vector2(0, gravity), true);
		//camera = new OrthographicCamera(1, h/w);
        camWidth = 50;
        PPM = pixWidth/camWidth;
        camHeight = (int) (50*pixHeight/pixWidth);
		camera = new OrthographicCamera(camWidth, camHeight);
		//camera.zoom=10.1f;
		//camera.update();
		batch = new SpriteBatch();
		
		//
		textureBox = new Texture(Gdx.files.internal("data/wall.png"));
		textureBox.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        texture = new Texture(Gdx.files.internal("data/ball.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		region = new TextureRegion(texture, 0, 0, 64, 64);
		regionBox = new TextureRegion(textureBox, 0, 0, 64, 64);


		int bRad = 2;

        PhysObject pObj = new PhysObject();
        pObj.createCircle(mWorld, bRad, 0,0, region,BodyType.DynamicBody);
        physObject.add(pObj);
        
        inputModule = new InputModule();
        inputModule.setInfo(this);
        Gdx.input.setInputProcessor(inputModule);
        
        
        PhysObject mObj = new PhysObject();
        mObj.createBox(mWorld,0.f,-camHeight*2,camWidth*10,1.f, region,BodyType.StaticBody);
            physObject.add(mObj);
        
       
        
        //make a floor of squares
        int fc = -50;
        while (fc<50){
        	fc+=8;
            PhysObject sObj = new PhysObject();
            sObj.createBox(mWorld, fc,-camHeight/2f,4,4, regionBox,BodyType.StaticBody);
            physObject.add(sObj);
        }
        

		sprite = new Sprite(region, 0, 0, 64, 64);
		sprite.setSize(1,1);
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
        
        
        /*/try out a random chain shape
		ChainShape chainShape = new ChainShape();
		chainShape.createLoop(new Vector2[] {new Vector2(-10, 10), new Vector2(-10, 5), new Vector2(10, 5), new Vector2(10, 11),});
		BodyDef chainBodyDef = new BodyDef();
		chainBodyDef.type = BodyType.StaticBody;
		Body chainBody = mWorld.createBody(chainBodyDef);
		chainBody.createFixture(chainShape, 0);
		chainShape.dispose();//*/


        
	}
	public World getWorld(){
		return mWorld;
	}
	public SoftBody quickNewSoft(){
		SoftBody newsoft = new SoftBody();
		newsoft.setup(mWorld, region);
		softBody.add(newsoft);
		return newsoft;
	}
	public void quickAddObj(float x, float y){
        PhysObject pObj = new PhysObject();
        pObj.createCircle(mWorld, 1.f,x,y, region,BodyType.DynamicBody);
        physObject.add(pObj);
	}
	public void resetAll(){
        Iterator<PhysObject> it=physObject.iterator();
        while(it.hasNext())
        {
        	PhysObject cObj=(PhysObject)it.next();
        	cObj.reset(mWorld);
          //System.out.println("Value :"+value);
        }
        Iterator<SoftBody> softit=softBody.iterator();
        while(softit.hasNext())
        {
        	SoftBody cObj=(SoftBody)softit.next();
        	cObj.reset();
        }
	}
	public SoftBody getSB(Body checkMe){
        Iterator<SoftBody> softit=softBody.iterator();
        while(softit.hasNext())
        {
        	SoftBody cObj=(SoftBody)softit.next();
        	if(cObj.containsBody(checkMe)) return cObj;
        }
		return null;
	}
	/*public void delPBs(){
        Iterator<PhysObject> it=physObject.iterator();
        while(it.hasNext())
        {
        	PhysObject cObj=(PhysObject)it.next();
        	cObj.destroyBody(mWorld);
          //System.out.println("Value :"+value);
        }
        Iterator<SoftBody> softit=softBody.iterator();
        while(softit.hasNext())
        {
        	SoftBody cObj=(SoftBody)softit.next();
        	cObj.destroyBody();
        }
	}*/

	@Override
	public void dispose() {
		batch.dispose();
		texture.dispose();
	}

	@Override
	public void render() {
		inputModule.update();
        //mWorld.step(Gdx.app.getGraphics().getDeltaTime(), 3, 3);
		if(frozen){
			mWorld.setGravity(Vector2.Zero);
			Iterator<Body> pbi = mWorld.getBodies();
			while(pbi.hasNext()){
				Body cb = pbi.next();
				cb.setLinearVelocity(0, 0);
				cb.setAngularVelocity(0);
			}
			
		} else {
			mWorld.setGravity(new Vector2(0,-9.81f));
			Iterator<Body> pbi = mWorld.getBodies();
			while(pbi.hasNext()){
				Body cb = pbi.next();
				cb.setAwake(true);
			}
			
		}
		if(runStep)
        mWorld.step(1.f/60.f, 3, 3);
        
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
        Iterator<PhysObject> it=physObject.iterator();
        while(it.hasNext())
        {
        	PhysObject cObj=(PhysObject)it.next();
        	cObj.draw(batch);
        }
        Iterator<SoftBody> softit=softBody.iterator();
        while(softit.hasNext())
        {
        	SoftBody cObj=(SoftBody)softit.next();
        	cObj.draw(batch);
        }
        
        
        inputModule.paintingControl.deformableMesh.renderSprites(batch,sprite);

		batch.end();
		inputModule.paintingControl.deformableMesh.renderLines(this);
		debugRenderer.render(mWorld, camera.combined);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
