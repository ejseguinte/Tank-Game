/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a1;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import myGameEngine.*;
import net.java.games.input.Controller;
import networking.GameClientTCP;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import sage.app.BaseGame;
import sage.audio.AudioManagerFactory;
import sage.audio.AudioResource;
import sage.audio.AudioResourceType;
import sage.audio.IAudioManager;
import sage.audio.Sound;
import sage.audio.SoundType;
import sage.camera.ICamera;
import sage.display.DisplaySettingsDialog;
import sage.display.DisplaySystem;
import sage.display.IDisplaySystem;
import sage.event.EventManager;
import sage.event.IEventManager;
import sage.input.IInputManager;
import sage.input.InputManager;
import sage.input.action.IAction;
import sage.model.loader.OBJLoader;
import sage.model.loader.ogreXML.OgreXMLParser;
import sage.networking.IGameConnection.ProtocolType;
import sage.physics.IPhysicsEngine;
import sage.physics.IPhysicsObject;
import sage.physics.PhysicsEngineFactory;
import sage.scene.Group;
import sage.scene.HUDString;
import sage.scene.Model3DTriMesh;
import sage.scene.SceneNode;
import sage.scene.SkyBox;
import sage.scene.shape.*;
import sage.scene.shape.Rectangle;
import sage.scene.state.RenderState;
import sage.scene.state.TextureState;
import sage.terrain.AbstractHeightMap;
import sage.terrain.ImageBasedHeightMap;
import sage.terrain.TerrainBlock;
import sage.texture.Texture;
import sage.texture.TextureManager;

/**
 *
 * @author EJSeguinte
 */
public class MyGame extends BaseGame{
	//Hud
	private int p1Score = 0;
    private float time = 0;
    private String playerName;
    private HUDString p1ScoreString;
    private HUDString p1TimeString;
    //Basic Game info
    private IEventManager eventMgr;
    private IDisplaySystem display;
    private SceneNode player1;
    private SceneNode tread;
    private ICamera camera1;
    private Camera3Pcontroller cam1;
    private IInputManager im;
    //Terrain
    private ImageBasedHeightMap myHeightMap;
    private float heightScale = 0.03f;
    private TerrainBlock myTerrainBlock;
    private TextureState myTerrainTextureState;
    private Texture myTerrainTexture;
    //Scripting
    ScriptEngine jsEngine;
    String scriptFileName;
    File ScriptFile;
    //Player
    private Group player;
    private float rot;
    private float topRot;
    //Skybox
    private SkyBox skybox;
    //Networking
    private String serverAddress;
    private int serverPort;
	private ProtocolType serverProtocol;
	private GameClientTCP thisClient;
    private boolean isConnected;
    private Group ghost;
    private SceneNode ghostTop;
    private SceneNode ghostTread;
    //Physics
    private IPhysicsEngine physicsEngine; 
    private IPhysicsObject physPlayer, terrain;
    private List<IPhysicsObject> towerBulletsP;
    private List<SceneNode> towerBullets;
    private List<IPhysicsObject> ghostBulletsP;
    private List<SceneNode> ghostBullets;
    private List<IPhysicsObject> bulletsP;
    private List<SceneNode> bullets;
    //AI Model
    private List<NPC> towerList;
    private Group towers;
    private towerController control;
    private float lastTickUpdateTime;
    //Loaded Textures
    private TextureState tankbot;
    private Texture tankbotTex;
    private TextureState tanktop;
    private Texture tanktopTex;
    //Animation
    Group flag;
    //Audio
    IAudioManager audioMgr;
    Sound tank, background;
    //Game States
    private boolean started;
    private boolean networked;
    private boolean gameOver;
    //Testing States
    private boolean sound;
    private boolean testingNetwork;
    private boolean testing;
    
    public MyGame(String serverAddr, int sPort){
    	super();
    	try {
			this.serverAddress = InetAddress.getLocalHost().getHostAddress(); //Should be updated to be input in Starter
		} catch (UnknownHostException e) {
			e.printStackTrace();
		};
    	this.serverPort = 6000;	//Should be updated to be input in Starter
    	this.serverProtocol = ProtocolType.TCP;
    	try{ 
    		thisClient = new GameClientTCP(InetAddress.getByName(serverAddress),serverPort, serverProtocol, this); 
		}catch (UnknownHostException e) { 
			e.printStackTrace(); 
		}catch (IOException e) {
			e.printStackTrace();
		}
    	if (thisClient != null){ 
    		thisClient.sendJoinMessage(); 
    	}
    }

