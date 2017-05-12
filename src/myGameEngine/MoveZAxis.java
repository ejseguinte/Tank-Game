/*

 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myGameEngine;

import a1.MyGame;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;
import sage.terrain.TerrainBlock;

/**
 *
 * @author EJSeguinte
 */
public class MoveZAxis extends AbstractInputAction{
	private SceneNode target;
    private SetSpeedAction runAction;
    private SceneNode tread;
    private SceneNode tankTop; //hehehe
    private TerrainBlock terrain;
    private MyGame game;
    
    public MoveZAxis(Camera3Pcontroller c, SetSpeedAction r, SceneNode tread, SceneNode top, TerrainBlock ter, MyGame game){ 
        target = c.getTarget();
        runAction = r;
        this.tread = tread;
        terrain = ter;
        tankTop = top;
        this.game = game;
    }
    
    private void updateVerticalPosition() {
    	Point3D avLoc = new Point3D(tread.getLocalTranslation().getCol(3));
    	float x = (float) avLoc.getX();
    	float z = (float) avLoc.getZ();
    	float terHeight = terrain.getHeight(x,z);
    	float desiredHeight = terHeight + (float)terrain.getOrigin().getY()-1;
    	tread.getLocalTranslation().setElementAt(1, 3, desiredHeight);
    	tankTop.getLocalTranslation().setElementAt(1, 3, desiredHeight + 1.5f);
    }
    
    public void performAction(float time, net.java.games.input.Event e ){
    	float moveAmount = 0;
    	if (e.getValue() < 0.9){ 
        	moveAmount = (float) 0.15 ;
        	float dis = moveAmount * time;
            float treadX = (float) (dis * Math.sin(Math.toRadians(game.getBotRot())));
            float treadZ = (float) (dis * Math.cos(Math.toRadians(game.getBotRot())));
            tread.translate(treadX, 0, treadZ);
            tankTop.translate(treadX, 0, treadZ);
            //updateVerticalPosition();
        }else { 
            if (e.getValue() > -0.9){ 
            	moveAmount = (float) -0.15 ;
            	float dis = moveAmount * time;
                float treadX = (float) (dis * Math.sin(Math.toRadians(game.getBotRot())));
                float treadZ = (float) (dis * Math.cos(Math.toRadians(game.getBotRot())));
                tread.translate(treadX, 0, treadZ);
                tankTop.translate(treadX, 0, treadZ);
                //updateVerticalPosition();
            }
        }
        
    } 
}
