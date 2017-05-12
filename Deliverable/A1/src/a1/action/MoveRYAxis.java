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
public class MoveRYAxis extends AbstractInputAction{
    private ICamera camera;
    private float speed;
    public MoveRYAxis(ICamera c, float s){ 
        camera = c;
        speed = s;
    }
    public void performAction(float time, net.java.games.input.Event e ){
        Matrix3D rotationAmt = new Matrix3D();
        Vector3D nd = camera.getViewDirection(); //Z Direction
        Vector3D vd = camera.getUpAxis();        //Y Direction
        Vector3D ud = camera.getRightAxis();     //X Direction
        if (e.getValue() < -0.2){ 
            rotationAmt.rotate(speed,ud);
        }else { 
            if (e.getValue() > 0.2){ 
                rotationAmt.rotate(-speed,ud);
            }
        }
        //create a point for the new location
        nd = nd.mult(rotationAmt);
        vd = vd.mult(rotationAmt);
        camera.setUpAxis(vd.normalize());
        camera.setViewDirection(nd.normalize());
    } 
}