    //Game Initialization
    protected void loadGameValues(){
    	started = (boolean) jsEngine.get("started");
    	networked = (boolean) jsEngine.get("networked");
    	gameOver = (boolean) jsEngine.get("gameOver");
    	sound = (boolean) jsEngine.get("sound");
    	testingNetwork = (boolean) jsEngine.get("testingNetwork");
    	testing = (boolean) jsEngine.get("testing");
    }
    
    @Override
    protected void initGame(){
    	display.setTitle("Tanks");
    	eventMgr = EventManager.getInstance();
        im = getInputManager();
        
		playerName = "Player 1";
		
		initPhysicsSystem();
        createScene();
        initTerrain();
        createPlayers();
        createSagePhysicsWorld();
        initInputs(); 
        initAudio();
    }
    
    @Override
    protected void initSystem(){
    	lastTickUpdateTime = 0;
    	initScriptEngine();
    	loadGameValues();
    	
    	DisplaySystem dis;
    	if(testing){
    		dis = (new DisplaySystem(800,600,32,60,false,"sage.renderer.jogl.JOGLRenderer"));
    	}else{
	    	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    	GraphicsDevice device = ge.getDefaultScreenDevice();
	    	DisplaySettingsDialog displayMenu = new DisplaySettingsDialog(device);
	    	displayMenu.showIt();
	    	DisplayMode mode = displayMenu.getSelectedDisplayMode();
	    	while(mode == null){
	    		displayMenu.showIt();
	    		mode = displayMenu.getSelectedDisplayMode();
	    	}
	    	dis = (new DisplaySystem(mode.getWidth(),mode.getHeight(),
	    			mode.getBitDepth(),mode.getRefreshRate(),displayMenu.isFullScreenModeSelected(),"sage.renderer.jogl.JOGLRenderer"));
    	}
    	
    	display = dis;
    	setDisplaySystem(display);
    	
    	IInputManager inputManager = new InputManager(); 
    	setInputManager(inputManager);
    	
    	ArrayList<SceneNode> gameWorld = new ArrayList<SceneNode>();
    	setGameWorld(gameWorld);
    }   
    
    protected void initPhysicsSystem(){ 
    	String engine = "sage.physics.ODE4J.ODE4JPhysicsEngine";
	    physicsEngine = PhysicsEngineFactory.createPhysicsEngine(engine);
	    physicsEngine.initSystem();
	    float[] gravity = {0, -1f, 0};
	    physicsEngine.setGravity(gravity);
    }
   
    
    private void initTerrain() {
    	 myHeightMap = new ImageBasedHeightMap("./Resources/Textures/TestHeightMap1.jpg");
    	 myTerrainBlock = createTerBlock(myHeightMap);
    	 myTerrainTexture = TextureManager.loadTexture2D("./Resources/Textures/TestGroundTexture.jpg");
    	 myTerrainTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
    	 myTerrainTextureState = (TextureState) 
    			 display.getRenderer().createRenderState(RenderState.RenderStateType.Texture);
    	 myTerrainTextureState.setTexture(myTerrainTexture,0);
    	 myTerrainTextureState.setEnabled(true);
    	 myTerrainBlock.setRenderState(myTerrainTextureState);
    	 addGameWorldObject(myTerrainBlock);
    }
    
