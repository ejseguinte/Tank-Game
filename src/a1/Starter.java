/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a1;

import java.util.Scanner;

/**
 *
 * @author EJSeguinte
 */
public class Starter {
    public static void main (String [] args){ 
//    	Scanner in = new Scanner(System.in);
//    	System.out.println("What is the Server Address:");
//    	String serverAddress = in.nextLine();
//    	System.out.println("What is the Server Port:");
//    	int port = in.nextInt();
      MyGame game = new MyGame("a",3); 
      game.start();
//        new MyGame().start();
    }
}
