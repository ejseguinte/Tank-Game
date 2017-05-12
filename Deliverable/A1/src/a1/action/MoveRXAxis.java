/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a1.action;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

/**
 *
 * @author EJSeguinte
 */
public class MoveRXAxis extends AbstractInputAction{
    private ICamera camera;
    private float speed;
    public MoveRXAxis(ICamera c, float s){ 
        camera = c;
        speed = s;
    }
    public void performAction(float time, net.java.games.input.Event e ){
        Matrix3D rotationAmt = new Matrix3D();
        Vector3D nd = camera.getViewDirection(); //Z Direction
        Vector3D vd = camera.getUpAxis();        //Y Direction
        Vector3D ud = camera.getRightAxis();     //X Direction
        if (e.getValue() < -0.2){ 
            rotationAmt.rotate(speed,vd);
        }else { 
            if (e.getValue() > 0.2){ 
                rotationAmt.rotate(-speed,vd);
            }
        }
        //create a point for the new location
        nd = nd.mult(rotationAmt);
        ud = ud.mult(rotationAmt);
        camera.setRightAxis(ud.normalize());
        camera.setViewDirection(nd.normalize());
    } 
}
