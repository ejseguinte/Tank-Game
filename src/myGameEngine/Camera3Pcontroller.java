package myGameEngine;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Component.Identifier.Axis;
import sage.camera.ICamera;
import sage.input.IInputManager;
import sage.input.action.IAction;
import sage.scene.SceneNode;
import sage.util.MathUtils;

public class Camera3Pcontroller{
	 private ICamera cam; //the camera being controlled
	 private SceneNode target; //the target the camera looks at
	 private float cameraAzimuth; //rotation of camera around target Y axis
	 private float cameraElevation; //elevation of camera above target
	 private float cameraDistanceFromTarget;
	 private Point3D targetPos; // avatar’s position in the world
	 private Vector3D worldUpVec;
	 
	 public Camera3Pcontroller(ICamera cam, SceneNode target, IInputManager im, String controller){ 
		 this.cam = cam;
		 this.target = target;
		 worldUpVec = new Vector3D(0,1,0);
		 setCameraDistanceFromTarget(15.0f);
		 setCameraAzimuth(180); // start from BEHIND and ABOVE the target
		 setCameraElevation(15.0f); // elevation is in degrees
		 update(0.0f); // initialize camera state
	 }
	 
	 public void update(float time){
		 updateTarget();
		 updateCameraPosition();
		 cam.lookAt(targetPos, worldUpVec); // SAGE built-in function
	 }
	 
	 private void updateTarget(){
		 targetPos = new Point3D(target.getWorldTranslation().getCol(3)); 
	 }
	 
	 private void updateCameraPosition(){
		 double theta = getCameraAzimuth();
		 double phi = getCameraElevation() ;
		 double r = getCameraDistanceFromTarget();
		 // calculate new camera position in Cartesian coords
		 Point3D relativePosition = MathUtils.sphericalToCartesian(theta, phi, r);
		 Point3D desiredCameraLoc = relativePosition.add(targetPos);
		 cam.setLocation(desiredCameraLoc);
	 }

	 public float getCameraAzimuth() {
		return cameraAzimuth;
	}

	public void setCameraAzimuth(float cameraAzimuth) {
		this.cameraAzimuth = cameraAzimuth;
	}

	public float getCameraElevation() {
		return cameraElevation;
	}

	public void setCameraElevation(float cameraElevation) {
		this.cameraElevation = cameraElevation;
	}

	public float getCameraDistanceFromTarget() {
		return cameraDistanceFromTarget;
	}

	public void setCameraDistanceFromTarget(float cameraDistanceFromTarget) {
		this.cameraDistanceFromTarget = cameraDistanceFromTarget;
	}
	
	public SceneNode getTarget(){
		return target;
	}
	
	public void reset(){
		Vector3D dir = new Vector3D(0,0,0);
		cam.setViewDirection(dir);
	}
}