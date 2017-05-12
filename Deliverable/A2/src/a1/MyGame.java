/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a1;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import myGameEngine.BackwardAction;
import myGameEngine.Camera3Pcontroller;
import myGameEngine.DownPitchAction;
import myGameEngine.ForwardAction;
import myGameEngine.LeftAction;
import myGameEngine.LeftRollAction;
import myGameEngine.LeftYewAction;
import myGameEngine.MoveRXAxis;
import myGameEngine.MoveRYAxis;
import myGameEngine.MoveXAxis;
import myGameEngine.MoveZAxis;
import myGameEngine.MyDisplaySystem;
import myGameEngine.MyScaleController;
import myGameEngine.MyTranslateController;
import myGameEngine.QuitGameAction;
import myGameEngine.RightAction;
import myGameEngine.RightRollAction;
import myGameEngine.RightYewAction;
import myGameEngine.SetSpeedAction;
import myGameEngine.UpPitchAction;
import net.java.games.input.Controller;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.vecmath.Vector3d;

import sage.app.BaseGame;
import sage.camera.ICamera;
import sage.camera.JOGLCamera;
import sage.display.DisplaySystem;
import sage.display.IDisplaySystem;
import sage.event.EventManager;
import sage.event.IEventManager;
import sage.input.IInputManager;
import sage.input.action.IAction;
import sage.renderer.IRenderer;
import sage.scene.Group;
import sage.scene.HUDString;
import sage.scene.Leaf;
import sage.scene.SceneNode;
import sage.scene.shape.*;
import sage.scene.shape.Rectangle;

/**
 *
 * @author EJSeguinte
 */
public class MyGame extends BaseGame {
    private int p1Score = 0, p2Score = 0;
    private float timeTemp = 0;
    private float time = 0;
    private HUDString p1ScoreString, p2ScoreString;
    private HUDString p1TimeString, p2TimeString;
    
    private IEventManager eventMgr;
    private int numCrashes = 0;
    private Leaf[] pyr;
    private MyTruck truck;
    private Matrix3D[] matrix;
    private IDisplaySystem display;
    private IRenderer renderer;
    private SceneNode player1, player2;
    private ICamera camera1, camera2;
    private Camera3Pcontroller cam1, cam2;
    private IInputManager im;
    
    
    private IDisplaySystem createDisplaySystem(){
    	IDisplaySystem display = new MyDisplaySystem(1920, 1080, 32, 60, true,"sage.renderer.jogl.JOGLRenderer");
    	//IDisplaySystem display = new DisplaySystem();
	    System.out.print("\nWaiting for display creation...");
	    int count = 0;
	    // wait until display creation completes or a timeout occurs
	    while (!display.isCreated()){
	    	try{ 
	    		Thread.sleep(10); 
			}catch (InterruptedException e){
				throw new RuntimeException("Display creation interrupted"); 
			}
		    count++;
		    System.out.print("+");
		    if (count % 80 == 0) {
		    	System.out.println(); 
			}
		    if (count > 2000) { // 20 seconds (approx.)
		    	throw new RuntimeException("Unable to create display");
		    }
	    }
	    System.out.println();
	    return display ;
    }
    
    protected void shutdown(){ 
    	display.close() ;
    	super.shutdown();
    //...other shutdown methods here as necessary...
    }
    
    @Override
    protected void initGame(){
    	initSytem();
        createScene();
        createPlayers();
        initInputs();
        
    }
    
    protected void initSytem(){
    	display = createDisplaySystem();
        display.setTitle("My Cool Game");
        renderer = display.getRenderer();
    	eventMgr = EventManager.getInstance();
        renderer = getDisplaySystem().getRenderer();
        im = getInputManager();
    }
    
    private String getInputs(){
    	ArrayList<Controller> controller = im.getControllers();
    	
    	for (int i = 0; i < controller.size(); i++) {
			System.out.println((i+1)+ ": " + controller.get(i).getName());
		}
    	System.out.println("Please pick a controller: ");
    	Scanner reader = new Scanner(System.in); 
    	int n = reader.nextInt();
    	reader.close();
		return controller.get(n-1).getName();
    }
    