    private void initInputs(){
    	String kbName;
    	String mouseName;
    	String gpName;
    	if(testing){
	        kbName = (String)jsEngine.get("keyboard");	
	        mouseName = (String)jsEngine.get("mouse");		
	        gpName = (String)jsEngine.get("gamepad");	
    	}else{
    		kbName = im.getKeyboardName();	
	        mouseName = im.getMouseName(); 	
	        gpName = im.getFirstGamepadName();
    	}
       
        cam1 = new 	Camera3Pcontroller(camera1,player1,im,mouseName);

        IAction quitGame = new QuitGameAction(this);
        SetSpeedAction setSpeed = new SetSpeedAction();
        ForwardAction mvForward = new ForwardAction(cam1, setSpeed, tread, player1, myTerrainBlock, this);
        BackwardAction mvBackward = new BackwardAction(cam1, setSpeed, tread, player1, myTerrainBlock, this);
        LeftAction mvLeft = new LeftAction(cam1, setSpeed, tread, player1, myTerrainBlock, this);
        RightAction mvRight = new RightAction(cam1, setSpeed, tread, player1, myTerrainBlock, this);
        ShootAction shoot = new ShootAction(towerBullets, towerBulletsP, camera1, player, physicsEngine,this);
//        DownPitchAction rtDown  = new DownPitchAction(cam1,setSpeed);
//        UpPitchAction rtUp = new UpPitchAction(cam1,setSpeed);
        RightYewAction rtRight = new RightYewAction(cam1,setSpeed, this);
        LeftYewAction rtLeft = new LeftYewAction(cam1,setSpeed, this);
//        LeftRollAction rlLeft = new LeftRollAction(camera1,setSpeed);
//        RightRollAction rlRight= new RightRollAction(camera1,setSpeed);
        
        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.ESCAPE, quitGame, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
//        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.LSHIFT, setSpeed, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE );
        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.W, mvForward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.S, mvBackward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.A, mvLeft, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.D, mvRight, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.E, shoot, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
//        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.UP, rtUp, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
//        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.DOWN, rtDown, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.LEFT, rtLeft, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.RIGHT, rtRight, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
//        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.Q, rlLeft, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
//        im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.E, rlRight, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        
        if( gpName != null){
	        IAction zAxisMove = new MoveZAxis(cam1, setSpeed, tread, player1, myTerrainBlock, this);
	        IAction xAxisMove = new MoveXAxis(cam1, setSpeed, tread, player1, myTerrainBlock, this);
	        IAction ryAxisMove = new MoveRYAxis(cam1, 0.05f, this);
	        IAction rxAxisMove = new MoveRXAxis(cam1, 0.05f, this);
//	        
//	        
//	        im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._8, quitGame, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
//	        im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._8, setSpeed, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
//	       
	        im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.Y, zAxisMove, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	        im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.X, xAxisMove, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	        im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.RY, ryAxisMove, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	        im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.RX, rxAxisMove, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
        }
    }
    
    private void initScriptEngine(){
    	ScriptEngineManager factory = new ScriptEngineManager();
    	scriptFileName = "./Resources/Scripts/GameStates.js";
    	ScriptFile = new File(scriptFileName);
    	jsEngine = factory.getEngineByName("js");
    	executeScript(jsEngine, scriptFileName);
    	
    }
    private void executeScript(ScriptEngine engine, String scriptFileName){
    	try
	    { 
    		FileReader fileReader = new FileReader(scriptFileName);
    		//execute the script statements in the file
		    engine.eval(fileReader);
		    fileReader.close(); 
	    }
	    catch (FileNotFoundException e1){
	    	System.out.println(scriptFileName + " not found " + e1); 
	    }catch (IOException e2){ 
	    	System.out.println("IO problem with " + scriptFileName + e2); 
    	} catch (ScriptException e3) {
    		System.out.println("ScriptException in " + scriptFileName + e3); 
	    } catch (NullPointerException e4){ 
	    	System.out.println ("Null Pointer Exception in " + scriptFileName + e4); 
	    }
    }
    
