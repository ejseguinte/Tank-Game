/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a1;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import sage.event.IEventListener;
import sage.event.IGameEvent;
import sage.scene.TriMesh;
/**
 *
 * @author EJSeguinte
 */
public class MyTruck extends TriMesh  implements IEventListener{
    private static float[] vrts = new float[] {0,0,0,0,2,0,1,2,0,2,0,0,3,2,0,4,2,0,4,0,0,0,0,-4,0,2,-4,1,2,-4,2,0,-4,3,2,-4,4,0,-4,4,2,-4,0,0,-2,1,0,-2,3,0,-2,4,0,-2,0,2,-1,0,2,-3,4,2,-1,4,2,-3};
    private static float[] cl = new float[]  {1,1,1,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,1,0,1,1,1,0,1,1,1,0,1,1,1,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0};
    private static float[] cl2 = new float[] {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
    private static int[] triangles = new int[] {0,1,2,0,2,3,2,3,4,3,4,6,4,5,6,7,8,9,7,9,10,9,10,11,10,11,12,11,12,13,0,14,15,0,15,3,15,3,16,3,16,6,16,17,6,14,7,15,7,15,10,15,10,16,10,16,12,16,12,17,0,1,18,0,18,14,18,14,19,14,19,7,19,7,8,6,5,20,6,20,17,20,17,21,17,21,12,21,12,13};
    FloatBuffer colorBuffer1 = com.jogamp.common.nio.Buffers.newDirectFloatBuffer(cl);
    FloatBuffer colorBuffer2 = com.jogamp.common.nio.Buffers.newDirectFloatBuffer(cl2);
        
    public MyTruck(){ 
        FloatBuffer vertBuf = com.jogamp.common.nio.Buffers.newDirectFloatBuffer(vrts);
        IntBuffer triangleBuf = com.jogamp.common.nio.Buffers.newDirectIntBuffer(triangles);
        this.setVertexBuffer(vertBuf);
        this.setColorBuffer(colorBuffer1);
        this.setIndexBuffer(triangleBuf); 
    }
    
    @Override
    public boolean handleEvent(IGameEvent event){ 
        // if the event has programmer-defined information in it,
        // it must be cast to the programmer-defined event type.
        CrashEvent cevent = (CrashEvent) event;
        int crashCount = cevent.getWhichCrash();
        if (crashCount % 2 == 0) {
            this.setColorBuffer(colorBuffer1);
        }else {
            this.setColorBuffer(colorBuffer2);
        }
        return true;
    }
}
