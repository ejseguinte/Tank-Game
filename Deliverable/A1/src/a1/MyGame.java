/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a1;

import a1.action.*;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.text.DecimalFormat;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import sage.app.BaseGame;
import sage.camera.ICamera;
import sage.display.IDisplaySystem;
import sage.event.EventManager;
import sage.event.IEventManager;
import sage.input.IInputManager;
import sage.input.action.IAction;
import sage.scene.HUDString;
import sage.scene.Leaf;
import sage.scene.shape.*;

/**
 *
 * @author EJSeguinte
 */
public class MyGame extends BaseGame implements MouseMotionListener{
    private Robot robot;
    private Point cCenter;
    private float prevMouseX, prevMouseY;
    private float curMouseX, curMouseY;
    private boolean isRecentering;
    private int score = 0;
    private float timeTemp = 0;
    private float time = 0;
    private HUDString scoreString;
    private HUDString timeString;
    
    private IEventManager eventMgr;
    private int numCrashes = 0;
    private Leaf[] pyr;
    private MyTruck truck;
    private Matrix3D[] matrix;
    private IDisplaySystem display;
    private ICamera camera;
    private IInputManager im;
    
    @Override
    protected void initGame(){
        eventMgr = EventManager.getInstance();
        
        initMouseMode();
        initGameObjects();
        initInputs();
        
    }
    