    private void initInputs(){
    	
        String kbName = im.getKeyboardName();
        String mouseName = im.getMouseName();
        String gpName = im.getFirstGamepadName();
       
        cam1 = new Camera3Pcontroller(camera1,player1);
        cam2 = new Camera3Pcontroller(camera2, player2);
	        

        IAction quitGame = new QuitGameAction(this);
        SetSpeedAction setSpeed = new SetSpeedAction();
        ForwardAction mvForward = new ForwardAction(cam1, setSpeed);
        BackwardAction mvBackward = new BackwardAction(cam1, setSpeed);
        LeftAction mvLeft = new LeftAction(cam1, setSpeed);
        RightAction mvRight = new RightAction(cam1, setSpeed);
        DownPitchAction rtDown  = new DownPitchAction(cam1,setSpeed);
        UpPitchAction rtUp = new UpPitchAction(cam1,setSpeed);
        RightYewAction rtRight = new RightYewAction(cam1,setSpeed);
        LeftYewAction rtLeft = new LeftYewAction(cam1,setSpeed);
        LeftRollAction rlLeft = new LeftRollAction(camera1,setSpeed);
        RightRollAction rlRight= new RightRollAction(camera1,setSpeed);
        
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
        
        if( gpName != null){
	        IAction yAxisMove = new MoveZAxis(cam2, 0.05f);
	        IAction xAxisMove = new MoveXAxis(cam2, 0.05f);
	        IAction ryAxisMove = new MoveRYAxis(cam2, 0.05f);
	        IAction rxAxisMove = new MoveRXAxis(cam2, 0.05f);
	        
	        
	        im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._3, mvForward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	        im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._8, setSpeed, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
	       
	        im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.Y, yAxisMove, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	        im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.X, xAxisMove, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	        im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.RY, ryAxisMove, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	        im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.RX, rxAxisMove, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
        }
    }
    
    private void createScene() {
        Rectangle ground = new Rectangle();
        Matrix3D groundM = ground.getLocalTranslation();
        groundM.translate(1,-1,50);
        ground.setLocalTranslation(groundM);
        Vector3D groundV = new Vector3D(1,0,0);
        ground.rotate(90, groundV);
        ground.scale(100000, 100000, 1);
        ground.setColor(Color.LIGHT_GRAY);
        addGameWorldObject(ground);
        
        Pyramid one = new Pyramid();
        Matrix3D oneM = one.getLocalTranslation();
        oneM.translate(50,0,32);
        one.setLocalTranslation(oneM);
        oneM = new Matrix3D();
        oneM.translate(1, 3, -1);
        
        Pyramid two = new Pyramid();
        Matrix3D twoM = two.getLocalTranslation();
        twoM.translate(20,0,4);
        two.setLocalTranslation(twoM);
        twoM = new Matrix3D();
        twoM.translate(1, 3, -3);
        
        Pyramid three = new Pyramid();
        Matrix3D threeM = three.getLocalTranslation();
        threeM.translate(10,0,5);
        three.setLocalTranslation(threeM);
        threeM = new Matrix3D();
        threeM.translate(3, 3, -1);
        
        Pyramid four = new Pyramid();
        Matrix3D fourM = four.getLocalTranslation();
        fourM.translate(30,0,25);
        four.setLocalTranslation(fourM);
        fourM = new Matrix3D();
        fourM.translate(3, 3, -3);
        
        Group plants = new Group("Plants");
        plants.addChild(one);
        plants.addChild(two);
        plants.addChild(three);
        plants.addChild(four);
        MyTranslateController transCon = new MyTranslateController();
        MyScaleController scaleCon = new MyScaleController();
        scaleCon.addControlledNode(plants);
        transCon.addControlledNode(plants);
        plants.addController(transCon);
        plants.addController(scaleCon);
        addGameWorldObject(plants);
        
        truck = new MyTruck();
        Matrix3D truckM = truck.getLocalTranslation();
        truckM.translate(0,1,0);
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
    }
    