    public void initAudio() {
    	if(sound){
	    	AudioResource resource1, resource2;
	    	audioMgr = AudioManagerFactory.createAudioManager("sage.audio.joal.JOALAudioManager");
	        if(!audioMgr.initialize()){
	        	System.out.println("Audio Manager failed to initialize!");
	        	return;
	        }
	        
	        //Background is from https://www.freesound.org/people/ERH/sounds/30192/
	        //Shooting sound is from https://www.freesound.org/people/18hiltc/sounds/228611/	   
	        resource1 = audioMgr.createAudioResource("./Resources/Sounds/Background.wav",AudioResourceType.AUDIO_SAMPLE);
		    resource2 = audioMgr.createAudioResource("./Resources/Sounds/Shoot.wav", AudioResourceType.AUDIO_SAMPLE);
		    
		    background = new Sound(resource1, SoundType.SOUND_MUSIC, 100, true);
		    tank = new Sound(resource2, SoundType.SOUND_EFFECT, 100, false);
		    
		    
		    background.initialize(audioMgr);
		    tank.initialize(audioMgr);
		    
		    tank.setMaxDistance(30f);
		    tank.setMinDistance(0f);
		    
		    for(NPC tower: towerList) {
		    	tower.setAudio(audioMgr, resource2, sound);
		    }
		    
		    background.setLocation(new Point3D(player.getWorldTranslation().getCol(3)));
		    setEarParameters();
		    
		    background.play(100, true);
    	}
	    
    }
    
    public void setEarParameters() {
    	Matrix3D avDir = (Matrix3D) (player.getWorldRotation().clone());
		float camAz = cam1.getCameraAzimuth();
		avDir.rotateY(180.0f-camAz);
		Vector3D camDir = new Vector3D(0,0,1);
		camDir = camDir.mult(avDir);
		
		audioMgr.getEar().setLocation(camera1.getLocation());
		audioMgr.getEar().setOrientation(camDir, new Vector3D(0,1,0));
    }
    
    //Gameworld Creation
    private void createScene() {
    	
    	Group rootNode = new Group("RootNode");
    	skybox = new SkyBox("Sky", 20.0f, 20.0f, 20.0f); 
    	
    	//Textures from https://reije081.home.xs4all.nl/skyboxes/
    	Texture northTex = TextureManager.loadTexture2D("./Resources/Textures/North.bmp"); 
    	Texture southTex = TextureManager.loadTexture2D("./Resources/Textures/South.bmp");
    	Texture topTex   = TextureManager.loadTexture2D("./Resources/Textures/Top.bmp");
    	Texture westTex  = TextureManager.loadTexture2D("./Resources/Textures/East.bmp");
    	Texture eastTex  = TextureManager.loadTexture2D("./Resources/Textures/West.bmp");
    	
    	skybox.setTexture(SkyBox.Face.North, northTex); 
    	skybox.setTexture(SkyBox.Face.South, southTex);
    	skybox.setTexture(SkyBox.Face.Up, topTex);
    	skybox.setTexture(SkyBox.Face.East, eastTex); 
    	skybox.setTexture(SkyBox.Face.West,  westTex);
    	
    	rootNode.addChild(skybox);
    	addGameWorldObject(rootNode);
    	if(testing){
	        Group axis = new Group("Axis");
	        Point3D origin = new Point3D(0,0,0);
	        Point3D xEnd = new Point3D(10000000,0,0);
	        Point3D yEnd = new Point3D(0,10000000,0);
	        Point3D zEnd = new Point3D(0,0,10000000);
	        Line xAxis = new Line (origin, xEnd, Color.red, 2);
	        Line yAxis = new Line (origin, yEnd, Color.green, 2);
	        Line zAxis = new Line (origin, zEnd, Color.blue, 2);
	        axis.addChild(xAxis); 
	        axis.addChild(yAxis);
	        axis.addChild(zAxis);
	        addGameWorldObject(axis);
    	}
        
        super.update(0f);
    }
    
    private void createSagePhysicsWorld(){ 
    	//Set up bullet
    	towerBullets = new LinkedList<SceneNode>(); 
    	towerBulletsP = new LinkedList<IPhysicsObject>(); 
    	ghostBullets = new LinkedList<SceneNode>(); 
    	ghostBulletsP = new LinkedList<IPhysicsObject>(); 
    	bullets = new LinkedList<SceneNode>(); 
    	bulletsP = new LinkedList<IPhysicsObject>(); 
    	// add the Tank physics
	    float mass = 10.0f;
	    float[] size = {4,.5f,6};
	    physPlayer = physicsEngine.addBoxObject(physicsEngine.nextUID(),mass, player.getWorldTransform().getValues(),size);
	    player.setPhysicsObject(physPlayer);
	    player.updateGeometricState(1.0f, true);
	    
	    // add the ground plane physics
	    float up[] = {0f, 1f, 0}; // {0,1,0} is flat
	    terrain =
	    physicsEngine.addStaticPlaneObject(physicsEngine.nextUID(),
	    myTerrainBlock.getWorldTransform().getValues(), up, 0.0f);
	    //myTerrainBlock.setPhysicsObject(terrain);
    }
    
