package myGameEngine;

import graphicslib3D.Matrix3D;
import sage.scene.Controller;
import sage.scene.SceneNode;

public class MyScaleController extends Controller {

	private double translationRate = 2 ; // movement per second
	private double cycleTime = 2000.0; // default cycle time
	private double totalTime;
	private float direction = 1.0f;
	private float growth = 1.0f +1f/1000;
	private float neggrowth = .90f;
	public void setCycleTime(double c){
		cycleTime = c;
	}
	
	@Override
	public void update(double time) {
		totalTime += time;
		
		if((totalTime > cycleTime) && growth < 1){
			growth = 1f +1f/1000;
			totalTime = 0;
		}else if((totalTime > cycleTime) && growth > 1){
			growth = 1f - 1f/1000;
			totalTime = 0;
		}
		
		for(SceneNode node: controlledNodes){
			node.scale(growth, growth, growth);;
		}
	}

}
