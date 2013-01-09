package net.chozabu.gexEdit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Intersector;

import java.awt.geom.Area;

//IMPORTANT NOTE BOX2D USES CCW POLY WINDING
//current implementation:
//store complete outer loop CCW
//store inner loops CW
//generate complete loop with keyholes to pass to triangulator.
//use triangles for physics and rendering
//use outer loop to determine if an object is broken off
public class DeformableMesh {
	
	Area tArea;

	List<Vector2> points;
	List<InterSection> mInterSection;
	
	//Intersection is for a double linked list
	//I should probably have separate lists to allow for n-poly operations.
	class InterSection {
		Vector2 pA1,pA2,pB1,pB2,pI;//the 4 input points, and intersection point
		//boolean aEntering,bEntering; //entering or leaving polygon?
		int indexA, indexB;
		List<Vector2> pointsA;//do I want this?
		List<Vector2> pointsB;
		//List<InterSection> isA;//and/or this for n-poly collisions?
		//List<InterSection> isB;
		InterSection lastA, lastB;
		InterSection nextA, nextB;
		boolean resolved = false;
		InterSection(){
		}
	}
	class InnerLoop {
		List<Vector2> points;
		InnerLoop(){
			points = new ArrayList<Vector2>();
		}
	}
	List<InnerLoop> innerLoop;
	
