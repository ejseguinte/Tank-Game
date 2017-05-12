/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a1;

import graphicslib3D.Matrix3D;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import sage.scene.TriMesh;
/**
 *
 * @author EJSeguinte
 */
public class MyPyramid extends TriMesh{
    private static float[] vrts = new float[] {0,1,0,-1,-1,1,1,-1,1,1,-1,-1,-1,-1,-1};
    private static float[] cl = new float[] {1,0,0,1,0,1,0,1,0,0,1,1,1,1,0,1,1,0,1,1};
    private static float[] cl2 = new float[] {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
    private static int[] triangles = new int[] {0,1,2,0,2,3,0,3,4,0,4,1,1,4,2,4,3,2};
    FloatBuffer colorBuffer1 = com.jogamp.common.nio.Buffers.newDirectFloatBuffer(cl);
    FloatBuffer colorBuffer2 = com.jogamp.common.nio.Buffers.newDirectFloatBuffer(cl2);
    Matrix3D matrix;
    public MyPyramid(){ 
        FloatBuffer vertBuf = com.jogamp.common.nio.Buffers.newDirectFloatBuffer(vrts);
        IntBuffer triangleBuf = com.jogamp.common.nio.Buffers.newDirectIntBuffer(triangles);
        this.setVertexBuffer(vertBuf);
        this.setColorBuffer(colorBuffer1);
        this.setIndexBuffer(triangleBuf); 
        matrix = new Matrix3D();
    }
    
    public Matrix3D getMatrix(){
        return matrix;
    }
    
    public void setMatrix(float x, float y, float z){
        matrix.translate(x, y, z);
    }
}
