package myGameEngine;

import java.util.Random;

import a1.MyGame;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.audio.AudioManager;
import sage.audio.AudioResource;
import sage.audio.IAudioManager;
import sage.audio.Sound;
import sage.audio.SoundType;
import sage.camera.ICamera;
import sage.physics.IPhysicsEngine;
import sage.physics.IPhysicsObject;
import sage.scene.SceneNode;
import sage.scene.shape.Cube;

public class NPC{
	private MyGame game;
	private SceneNode model;
	private Sound tower;
	private boolean soundOn;
	private IPhysicsEngine physicsEngine;
	private ICamera cam;
	private float x;
	private float z;
	
	public NPC(MyGame game, IPhysicsEngine physicsEngine, float x, float y, float z){
		this.game = game;
		model = new Cube();
		model.translate(x, y, z);
		model.scale(3, 10, 3);
		this.x = x;
		this.z = z;
	}
	
	public void setAudio(IAudioManager audioMgr, AudioResource sound, boolean soundOn) {
		this.soundOn = soundOn;
		if(soundOn){
			tower = new Sound(sound, SoundType.SOUND_EFFECT, 100, true);
			tower.initialize(audioMgr);
			tower.setMaxDistance(30f);
		    tower.setMinDistance(3f);
		    tower.setRollOff(5f);
		    tower.setLocation(new Point3D(model.getWorldTranslation().getCol(3)));
		}
	}
	
	public SceneNode getModel(){
		return model;
	}
	
	public void releaseAudio(IAudioManager audioMgr) {
		tower.release(audioMgr);
	}
	
	public void update(){
		Vector3D loc = game.getPlayerPosition();
		float targetX = (float) (loc.getX() - x);
		float targetZ = (float) (loc.getZ() - z);
		
		if(Math.abs(targetX) < 30 || Math.abs(targetZ) < 30 ){
			if(soundOn){
				tower.play(5, false);
			}
			Random r = new Random();
			boolean neg = r.nextBoolean();
			float negative = 1;
			if(neg){
				negative = -1;
			}
			game.createBullet(new Vector3D(targetX + r.nextInt(10) * negative, 0, targetZ + r.nextInt(10) * negative), x, z);
			
		}
	}
}
