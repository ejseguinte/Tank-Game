package myGameEngine;

import java.util.List;

import a1.MyGame;
import graphicslib3D.Matrix3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;
import sage.physics.IPhysicsEngine;
import sage.physics.IPhysicsObject;
import sage.scene.SceneNode;
import sage.scene.shape.Cube;

public class ShootAction extends AbstractInputAction{
	private MyGame game;
	private float lastTime;
	public ShootAction(List<SceneNode> bullets, List<IPhysicsObject> bulletsP, ICamera camera1, SceneNode player, IPhysicsEngine physicsEngine, MyGame game){
		this.game = game;
	}
	@Override
	public void performAction(float time, Event evt) {
		long currentTime = System.nanoTime();
		float elapsedShootMilliSecs = (currentTime-lastTime)/(1000000.0f);
		if(elapsedShootMilliSecs > 250){
			game.createBullet();
			lastTime = currentTime;
		}
	}

}
