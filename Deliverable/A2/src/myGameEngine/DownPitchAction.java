/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myGameEngine;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;

/**
 *
 * @author EJSeguinte
 */
public class DownPitchAction extends AbstractInputAction { 
    private Camera3Pcontroller camera;
    private SetSpeedAction runAction;
    
    public DownPitchAction(Camera3Pcontroller c, SetSpeedAction r){ 
        camera = c;
        runAction = r;
    }
    
    public void performAction(float time, Event e){ 
        float moveAmount ;
        if (runAction.isRunning()){ 
            moveAmount = (float) .5 ; 
        } else { 
            moveAmount = (float) .1 ; 
        }
         camera.setCameraDistanceFromTarget(camera.getCameraDistanceFromTarget() + moveAmount) ;
    }   
}
