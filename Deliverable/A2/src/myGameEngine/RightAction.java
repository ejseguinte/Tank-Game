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
public class RightAction extends AbstractInputAction { 
    private SceneNode target;
    private SetSpeedAction runAction;
    
    public RightAction(Camera3Pcontroller c, SetSpeedAction r){ 
        target  = c.getTarget();
        runAction = r;
    }
    
    public void performAction(float time, Event e){ 
        float moveAmount ;
        if (runAction.isRunning()){ 
            moveAmount = (float) -0.5 ; 
        } else { 
            moveAmount = (float) -0.1 ; 
        }
//        //Vector3D viewDir = camera.getViewDirection().normalize();
//        Vector3D curLocVector = new Vector3D(camera.getLocation());
//        Vector3D move = new Vector3D(moveAmount,0,0);
//        Vector3D newLocVec = curLocVector.add(move);
//        double newX = newLocVec.getX();
//        double newY = newLocVec.getY();
//        double newZ = newLocVec.getZ();
//        Point3D newLoc = new Point3D(newX, newY, newZ);
//        camera.setLocation(newLoc);
        target.getLocalTranslation().translate((moveAmount * time), 0, 0);
        }
}
