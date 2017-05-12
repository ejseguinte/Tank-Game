/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myGameEngine;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;

/**
 *
 * @author EJSeguinte
 */
public class MoveRYAxis extends AbstractInputAction{
    private Camera3Pcontroller camera;
    private float speed;
    
    public MoveRYAxis(Camera3Pcontroller c, float s){ 
        camera = c;
        speed = s;
    }
    public void performAction(float time, net.java.games.input.Event evt ){
		 float rotAmount;
		 if (evt.getValue() < -0.2) { 
			 rotAmount=-0.1f; 
			 } else { 
				 if (evt.getValue() > 0.2) { 
					 rotAmount=0.1f; 
				 } else { 
					 rotAmount=0.0f; 
				 }
		 }
		 camera.setCameraDistanceFromTarget(camera.getCameraDistanceFromTarget() + rotAmount) ;
    } 
}
