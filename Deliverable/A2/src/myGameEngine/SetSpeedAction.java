/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myGameEngine;

import net.java.games.input.Event;
import sage.input.action.AbstractInputAction;

/**
 *
 * @author EJSeguinte
 */
public class SetSpeedAction extends AbstractInputAction { 
    private boolean running = false;
    
    public boolean isRunning() { return running; }
    
    public void performAction(float time, Event event){ 
        System.out.println("changed"); running = !running; 
    }
}