	DeformableMesh(){
		points = new ArrayList<Vector2>();
		innerLoop = new ArrayList<InnerLoop>();
	}
	

	
	//optimise this.
	public void addLoop(List<Vector2> pointsA){
		Log.o("\n");
		
		//Area na;
		//na.
		
		if (points == null || points.size()<2){
			System.out.println("adding new shape!");
			points = pointsA;
			return;
		}
		
		
		/*if(points.get(0).dst2(pointsA.get(0))<1f){
			Log.o("Too close for comfort! quitting");
			return;
		}*/

		/**************************
		 * find intersections
		 */
		System.out.println("Checking intersections");
		List<InterSection> interSection = new ArrayList<InterSection>();
		Vector2 lastO=points.get(points.size()-1);
		Vector2 lastN=pointsA.get(pointsA.size()-1);
		//points.get(points.size());
		InterSection lastA = null;
		int indexA = 0;
		InterSection newI = new InterSection();
		for (Vector2 n: pointsA){//TODO blast. these nested loops should be reversed to allow for multiple input!
			int indexB = 0;
			boolean gotA = false;
			for (Vector2 o: points){
				if(o.dst2(n)<0.01f){
					//Log.o("two verts touchin, panic! quitting!!");
					//return;
				}
				boolean overlapping = false;
				Vector2 pI = new Vector2();
				overlapping = Intersector.intersectSegments(lastN, n, lastO, o, pI);
				if (overlapping){
					newI = new InterSection();
					newI.indexA = indexA;
					newI.indexB = indexB;
					newI.pA1 = lastN;
					newI.pA2 = n;
					newI.pB1 = lastO;
					newI.pB2 = o;
					newI.pointsA = pointsA;
					newI.pointsB = points;
					if (!gotA){
						newI.lastA = lastA;//can't do lastB here!
						if (lastA!=null)
							lastA.nextA=newI;
					} else {
						Log.o("double collision.");
						return;
					}
					newI.pI = pI;
					interSection.add(newI);
					lastA=newI;
					//break;//magic fix? or cover-up
				}
				lastO = o;
				indexB++;
			}
			lastN = n;
			indexA++;
		}
		System.out.println("found: "+interSection.size());
		
		
		//replace with new shape//TODO add separate new shape instead
		if (interSection.size() < 1){
			boolean inpoly = false;
			for (Vector2 n: pointsA){//I could just do this for any one point...
				if (Intersector.isPointInPolygon(points, n)){
					inpoly = true;
					break;
				}	
			}
			if(inpoly){
				Log.o("Inside existing poly - implement innerloops!");
				return;
			} else {
				System.out.println("no intersection! replacing with new shape");
				innerLoop.clear();
				points = pointsA;
			}
			return;
		} else if (interSection.size() > 6){
			Log.o("over 6 intersections? broken. or you are working on a complex world, and this line should have been removed from the code by now. quitting");
			return;
		}
		//complete the loop!
		newI.nextA = interSection.get(0);
		

		/**************************
		 * sort intersections
		 */
		//sort B's intersections links. //TODO do this for n-polys, to allow n-poly operations.
		InterSection lastB = null;
		InterSection startB = null;
		InterSection endB = null;
		for (Vector2 o: points){
			for(InterSection sortI: interSection){
				if (sortI.pB1 == o){
					endB = sortI;
					if (startB == null)
						startB = sortI;
					sortI.lastB = lastB;
					if (lastB!=null)
						lastB.nextB=sortI;
					lastB=sortI;
				}
			}
			lastO = o;
		}
		endB.nextB = startB;
		startB.lastB = endB;

		System.out.println("Sorted intersections for poly B");
		
		mInterSection = interSection;
		

		
		/*int iindex = 0;
		for(InterSection printI: interSection){
			System.out.println("index "+iindex);
			iindex++;
		}*/
		

		/**************************
		 * Construct Loops
		 */
		System.out.println("--===BEGIN LOOPING LOOP==--");
		List<InnerLoop> loops = new ArrayList<InnerLoop>();
		boolean done = false;
		while (!done){
			InnerLoop tLoop = new InnerLoop();
			tLoop.points = makeLoop(interSection, pointsA);
			if (tLoop.points != null){
				loops.add(tLoop);
				//points = tLoop.points;
			} else {
				done = true;
				break;
			}
			Log.o("---LOOPING RECON----");
		}
		if (loops.size()<1){
			Log.o("something went wrong - no loops! quitting.");
			return;
		}
		
		InnerLoop biggest = (InnerLoop) loops.get(0);
		for (InnerLoop l: loops){
			if (l.points.size() > biggest.points.size()){
				biggest = l;
			}
		}
		

		//InnerLoop lastLoop = (InnerLoop) loops.get(loops.size()-1);
		points = biggest.points;
		
		for (InnerLoop l: loops){
			if (l.points!= biggest.points){
				innerLoop.add(l);
			}
		}
		
		/*Log.o("Simplifiying");
		Vector2 olp = null;
		Vector2 lp = null;
		List<Vector2> reml = new ArrayList<Vector2>();
		for (Vector2 p: points){
			if (olp!=null)
			if (lp.dst2(olp)< 0.3f && lp.dst2(p) < 0.3f){
				reml.add(p);
			}
			olp = lp;
			lp = p;
		}
		for (Vector2 p: reml){
			points.remove(p);
		}*/
		
		//makeLoop(tLoop.points,interSection, pointsA);
		//points = tLoop.points;
		Log.o("--------DONE------ found loops: "+loops.size());
	}
	
	
	List<Vector2> makeLoop(List<InterSection> interSection, List<Vector2> pointsA){
		
		List<Vector2> cLoop;
		//int cIndex;


		Vector2 testPoint = pointsA.get(pointsA.size()-1);
		System.out.println("Begin new line construction");
		//re-construct the outer loop
		InterSection currentIntersection = interSection.get(0);
		Iterator<InterSection> arg = interSection.iterator();
		while(arg.hasNext()){
			currentIntersection = arg.next();
			if(currentIntersection.resolved == false){
				testPoint = currentIntersection.pA1;
				break;
			}
		}
		if(currentIntersection == interSection.get(interSection.size()-1)){
			Log.o("We are done here.");
			return null;//TODO - indicate sucess?
		}
		InterSection startedAt = currentIntersection;
		
		
		boolean AinPoly = Intersector.isPointInPolygon(points, testPoint);
		if (AinPoly)
		{
			cLoop = pointsA;
			System.out.println("A[0] in B TRUE ");
		} else {
			System.out.println("A[0] in B FALSE");
			cLoop = points;
		}
		
		
		List<Vector2> newOuterLoop = new ArrayList<Vector2>();
		boolean complete = false;
		int iCount = 0;
		while (!complete){
			iCount++;
			newOuterLoop.add(currentIntersection.pI);
			//System.out.println(currentIntersection.indexA);
			if(cLoop == currentIntersection.pointsA){
				Log.o("Aadding from: "+currentIntersection.indexA+" to: "+currentIntersection.nextB.indexA);
				if (currentIntersection.indexA < currentIntersection.nextA.indexA){
					newOuterLoop.addAll(cLoop.subList(currentIntersection.indexA, currentIntersection.nextA.indexA));
				} else {
					newOuterLoop.addAll(cLoop.subList(currentIntersection.indexA, cLoop.size()));
					newOuterLoop.addAll(cLoop.subList(0, currentIntersection.nextA.indexA));
				}
				currentIntersection = currentIntersection.nextA;
				//cLoop = points;
				cLoop =  currentIntersection.pointsB;
			} else if(cLoop == currentIntersection.pointsB) {//this could be a general else
				Log.o("Badding from: "+currentIntersection.indexB+" to: "+currentIntersection.nextB.indexB);
				if (currentIntersection.indexB < currentIntersection.nextB.indexB){
					newOuterLoop.addAll(cLoop.subList(currentIntersection.indexB, currentIntersection.nextB.indexB));
				} else {
					newOuterLoop.addAll(cLoop.subList(currentIntersection.indexB, cLoop.size()));
					newOuterLoop.addAll(cLoop.subList(0, currentIntersection.nextB.indexB));
					
				}
				currentIntersection = currentIntersection.nextB;
				//cLoop = pointsA;
				cLoop =  currentIntersection.pointsA;
			} else {
				//this should not happen with only two objects.
				//some re-writing should also happen to allow for n-objects, replacing this if block
				System.out.println("Whoops! n-poly operations not implemented. or something broke...");
			}
			//newOuterLoop.add(currentIntersection.pI);
			

			Log.o("joined " + iCount +" intersections so far!");

			
			//quit the loop
			if (currentIntersection == startedAt){
				Log.o("finished");
				currentIntersection.resolved = true;
				complete = true;
				break;//this *shouldnt* be needed 
			}else if(currentIntersection.resolved == true){
				Log.o("------------------------");
				Log.o("--------warning---------");
				Log.o("--this should never-----");
				Log.o("-------happen-----------");
				Log.o("------------------------");
				Log.o("----looped without------");
				Log.o("--reaching 1st point----");
				Log.o("------------------------");
				return null;
			}else{
				currentIntersection.resolved = true;
			}

			
			
			//quit if something goes wrong.
			if (iCount > interSection.size()+2){
				Log.o("something has broken here, quitting before infinite loop!");
				Log.o("NEVER GOT BACK TO ORIGNAL INTERSECTION. not DUMPING POINTS");
				//points = newOuterLoop;
				return null;
			}
		}
		Log.o("iCount%2 is: "+iCount%2);
		if(iCount%2 == 0){
			//points = newOuterLoop;
			//returnList = newOuterLoop;
			return newOuterLoop;
		}else{
			Log.o("-------warning--------");
			Log.o("-----"+iCount+"-----");
			Log.o("-------uneven--------");
			return null;
			//return 0;
		}
		//return 0;//never happens, unless above if statement changed!
	}
	public void renderLines(MyGdxGame root){
		renderLinesSet(root, points);
		for (InnerLoop il: innerLoop){
			renderLinesSet(root, il.points);
		}

        if (mInterSection == null)return;
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        root.camera.update();
        shapeRenderer.setProjectionMatrix(root.camera.combined);
        shapeRenderer.begin(ShapeType.Line);
        float cp = 0.2f;
        float add = 0.3f;//1.f/mInterSection.size();
		for (InterSection i: mInterSection){
	        shapeRenderer.setColor(0, 1, 0, 1);
        	shapeRenderer.line(i.pI.x, i.pI.y, i.nextA.pI.x, i.nextA.pI.y);
        	//shapeRenderer.line(i.pI.x-0.1f, i.pI.y-0.1f, i.nextA.pI.x-0.1f, i.nextA.pI.y-0.1f);
	        shapeRenderer.setColor(0, 0, 1, 1);
        	shapeRenderer.line(i.pI.x-cp*0.1f, i.pI.y+cp*0.1f, i.nextB.pI.x-cp*0.1f, i.nextB.pI.y+cp*0.1f);
        	cp+=add;
		}
        shapeRenderer.end();
        shapeRenderer.begin(ShapeType.Circle);
        cp = 0.2f;
		for (InterSection i: mInterSection){
	        shapeRenderer.setColor(0, 1, 0, 1);
        	shapeRenderer.circle(i.pI.x, i.pI.y, cp);
	        shapeRenderer.setColor(0, 0, 1, 1);
        	shapeRenderer.circle(i.pI.x, i.pI.y, cp+add*0.5f);
        	cp+=add;
		}
        shapeRenderer.end();
	}
	public void renderLinesSet(MyGdxGame root,List<Vector2> pointsIn){

        if(pointsIn.size()>3){
	        ShapeRenderer shapeRenderer = new ShapeRenderer();
	        root.camera.update();
	        shapeRenderer.setProjectionMatrix(root.camera.combined);
	        shapeRenderer.begin(ShapeType.Line);
	        shapeRenderer.setColor(1, 0, 0, 1);
	        
	        Vector2 lP = pointsIn.get(pointsIn.size()-1);
	    	for (Vector2 p: pointsIn){
	            shapeRenderer.line(lP.x, lP.y, p.x, p.y);
	            lP = p;
	    	}
	        shapeRenderer.end();
        }
	}


