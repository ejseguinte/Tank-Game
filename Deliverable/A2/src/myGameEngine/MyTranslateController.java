package myGameEngine;

import graphicslib3D.Matrix3D;
import sage.scene.Controller;
import sage.scene.SceneNode;

public class MyTranslateController extends Controller {
	
	private double translationRate = .002 ; // movement per second
	private double cycleTime = 2000.0; // default cycle time
	private double totalTime;
	private double direction = 1.0;
	
	public void setCycleTime(double c){
		cycleTime = c;
	}
	
	@Override
	public void update(double time) {
		totalTime += time;
		double transAmount = translationRate * time;
		
		if(totalTime > cycleTime){
			direction = -direction;
			totalTime = 0;
		}
		
		transAmount = direction * transAmount;
		
		Matrix3D newTrans = new Matrix3D();
		newTrans.translate(0, transAmount, 0);
		
		for(SceneNode node: controlledNodes){
			Matrix3D curTrans = node.getLocalTranslation();
			curTrans.concatenate(newTrans);
			node.setLocalTranslation(curTrans);
		}
	}

}
