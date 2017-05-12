/*

 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myGameEngine;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;

/**
 *
 * @author EJSeguinte
 */
public class MoveZAxis extends AbstractInputAction{
    private float speed;
    private SceneNode target;
    public MoveZAxis(Camera3Pcontroller c, float s){ 
        speed = s;
        target = c.getTarget();
    }
    public void performAction(float time, net.java.games.input.Event e )
    {
//        Vector3D newLocVector = new Vector3D();
//        Vector3D viewDir = camera.getViewDirection().normalize();
//        Vector3D curLocVector = new Vector3D(camera.getLocation());
//        if (e.getValue() < -0.2){ 
//            newLocVector = curLocVector.add(viewDir.mult(speed * time)); 
//        }else { 
//            if (e.getValue() > 0.2){ 
//                newLocVector = curLocVector.minus(viewDir.mult(speed * time)); 
//            }else { 
//                newLocVector = curLocVector; 
//            }
//        }
//        //create a point for the new location
//        double newX = newLocVector.getX();
//        double newY = newLocVector.getY();
//        double newZ = newLocVector.getZ();
//        Point3D newLoc = new Point3D(newX, newY, newZ);
//        camera.setLocation(newLoc);
        
        if (e.getValue() < -0.2){ 
        	target.getLocalTranslation().translate(0, 0, (speed * time * 1));
        }else { 
            if (e.getValue() > 0.2){ 
            	target.getLocalTranslation().translate(0, 0, (speed * time * -1));
            }else { 
                //newLocVector = curLocVector; 
            }
        }
    } 
}