    //Creates bullet for Player
    public void createBullet(){
    	SceneNode bulletGraphic = new Sphere(1.0, 4,4,Color.BLUE);
    	if(sound){
    		tank.play(5, false);
    	}
    	Matrix3D mat = new Matrix3D();
    	mat.translate(getPlayerPosition().getX(), getPlayerPosition().getY() , getPlayerPosition().getZ());
    	bulletGraphic.setLocalTranslation(mat);
    	addGameWorldObject(bulletGraphic);
    	bulletGraphic.updateGeometricState(1.0f, true);
    	IPhysicsObject bullet = physicsEngine.addSphereObject(physicsEngine.nextUID(),1f, bulletGraphic.getWorldTransform().getValues(),1f);
    	bulletGraphic.setPhysicsObject(bullet);
    	bullets.add(bulletGraphic);
    	float[] direction = {(float)camera1.getViewDirection().getX()*100 ,0,(float)camera1.getViewDirection().getZ()*100};
    	bullet.setLinearVelocity(direction);
    	bullet.setSleepThresholds(0, 0);
    	bulletsP.add(bullet);
    	try {
    		if(thisClient != null)
    			thisClient.sendShootMessage(direction);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    //Create Bullets for AI
    public void createBullet(Vector3D loc,float x , float z){
    	SceneNode bulletGraphic = new Sphere(1.0, 4,4,Color.WHITE);
    	Matrix3D mat = new Matrix3D();
    	mat.translate(x, getPlayerPosition().getY() , z);
    	bulletGraphic.setLocalTranslation(mat);
    	addGameWorldObject(bulletGraphic);
    	bulletGraphic.updateGeometricState(1.0f, true);
    	IPhysicsObject bullet = physicsEngine.addSphereObject(physicsEngine.nextUID(),1f, bulletGraphic.getWorldTransform().getValues(),1f);
    	bulletGraphic.setPhysicsObject(bullet);
    	towerBullets.add(bulletGraphic);
    	float[] direction = {(float) (loc.getX())*1 ,0,(float) (loc.getZ())*1};
    	bullet.setLinearVelocity(direction);
    	towerBulletsP.add(bullet);
    }
    
  //Create Bullets for Ghost
    public void createBullet(Vector3D loc, float[] direction){
    	SceneNode bulletGraphic = new Sphere(1.0, 4,4,Color.RED);
    	Matrix3D mat = new Matrix3D();
    	mat.translate(loc.getX(), loc.getY() , loc.getZ());
    	bulletGraphic.setLocalTranslation(mat);
    	addGameWorldObject(bulletGraphic);
    	bulletGraphic.updateGeometricState(1.0f, true);
    	IPhysicsObject bullet = physicsEngine.addSphereObject(physicsEngine.nextUID(),1f, bulletGraphic.getWorldTransform().getValues(),1f);
    	bulletGraphic.setPhysicsObject(bullet);
    	ghostBullets.add(bulletGraphic);
    	bullet.setLinearVelocity(direction);
    	ghostBulletsP.add(bullet);
    }
    
    private void createPlayers(){
    	rot = 0;
    	topRot = 0;
    	
    	OBJLoader loader = new OBJLoader();
    	
    	//Load Bottom of Tank
    	tread = loader.loadModel("./Resources/Model/tankbot.obj");
    	tankbotTex = TextureManager.loadTexture2D("./Resources/Model/tankbot.png");
    	tankbotTex.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
    	tankbot =(TextureState) display.getRenderer().createRenderState(RenderState.RenderStateType.Texture);
    	tankbot.setTexture(tankbotTex,0);
    	tankbot.setEnabled(true);
    	tread.setRenderState(tankbot);
    	
    	//Load top of Tank
    	player1 = loader.loadModel("./Resources/Model/tanktop.obj");
    	tanktopTex = TextureManager.loadTexture2D("./Resources/Model/tanktop.png");
    	tanktopTex.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
    	tanktop =(TextureState) display.getRenderer().createRenderState(RenderState.RenderStateType.Texture);
    	tanktop.setTexture(tanktopTex,0);
    	tanktop.setEnabled(true);
    	player1.setRenderState(tanktop);
    	
    	//Create flags  
    	flag = createFlag();
    	

    	Iterator<SceneNode> itr = flag.getChildren();
    	while (itr.hasNext()){
    		Model3DTriMesh mesh = ((Model3DTriMesh)itr.next());
    		mesh.startAnimation("my_animation");
    	}
    	flag.scale(5, 5, 5);
    	flag.translate(100, 13, 50);
    	addGameWorldObject(flag);
    	
    	//Create player group
    	player = new Group("PlayerModel");
    	player1.translate(100, 0, 30);
	   	player1.rotate(-90, new Vector3D(0, 1, 0));
	   	player.addChild(player1);
	   	tread.scale(2, 1, 3);
	   	tread.translate(100, 0, 30);
	   	player.addChild(tread);
	   	addGameWorldObject(player);
	   	
	   	//Sets correct height for initialization
	   	Point3D avLoc = new Point3D(tread.getLocalTranslation().getCol(3));
    	float x = (float) avLoc.getX();
    	float z = (float) avLoc.getZ();
	   	float terHeight = myTerrainBlock.getHeight(x,z);
    	float desiredHeight = terHeight + (float)myTerrainBlock.getOrigin().getY()+1;
    	tread.getLocalTranslation().setElementAt(1, 3, desiredHeight);
    	player1.getLocalTranslation().setElementAt(1, 3, desiredHeight + 1.5f);
    	
    	player.updateLocalBound();
    	
   	 	camera1 = display.getRenderer().getCamera();
   	 	camera1.setPerspectiveFrustum(45, 1, 0.01, 1000);
   	 	createPlayerHUDs();
   	 	createBases();
   	 	super.update(0f);
    }
    
    private void createBases(){
    	towers = new Group();
    	towerList = new ArrayList<>();
    	towerList.add(new NPC(this, physicsEngine, 70 ,1,285));
    	towerList.add(new NPC(this, physicsEngine, 130,1,285));
    	towerList.add(new NPC(this, physicsEngine, 70 ,1,345));
    	towerList.add(new NPC(this, physicsEngine, 130,1,345));
    	
    	control = new towerController();
    	
    	for(NPC tower: towerList) {
    		towers.addChild(tower.getModel());
    		control.addTower(tower);
    	}

    	addGameWorldObject(towers);
    }
    
    private void createPlayerHUDs(){
   	 HUDString player1ID = new HUDString(playerName);
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
//   	 p1TimeString = new HUDString("Time = " + time);
//   	 p1TimeString.setName("P1Time");
//   	 p1TimeString.setLocation(0.01, 0.03);
//   	 p1TimeString.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO); //Turned off time
//   	 p1TimeString.setColor(Color.red);
//   	 p1TimeString.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
//   	 camera1.addToHUD(p1TimeString);
   }
    
    private TerrainBlock createTerBlock(AbstractHeightMap heightMap){
    	Vector3D terrainScale = new Vector3D(1.5, heightScale *5, 1.5);
    	int terrainSize = heightMap.getSize();
    	float cornerHeight =
    			 heightMap.getTrueHeightAtPoint(0, 0) * heightScale;
    	Point3D terrainOrigin = new Point3D(0, -cornerHeight, 0);
    	String name = "Terrain:" + heightMap.getClass().getSimpleName();
    	TerrainBlock tb = new TerrainBlock(name, terrainSize, terrainScale,
    			 heightMap.getHeightData(), terrainOrigin);
    	return tb;
    }
    
    private Group createFlag(){
    	Group model = null;
    	OgreXMLParser loader = new OgreXMLParser();
    	try{
	    	// Load an Ogre XML model consisting of geometry data, material/textures,
	    	model = loader.loadModel("./Resources/Model/Flag/plane.mesh.xml",
	    							 "./Resources/Model/Flag/plane.material",
	    							 "./Resources/Model/Flag/plane.skeleton.xml");
	    	model.updateGeometricState(0, true);
    	}catch (Exception e){
    		e.printStackTrace();
	    	System.exit(1);
    	}

    	return model;
    }
    
    //Player Methods
    public float getTopRot(){
    	return topRot;
    }
    
    public float setTopRot(float rotation){
    	topRot += rotation;
    	topRot = topRot % 360;
    	return topRot;
    }
    
    public float getBotRot(){
    	return rot;
    }
    
    public float setBotRot(float rotation){
    	rot += rotation;
    	return rot;
    }
    
    public SceneNode getPlayer(){
    	return tread;
    }
    
    //Networking  
    public void setIsConnected(boolean connected){
    	this.isConnected = connected;
    }
    
    public Vector3D getPlayerPosition(){
    	double x = player1.getLocalTranslation().getCol(3).getX();
    	double y = player1.getLocalTranslation().getCol(3).getY();
    	double z = player1.getLocalTranslation().getCol(3).getZ();
    	
    	return new Vector3D(x,y,z);
    }
    
    public void rotateGhostBottom(String id, float rotation){
    	ghostTread.rotate(rotation, new Vector3D(0,1,0));
    }
    
    public void rotateGhostTop(String id, float rotation){
    	ghostTop.rotate(rotation, new Vector3D(0,1,0));
    }
    
    public void addGhostPlayer(String id, Vector3D loc, float topRotation, float botRotation){
    	ghost = new Group();
    	
    	OBJLoader loader = new OBJLoader();
    	
    	//Load Bottom of Tank
    	ghostTread = loader.loadModel("./Resources/Model/tankbot.obj");
    	ghostTread.setRenderState(tankbot);
    	
    	//Load top of Tank
    	ghostTop = loader.loadModel("./Resources/Model/tanktop.obj");
    	ghostTop.setRenderState(tanktop); 	
    	
    	ghostTop.translate((float)loc.getX(), (float)loc.getY(), (float)loc.getZ());
    	ghostTop.rotate(-90, new Vector3D(0, 1, 0));
	   	ghost.addChild(ghostTop);
	   	ghostTread.scale(2, 1, 3);
	   	ghostTread.translate((float)loc.getX(), (float)loc.getY(), (float)loc.getZ());
	   	ghost.addChild(ghostTread);
	   	addGameWorldObject(ghost);
	   	
	   	//Sets correct height for initialization
	   	Point3D avLoc = new Point3D(ghostTread.getLocalTranslation().getCol(3));
    	float x = (float) avLoc.getX();
    	float z = (float) avLoc.getZ();
	   	float terHeight = myTerrainBlock.getHeight(x,z);
    	float desiredHeight = terHeight + (float)myTerrainBlock.getOrigin().getY()+1;
    	ghostTread.getLocalTranslation().setElementAt(1, 3, desiredHeight);
    	ghostTop.getLocalTranslation().setElementAt(1, 3, desiredHeight + 1.5f);
    	
    	ghost.updateLocalBound();
    	
    	rotateGhostBottom(id, botRotation);
    	rotateGhostTop(id, topRotation);

    }
    
    public void shootGhostPlayer(String id, float[] view){
    	if(ghost != null){
    		double x = ghostTread.getLocalTranslation().getCol(3).getX();
        	double y = ghostTread.getLocalTranslation().getCol(3).getY();
        	double z = ghostTread.getLocalTranslation().getCol(3).getZ();
        	Vector3D loc = new Vector3D(x,y,z);
        	
        	createBullet(loc, view);
    	}
    		
    }
    
    public void gotShot(){
    	 p1Score += 5;
    }
    
    public void moveGhostPlayer(String id, Vector3D loc, float topRotation, float botRotation){
    	if(ghost != null){
    		removeGameWorldObject(ghost);
    		addGhostPlayer(id, loc, topRotation, botRotation);
    	}
    		
    }
    
    public void removeGhostPlayer(String id){
    	removeGameWorldObject(ghost);
    }
    
    //Update function
    @Override
    public void update(float elapsedTimeMS){ 
    	long currentTime = System.nanoTime();
    	//Elapsed Time to check for NPC Shoot Action
		float elapsedTickMilliSecs = (currentTime-lastTickUpdateTime)/(1000000.0f);
		
		//Elapsed Time to check if collided
		float elapsedHitMilliSecs = (currentTime-lastTickUpdateTime)/(1000000.0f);
		
		//Move Skybox with Camera
    	Point3D camLoc = camera1.getLocation();
    	Matrix3D camTranslation = new Matrix3D(); 
    	camTranslation.translate(camLoc.getX(), camLoc.getY(), camLoc.getZ()); 
    	skybox.setLocalTranslation(camTranslation);
    	
    	//Networking
    	if (thisClient != null) {
    		thisClient.processPackets();
	    		try {
					thisClient.sendMoveMessage(getPlayerPosition());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	}
    	
    	//Update Physics
    	physicsEngine.update(20.0f);
    	 for (SceneNode s : getGameWorld()){ 
    		 if (s.getPhysicsObject() != null){ 
    			 Matrix3D mat = new Matrix3D(s.getPhysicsObject().getTransform());
    			 s.getLocalTranslation().setCol(3,mat.getCol(3));
    			 if(elapsedHitMilliSecs > 50f){
    				 elapsedHitMilliSecs = currentTime;
	    			 if(!bullets.contains(s) && s.getPhysicsObject() != player.getPhysicsObject() && s.getWorldBound().intersects(player.getWorldBound())){
	    				 p1Score -= 5;
	    				 //Checks to see if the bullet was from the ghost
	    				 if(ghost != null && ghostBullets != null){
		    				 if(ghostBullets.contains(s) && thisClient != null){
	    						try {
									thisClient.sendGotShotMessage();
								} catch (IOException e) {
									e.printStackTrace();
								}
		    				 }
	    				 }
	    			 }
    			 }
    		 } 
		 }
    	 
    	 //Update NPC Controller
    	 if(elapsedTickMilliSecs >= 600f){
    		 lastTickUpdateTime = currentTime;
    		 control.update();
    		 
    	 }
    	 
    	 //Check to see if there are too many bullet objects
    	 if(ghost != null){
    		 if(ghostBullets != null && thisClient != null ){
		    	 if(ghostBullets.size() >100){
		    		 physicsEngine.removeObject(ghostBulletsP.remove(0).getUID());
		    		 removeGameWorldObject(ghostBullets.remove(0));
		    	 }
    		 }
    	 }
    	 if(towerBullets.size() >100){
    		 physicsEngine.removeObject(towerBulletsP.remove(0).getUID());
    		 removeGameWorldObject(towerBullets.remove(0));
    	 }
    	 if(bullets.size() >100){
    		 physicsEngine.removeObject(bulletsP.remove(0).getUID());
    		 removeGameWorldObject(bullets.remove(0));
    	 }
    	 
    	 //Update HUD information
    	 p1ScoreString.setText("Score = " + p1Score);
         time += elapsedTimeMS;
//         DecimalFormat df = new DecimalFormat("0.0");
//         p1TimeString.setText("Time = " + df.format(time/1000));
         cam1.update(elapsedTimeMS);
         
         //Animation 
         Iterator<SceneNode> itr = flag.getChildren();
     	 while (itr.hasNext()){
     		Model3DTriMesh mesh = ((Model3DTriMesh)itr.next());
     		mesh.updateAnimation(elapsedTimeMS);
     	 } 
     	
         //Audio update
         if(sound){
	         tank.setLocation(new Point3D(player.getWorldTranslation().getCol(3))); 
	         setEarParameters();
         }
         
         super.update(elapsedTimeMS); 
    }
    
    //Extra
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
    
    @Override
    protected void shutdown(){
	    super.shutdown();
	    if(thisClient != null){ 
	    	try {
				thisClient.sendByeMessage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    try{
	    	if(thisClient != null) // shutdown() is inherited
	    		thisClient.shutdown();
	    } catch (IOException e) { 
	    	e.printStackTrace(); 
    	}
	    
	    if(sound){
		    background.release(audioMgr);
		    tank.release(audioMgr);
		    audioMgr.shutdown();
	    }
    }
}