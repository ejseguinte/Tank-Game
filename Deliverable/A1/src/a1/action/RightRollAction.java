/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a1.action;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

/**
 *
 * @author EJSeguinte
 */
public class RightRollAction extends AbstractInputAction { 
    private ICamera camera;
    private SetSpeedAction runAction;
    
    public RightRollAction(ICamera c, SetSpeedAction r){ 
        camera = c;
        runAction = r;
    }
    
    public void performAction(float time, Event e){ 
        float moveAmount ;
        if (runAction.isRunning()){ 
            moveAmount = (float) 1.5 ; 
        } else { 
            moveAmount = (float) .5 ; 
        }
        
        Matrix3D rotationAmt = new Matrix3D();
        Vector3D nd = camera.getViewDirection(); //Z Direction
        Vector3D vd = camera.getUpAxis();        //Y Direction
        Vector3D ud = camera.getRightAxis();     //X Direction
        rotationAmt.rotate(moveAmount,nd);
        vd = vd.mult(rotationAmt);
        ud = ud.mult(rotationAmt);
        camera.setRightAxis(ud.normalize());
        camera.setUpAxis(vd.normalize());
    }   
}
