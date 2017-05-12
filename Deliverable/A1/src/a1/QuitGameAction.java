/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a1;

import net.java.games.input.Event;
import sage.app.AbstractGame;
import sage.input.action.AbstractInputAction;

    
/**
 *
 * @author EJSeguinte
 */
public class QuitGameAction extends AbstractInputAction {

    private AbstractGame game;
    public QuitGameAction(AbstractGame g){ // constructor
        this.game = g; 
    }
    // Sets the "game over" flag in the game associated with this
    // IAction to true. The time and event parameters are ignored.
    public void performAction(float time, Event event){ 
        game.setGameOver(true); 
    }
}

