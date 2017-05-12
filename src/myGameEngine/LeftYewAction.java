/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myGameEngine;

import a1.MyGame;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.camera.controllers.ThirdPersonOrbitCameraController;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;

/**
 *
 * @author EJSeguinte
 */
public class LeftYewAction extends AbstractInputAction { 
    private Camera3Pcontroller camera;
    private SetSpeedAction runAction;
    private MyGame game;
    
    public LeftYewAction(Camera3Pcontroller cam1, SetSpeedAction r, MyGame game){
        camera = cam1;
        runAction = r;
        this.game = game;
    }
    
    public void performAction(float time, Event e){ 
        float moveAmount ;
        if (runAction.isRunning()){ 
            moveAmount = (float) 1.5 ; 
        } else { 
            moveAmount = (float) .9 ; 
        }
        
//        Matrix3D rotationAmt = new Matrix3D();
//        Vector3D nd = camera.getViewDirection(); //Z Direction
//        Vector3D vd = camera.getUpAxis();        //Y Direction
//        Vector3D ud = camera.getRightAxis();     //X Direction
//        rotationAmt.rotate(moveAmount,vd);
//        nd = nd.mult(rotationAmt);
//        ud = ud.mult(rotationAmt);
//        camera.setRightAxis(ud.normalize());
//        camera.setViewDirection(nd.normalize());
         Vector3D targetV = new Vector3D(0,1,0);
		 SceneNode target = camera.getTarget();
		 target.rotate(moveAmount, targetV);
		 game.setTopRot(moveAmount);
		 camera.setCameraAzimuth(camera.getCameraAzimuth() + moveAmount) ;
		 camera.setCameraAzimuth(camera.getCameraAzimuth() % 360) ;
		 camera.update(time);
    }   
}
