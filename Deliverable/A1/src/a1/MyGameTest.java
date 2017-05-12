/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a1;

import graphicslib3D.Point3D;
import java.awt.Color;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.Random;
import sage.app.BaseGame;
import sage.display.*;
import sage.scene.HUDString;
import sage.scene.SceneNode;
import sage.scene.shape.Rectangle;

/**
 *
 * @author EJSeguinte
 */
public class MyGameTest extends BaseGame implements MouseListener{
    private int score = 0;
    private float time = 0;
    private HUDString scoreString;
    private HUDString timeString;
    IDisplaySystem display;
    
    @Override
    public void initGame(){
        display = getDisplaySystem();
        
        Rectangle rect1 = new Rectangle(0.3f,0.3f);
        addGameWorldObject(rect1);
        Rectangle rect2 = new Rectangle(0.3f,0.3f);
        addGameWorldObject(rect2);
        
        timeString = new HUDString ("time = " + time);
        timeString.setLocation(0, 0.05);
        addGameWorldObject(timeString);
        scoreString = new HUDString("score = " + score);
        addGameWorldObject(scoreString);
        
        display.setTitle("MyGame");
        display.addMouseListener(this);
        
    }
    
    @Override
    public void update(float elapsedTimeMS){
        // add a small movement to each game world object
        for (SceneNode s : getGameWorld()){ 
            Random rng = new Random();
            float tx = (rng.nextFloat()*2 -1) * 0.005f ;
            float ty = (rng.nextFloat()*2 -1) * 0.005f ;
            s.translate(tx, ty, 0);
        }
        // update the HUD
        scoreString.setText("Score = " + score);
        time += elapsedTimeMS;
        DecimalFormat df = new DecimalFormat("0.0");
        timeString.setText("Time = " + df.format(time/1000));
        // tell BaseGame to update game world state
        super.update(elapsedTimeMS);
    }
    
    @Override
    public void mousePressed(MouseEvent e)
    {
        // get relevant screen parameters
        Point3D scrClickPoint = new Point3D(e.getX(), e.getY(), 0);
        double scrWidth = display.getWidth();
        double scrHeight = display.getHeight();
        // convert screen point to world coordinates
        // based on a default SAGE world window of -0.5..+0.5
        Point3D worldClickPoint = new Point3D();
        worldClickPoint.setX((scrClickPoint.getX() / scrWidth) - 0.5);
        worldClickPoint.setY(-((scrClickPoint.getY() / scrHeight) - 0.5));
        worldClickPoint.setZ(0.0);
        //see if mouse click was on a gameworld object
        for (SceneNode s : getGameWorld()){
            if (s instanceof Rectangle){
                Rectangle r = (Rectangle) s;
                if (r.contains(worldClickPoint)){
                // mouse hit target; change color and increase score
                r.setColor(Color.red);
                score++ ;
                } 
            } 
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    
}
