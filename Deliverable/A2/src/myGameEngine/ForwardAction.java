/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myGameEngine;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;

/**
 *
 * @author EJSeguinte
 */
public class ForwardAction extends AbstractInputAction { 
    private SetSpeedAction runAction;
    private SceneNode target;
    
    public ForwardAction(Camera3Pcontroller c, SetSpeedAction r){ 
        target = c.getTarget();
        runAction = r;
    }
    
    public void performAction(float time, Event e){ 
        float moveAmount ;
        if (runAction.isRunning()){ 
            moveAmount = (float) 0.5 ; 
        } else { 
            moveAmount = (float) 0.1 ; 
        }
//    Vector3D viewDir = camera.getViewDirection().normalize();
//    Vector3D curLocVector = new Vector3D(camera.getLocation());
//    Vector3D newLocVec = curLocVector.add(viewDir.mult(moveAmount));
//    double newX = newLocVec.getX();
//    double newY = newLocVec.getY();
//    double newZ = newLocVec.getZ();
//    Point3D newLoc = new Point3D(newX, newY, newZ);
//    camera.setLocation(newLoc);
        target.getLocalTranslation().translate(0,0, (moveAmount * time * 1));
    }
}
