package opengl.android.com.opengldemo.view.widget;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import opengl.android.com.opengldemo.utils.BufferUtils;

/**
 * 长方体
 * update by lucas on 2018/12/07.
 */
public class Cuboid {
    private FloatBuffer mVertexBuffer;
    private float[] mVertex = new float[]{
            0.5f, 0.5f, 0,      //font top right
            -0.5f, 0.5f, 0,     //font top left
            -0.5f, -0.5f, 0,    //font bottom left
            0.5f, -0.5f, 0,     //font bottom right
            0.5f, -0.5f, -0.5f, //back bottom right
            0.5f, 0.5f, -0.5f,  //back top right
            -0.5f, 0.5f, -0.5f, //back top left
            -0.5f, -0.5f, -0.5f,//back bottom left
    };

    private ShortBuffer mIndexBuff;
    private short[] mIndex = new short[]{
            0, 1, 2,//font
            0, 2, 3,

            3, 4, 5,//right
            3, 5, 0,

            0, 5, 6,//top
            0, 6, 1,

            1, 6, 7,//left
            1, 7, 2,

            2, 7, 4,//bottom
            2, 4, 3,

            7, 6, 5,//back
            7, 5, 4,
    };

    public Cuboid() {
        initBuff();
    }

    /**
     * 初始化缓冲
     */
    private void initBuff() {
        //为顶点缓存区配置空间
        mVertexBuffer = BufferUtils.createFloatBuffer(mVertex);
        mVertexBuffer.position(0);

        //为绘制顺序配置空间
        mIndexBuff = BufferUtils.createShortBuffer(mIndex);
        mIndexBuff.position(0);
    }

    public void draw(GL10 gl) {

        //画顶点
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);

        //作图
        gl.glDrawElements(GL10.GL_TRIANGLES, mIndex.length, GL10.GL_UNSIGNED_SHORT, mIndexBuff);
    }
}
