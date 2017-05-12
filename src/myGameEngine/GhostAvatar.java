package myGameEngine;

import java.util.UUID;

import graphicslib3D.Vector3D;

public class GhostAvatar {
	private String id;
	private Vector3D location;
	private float topRotation;
	private float bottomRotation;
	
	public GhostAvatar(UUID id, Vector3D location){
		this.id = id.toString();
		this.location = location;
	}

	public String getId() {
		return id;
	}

	public Vector3D getLocation() {
		return location;
	}
}
