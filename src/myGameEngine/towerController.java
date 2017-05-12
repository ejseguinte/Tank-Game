package myGameEngine;

import java.util.ArrayList;
import java.util.List;

public class towerController {
	List<NPC> list;
	public towerController(){
		 list = new ArrayList<>();
	}
	
	public void addTower(NPC npc){
		list.add(npc);
	}
	
	public void update(){
		for(NPC npc: list){
			npc.update();
		}
	}
}