    private void createPlayers(){
    	 player2 = new Pyramid("PLAYER2");
    	 player2.translate(25, 1, 0);
    	 player2.rotate(-180, new Vector3D(0, 1, 0));
    	 addGameWorldObject(player2);
    	 camera2 = new JOGLCamera(renderer);
    	 camera2.setPerspectiveFrustum(60, 2, 1, 1000);
    	 camera2.setViewport(0.0, 1.0, 0.0, 0.45);
    	 
    	 player1 = new Cube("PLAYER1");
    	 player1.translate(50, 1, 0);
    	 player1.rotate(-90, new Vector3D(0, 1, 0));
    	 addGameWorldObject(player1);
    	 camera1 = new JOGLCamera(renderer);
    	 camera1.setPerspectiveFrustum(60, 2, 1, 1000);
    	 camera1.setViewport(0.0, 1.0, 0.55, 1.0);
    	 createPlayerHUDs();
    	 super.update(0f);
    }
    
    private void createPlayerHUDs(){
    	 HUDString player1ID = new HUDString("Player1");
    	 player1ID.setName("Player1ID");
    	 player1ID.setLocation(0.01, 0.15);
    	 player1ID.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
    	 player1ID.setColor(Color.red);
    	 player1ID.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
    	 camera1.addToHUD(player1ID);
    	 p1ScoreString = new HUDString("Score = " + p1Score);
    	 p1ScoreString.setName("P1Score");
    	 p1ScoreString.setLocation(0.01, 0.09);
    	 p1ScoreString.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
    	 p1ScoreString.setColor(Color.red);
    	 p1ScoreString.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
    	 camera1.addToHUD(p1ScoreString);
    	 p1TimeString = new HUDString("Time = " + time);
    	 p1TimeString.setName("P1Time");
    	 p1TimeString.setLocation(0.01, 0.03);
    	 p1TimeString.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
    	 p1TimeString.setColor(Color.red);
    	 p1TimeString.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
    	 camera1.addToHUD(p1TimeString);
    	 HUDString player2ID = new HUDString("Player2");
    	 player2ID.setName("Player2ID");
    	 player2ID.setLocation(0.01, 0.15);
    	 player2ID.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
    	 player2ID.setColor(Color.yellow);
    	 player2ID.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
    	 camera2.addToHUD(player2ID);
    	 p2ScoreString = new HUDString("Score = " + p2Score);
    	 p2ScoreString.setName("P2Score");
    	 p2ScoreString.setLocation(0.01, 0.09);
    	 p2ScoreString.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
    	 p2ScoreString.setColor(Color.yellow);
    	 p2ScoreString.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
    	 camera2.addToHUD(p2ScoreString);
    	 p2TimeString = new HUDString("Time = " + time);
    	 p2TimeString.setName("P2Time");
    	 p2TimeString.setLocation(0.01, 0.03);
    	 p2TimeString.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
    	 p2TimeString.setColor(Color.yellow);
    	 p2TimeString.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
    	 camera2.addToHUD(p2TimeString);
    }
    
    
    protected void render(){
	    renderer.setCamera(camera1);
	    super.render();
	    renderer.setCamera(camera2);
	    super.render();
    }
    
    @Override
    public void update(float elapsedTimeMS){ 
        if((p1Score + p2Score) < pyr.length){
            for (Leaf i : pyr) {
            	if(i.getWorldBound().intersects(player1.getWorldBound())){
                    numCrashes++;
                    CrashEvent newCrash = new CrashEvent(numCrashes);
                    eventMgr.triggerEvent(newCrash);
                    i.setLocalTranslation(matrix[(p1Score + p2Score)]);
                    p1Score++;
                    timeTemp = 5;
                }else if(i.getWorldBound().intersects(player2.getWorldBound())){
	                numCrashes++;
	                CrashEvent newCrash = new CrashEvent(numCrashes);
	                eventMgr.triggerEvent(newCrash);
	                i.setLocalTranslation(matrix[(p1Score + p2Score)]);
	                p2Score++;
	                timeTemp = 5;
	            }
            }
        }
        p1ScoreString.setText("Score = " + p1Score);
        p2ScoreString.setText("Score = " + p2Score);
        time += elapsedTimeMS;
        DecimalFormat df = new DecimalFormat("0.0");
        p1TimeString.setText("Time = " + df.format(time/1000));
        p2TimeString.setText("Time = " + df.format(time/1000));
        if(elapsedTimeMS > timeTemp){
            truck.setColorBuffer(truck.colorBuffer1);
        }
        cam1.update(elapsedTimeMS);
        cam2.update(elapsedTimeMS);
        super.update(elapsedTimeMS); 
    }

}
