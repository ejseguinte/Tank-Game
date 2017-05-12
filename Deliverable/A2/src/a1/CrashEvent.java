/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a1;
import sage.event.*;
/**
 *
 * @author EJSeguinte
 */

public class CrashEvent extends AbstractGameEvent{
    // programmer-defined parts go here
    private int whichCrash;
    public CrashEvent(int n) { 
        whichCrash = n; 
    }
    
    public int getWhichCrash() { 
        return whichCrash;
    }
}