    private void initInputs(){
       display.addMouseMotionListener(this);

        im = getInputManager();
        String kbName = im.getKeyboardName();

        IAction quitGame = new QuitGameAction(this);
        SetSpeedAction setSpeed = new SetSpeedAction();
        ForwardAction mvForward = new ForwardAction(camera, setSpeed);
        BackwardAction mvBackward = new BackwardAction(camera, setSpeed);
        LeftAction mvLeft = new LeftAction(camera, setSpeed);
        RightAction mvRight = new RightAction(camera, setSpeed);
        DownPitchAction rtDown  = new DownPitchAction(camera,setSpeed);
        UpPitchAction rtUp = new UpPitchAction(camera,setSpeed);
        RightYewAction rtRight = new RightYewAction(camera,setSpeed);
        LeftYewAction rtLeft = new LeftYewAction(camera,setSpeed);
        LeftRollAction rlLeft = new LeftRollAction(camera,setSpeed);
        RightRollAction rlRight= new RightRollAction(camera,setSpeed);
        
        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.ESCAPE, quitGame, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.LSHIFT, setSpeed, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE );
        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.W, mvForward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.S, mvBackward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.A, mvLeft, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.D, mvRight, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.UP, rtUp, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.DOWN, rtDown, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.LEFT, rtLeft, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.RIGHT, rtRight, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.Q, rlLeft, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.E, rlRight, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        
        String gpName = im.getFirstGamepadName();
        if( gpName != null){
	        IAction yAxisMove = new MoveYAxis(camera, 0.05f);
	        IAction xAxisMove = new MoveXAxis(camera, 0.05f);
	        IAction ryAxisMove = new MoveRYAxis(camera, 0.05f);
	        IAction rxAxisMove = new MoveRXAxis(camera, 0.05f);
	        
	        
	        im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._3, mvForward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	        im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._8, setSpeed, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
	       
	        im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.Y, yAxisMove, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	        im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.X, xAxisMove, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	        im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.RY, ryAxisMove, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	        im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.RX, rxAxisMove, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
        }
    }
    
    private void initGameObjects() {
        display = getDisplaySystem();
        display.setTitle("My Cool Game");

        camera = display.getRenderer().getCamera();
        camera.setPerspectiveFrustum(45, 1, 0.01, 1000);
        camera.setLocation(new Point3D(1, 1, 20));

        Teapot one = new Teapot(Color.pink);
        Matrix3D oneM = one.getLocalTranslation();
        oneM.translate(-2,5,-12);
        one.setLocalTranslation(oneM);
        addGameWorldObject(one);
        oneM = new Matrix3D();
        oneM.translate(1, 1, -1);
        
        Teapot two = new Teapot();
        Matrix3D twoM = two.getLocalTranslation();
        twoM.translate(2,15,12);
        two.setLocalTranslation(twoM);
        addGameWorldObject(two);
        twoM = new Matrix3D();
        twoM.translate(1, 1, -3);
        
        Teapot three = new Teapot();
        Matrix3D threeM = three.getLocalTranslation();
        threeM.translate(-2,5,12);
        three.setLocalTranslation(threeM);
        addGameWorldObject(three);
        threeM = new Matrix3D();
        threeM.translate(3, 1, -1);
        
        Teapot four = new Teapot();
        Matrix3D fourM = four.getLocalTranslation();
        fourM.translate(-2,15,2);
        four.setLocalTranslation(fourM);
        addGameWorldObject(four);
        fourM = new Matrix3D();
        fourM.translate(3, 1, -3);
        
        truck = new MyTruck();
        Matrix3D truckM = truck.getLocalTranslation();
        truckM.translate(0,0,0);
        truck.setLocalTranslation(truckM);
        addGameWorldObject(truck);
        
        pyr = new Leaf[] {(Leaf) one,(Leaf) two,(Leaf) three,(Leaf) four};
        matrix = new Matrix3D[]{oneM,twoM,threeM,fourM};
        Point3D origin = new Point3D(0,0,0);
        Point3D xEnd = new Point3D(10000000,0,0);
        Point3D yEnd = new Point3D(0,10000000,0);
        Point3D zEnd = new Point3D(0,0,10000000);
        Line xAxis = new Line (origin, xEnd, Color.red, 2);
        Line yAxis = new Line (origin, yEnd, Color.green, 2);
        Line zAxis = new Line (origin, zEnd, Color.blue, 2);
        addGameWorldObject(xAxis); 
        addGameWorldObject(yAxis);
        addGameWorldObject(zAxis);
        
        super.update(0f);
        eventMgr.addListener(truck, CrashEvent.class);

        timeString = new HUDString ("time = " + time);
        timeString.setLocation(0, 0.05);
        addGameWorldObject(timeString);
        scoreString = new HUDString("score = " + score);
        scoreString.setLocation(0, 0.025);
        addGameWorldObject(scoreString);
    }
    
    @Override
    public void update(float elapsedTimeMS){ 
        if(score < pyr.length){
            for (Leaf i : pyr) {
                if(i.getWorldBound().contains(camera.getLocation())){
                    numCrashes++;
                    CrashEvent newCrash = new CrashEvent(numCrashes);
                    eventMgr.triggerEvent(newCrash);
                    i.setLocalTranslation(matrix[score]);
                    score++;
                    timeTemp = 5;
                }
            }
        }
        
        scoreString.setText("Score = " + score);
        time += elapsedTimeMS;
        DecimalFormat df = new DecimalFormat("0.0");
        timeString.setText("Time = " + df.format(time/1000));
        if(elapsedTimeMS > timeTemp){
            truck.setColorBuffer(truck.colorBuffer1);
        }
        super.update(elapsedTimeMS); 
    }
    
    private void initMouseMode(){
        Dimension dim = getRenderer().getCanvas().getSize();
        cCenter = new Point(dim.width/2, dim.height/2);
        isRecentering = false;
        try{ // some platforms may not support the Robot class
            robot = new Robot();
        } catch (AWTException ex){ 
            throw new RuntimeException("Couldn't create Robot!"); 
        }
        recenterMouse();
        prevMouseX = cCenter.x; // 'prevMouse' defines the initial
        prevMouseY = cCenter.y; // mouse position
        // also demonstrates changing the cursor
        Image faceImage = new ImageIcon("images/face.gif").getImage();
        Cursor faceCursor = Toolkit.getDefaultToolkit().createCustomCursor(faceImage, new Point(0,0), "FaceCursor");
        getRenderer().getCanvas().setCursor(faceCursor);
    }
    
    private void recenterMouse(){
        // Note that this generates one MouseEvent.
        isRecentering = true;
        Point p = new Point(cCenter.x, cCenter.y);
        Canvas canvas = getRenderer().getCanvas();
        SwingUtilities.convertPointToScreen(p, canvas);
        robot.mouseMove(p.x, p.y);
    }
    
    @Override
    public void mouseMoved(MouseEvent e){
        // if the robot is recentering and the location is in the center,
        // then this event was generated by the robot
        if (isRecentering && cCenter.x == e.getX() && cCenter.y == e.getY()){
        // mouse has been recentered, recentering is complete
            isRecentering = false;
        }else{ // event was due to a user mouse-move, and so it must be processed
            curMouseX = e.getX();
            curMouseY = e.getY();
            float mouseDeltaX = prevMouseX - curMouseX;
            float mouseDeltaY = prevMouseY - curMouseY;
            yaw(mouseDeltaX);
            pitch(mouseDeltaY);
            prevMouseX = curMouseX;
            prevMouseY = curMouseY;
            // tell robot to put the cursor back to the
            recenterMouse();
            prevMouseX = cCenter.x;
            prevMouseY = cCenter.y;
        }
    }
    
    public void pitch(float mouseDeltaY){
        Matrix3D rotationAmt = new Matrix3D();
        Vector3D nd = camera.getViewDirection(); //Z Direction
        Vector3D vd = camera.getUpAxis();        //Y Direction
        Vector3D ud = camera.getRightAxis();     //X Direction
        if (mouseDeltaY < 0.0){
            rotationAmt.rotate(-0.15,ud);
        }else{
            rotationAmt.rotate(0.15,ud);
        }
        nd = nd.mult(rotationAmt);
        vd = vd.mult(rotationAmt);
        camera.setUpAxis(vd.normalize());
        camera.setViewDirection(nd.normalize());
    }
    
    public void yaw(float mouseDeltaX) {
        Matrix3D rotationAmt = new Matrix3D();
        Vector3D nd = camera.getViewDirection(); //Z Direction
        Vector3D vd = camera.getUpAxis();        //Y Direction
        Vector3D ud = camera.getRightAxis();     //X Direction
        if (mouseDeltaX < 0.0){
            rotationAmt.rotate(-0.15,vd);
        }else{
            rotationAmt.rotate(0.105,vd);
        }
        nd = nd.mult(rotationAmt);
        ud = ud.mult(rotationAmt);
        camera.setRightAxis(ud.normalize());
        camera.setViewDirection(nd.normalize());
    } 

    @Override
    public void mouseDragged(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
