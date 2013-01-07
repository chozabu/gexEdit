package net.chozabu.gexEdit;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Intersector;

//IMPORTANT NOTE BOX2D USES CCW POLY WINDING
//current implementation:
//store complete outer loop CCW
//store inner loops CW
//generate complete loop with keyholes to pass to triangulator.
//use triangles for physics and rendering
//use outer loop to determine if an object is broken off
public class DeformableMesh {

	List<Vector2> points;
	
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
	}
	

	
	//optimise this.
	public void addLoop(List<Vector2> pointsA){
		
		if (points == null || points.size()<2){
			System.out.println("adding new shape!");
			points = pointsA;
			return;
		}
		
		if(points.get(0).dst2(pointsA.get(0))<1f){
			Log.o("Too close for comfort! quitting");
			return;
		}
		
		//find intersections
		System.out.println("Checking intersections");
		List<InterSection> interSection = new ArrayList<InterSection>();
		Vector2 lastO=points.get(points.size()-1);
		Vector2 lastN=pointsA.get(pointsA.size()-1);
		//points.get(points.size());
		InterSection lastA = null;
		int indexA = 0;
		Vector2 pI = new Vector2();
		InterSection newI = new InterSection();
		for (Vector2 n: pointsA){//TODO blast. these nested loops should be reversed to allow for multuple parimiters input!
			int indexB = 0;
			for (Vector2 o: points){
				boolean overlapping = false;
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
					newI.lastA = lastA;//can't do lastB here!
					if (lastA!=null)
						lastA.nextA=newI;
					newI.pI = pI;
					interSection.add(newI);
					lastA=newI;
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
			System.out.println("no intersection! replacing with new shape");
			points = pointsA;
			return;
		} else if (interSection.size() > 100){
			Log.o("over 100 intersections? broken. or you are working on a complex world, and this line should have been removed from the code by now. quitting");
			return;
		}
		//complete the loop!
		newI.nextA = interSection.get(0);
		
		//sort B's intersections links. //TODO do this for n-polys, to allow n-poly operations.
		InterSection lastB = null;
		InterSection startB = null;
		InterSection endB = null;
		for (Vector2 o: points){
			for(InterSection sortI: interSection){
				if (sortI.pB2 == o){
					if (startB == null)
						startB = sortI;
					else
						endB = sortI;
					sortI.lastB = lastB;
					if (lastB!=null)
						lastB.nextB=sortI;
					lastB=sortI;
				}
			}
			lastO = o;
		}
		endB.nextB = startB;

		System.out.println("Sorted intersections for poly B");
		
		List<Vector2> cLoop;
		//int cIndex;

		boolean AinPoly = Intersector.isPointInPolygon(points, pointsA.get(pointsA.size()-1));
		if (AinPoly)
		{
			cLoop = pointsA;
			System.out.println("A[0] in B TRUE ");
		} else {
			System.out.println("A[0] in B FALSE");
			cLoop = points;
		}
		
		int iindex = 0;
		for(InterSection printI: interSection){
			System.out.println("index "+iindex);
			iindex++;
			//System.out.println("nexta: "+printI.nextA);
			//System.out.println("nextb: "+printI.nextB);
		}

		System.out.println("Begin new line construction");
		//re-construct the outer loop
		InterSection cI = interSection.get(0);
		
		List<Vector2> newOuterLoop = new ArrayList<Vector2>();
		boolean complete = false;
		int iCount = 0;
		while (!complete){
			iCount++;
			//newOuterLoop.add(cI.pI);
			System.out.println(cI);
			if(cLoop == cI.pointsA){
				Log.o("adding from: "+cI.indexA+" to: "+cI.nextB.indexA);
				if (cI.indexA < cI.nextA.indexA){
					newOuterLoop.addAll(cLoop.subList(cI.indexA, cI.nextA.indexA));
				} else {
					newOuterLoop.addAll(cLoop.subList(cI.indexA, cLoop.size()));
					newOuterLoop.addAll(cLoop.subList(0, cI.nextA.indexA));
				}
				cI = cI.nextA;
				//cLoop = points;
				cLoop =  cI.pointsB;
			} else if(cLoop == cI.pointsB) {//this could be a general else
				Log.o("adding from: "+cI.indexB+" to: "+cI.nextB.indexB);
				if (cI.indexB < cI.nextB.indexB){
					newOuterLoop.addAll(cLoop.subList(cI.indexB, cI.nextB.indexB));
				} else {
					newOuterLoop.addAll(cLoop.subList(cI.indexB, cLoop.size()));
					newOuterLoop.addAll(cLoop.subList(0, cI.nextB.indexB));
					
				}
				cI = cI.nextB;
				//cLoop = pointsA;
				cLoop =  cI.pointsA;
			} else {
				//this should not happen with only two objects.
				//some re-writing should also happen to allow for n-objects, replacing this if block
				System.out.println("Whoops! n-poly operations not implemented. or something broke...");
			}
			//newOuterLoop.add(cI.pI);
			

			Log.o("joined " + iCount +" intersections so far!");
			//quit the loop
			if (cI == interSection.get(0)){
				Log.o("finished");
				complete = true;
				break;//this *shouldnt* be needed 
			}
			
			//quit if something goes wrong.
			if (iCount > interSection.size()+2){
				Log.o("something has broken here, quitting before infinite loop!");
				Log.o("NEVER GOT BACK TO ORIGNAL INTERSECTION. DUMPING POINTS");
				points = newOuterLoop;
				return;
			}
		}
		points = newOuterLoop;
	}
	public void renderShapes(){
		
	}
	public void renderLines(MyGdxGame root){

        if(points.size()>3){
	        ShapeRenderer shapeRenderer = new ShapeRenderer();
	        root.camera.update();
	        shapeRenderer.setProjectionMatrix(root.camera.combined);
	        shapeRenderer.begin(ShapeType.Line);
	        shapeRenderer.setColor(1, 0, 0, 1);
	        
	        Vector2 lP = points.get(points.size()-1);
	    	for (Vector2 p: points){
	            shapeRenderer.line(lP.x, lP.y, p.x, p.y);
	            lP = p;
	    	}
	        shapeRenderer.end();
        }
	}
}