	public void renderSprites(SpriteBatch batch, Sprite sprite) {
		/*renderSpritesSet(batch,sprite,points,1);
		for (InnerLoop il: innerLoop){
			renderSpritesSet(batch,sprite, il.points,0);
		}*/
        float pCount = 0f;
        if (mInterSection == null)return;
		for (InterSection i: mInterSection){
	    		pCount += 1.f/mInterSection.size();
	            sprite.setPosition(i.pI.x-sprite.getWidth()/2, i.pI.y-sprite.getHeight()/2);
	            sprite.setScale(1.5f-pCount);
	            //sprite.setRotation(bodyDef.angle*MathUtils.radiansToDegrees);
	            sprite.setColor(1-pCount, 1, 1, 1.f-pCount/2);
	            sprite.draw(batch);	
		}
	}

	public void renderSpritesSet(SpriteBatch batch, Sprite sprite,List<Vector2> pList,float g) {
        
        if(pList.size()>3){
	        float pCount = 0f;
	    	for (Vector2 p: pList){
	    		pCount += 1.f/pList.size();
	            sprite.setPosition(p.x-sprite.getWidth()/2, p.y-sprite.getHeight()/2);
	            sprite.setScale(1.5f-pCount);
	            //sprite.setRotation(bodyDef.angle*MathUtils.radiansToDegrees);
	            sprite.setColor(1-pCount, g, 1, 1.f-pCount/2);
	            sprite.draw(batch);
	    	}
        }
	}
}
