package opengl.android.com.opengldemo.view.widget;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import opengl.android.com.opengldemo.utils.BufferUtils;

/**
 * 光束
 * update by lucas on 2018/12/08.
 */
public class Beam {
    private float mLineWidth = 10f;
    private FloatBuffer mVertexBuffer;
    private float[] mVertex = new float[]{
            0, 0, 0,
            0, 0.2f, 0,
    };
//    private FloatBuffer mVertexBuffer;
//    private float[] mVertex = new float[]{
//
//    };

    public Beam() {
        initBuffer();
    }

    /**
     * 初始化缓存
     */
    private void initBuffer() {
        mVertexBuffer = BufferUtils.createFloatBuffer(mVertex);
        mVertexBuffer.position(0);
    }

    public void draw(GL10 gl) {
        gl.glLineWidth(mLineWidth);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
        gl.glDrawArrays(GL10.GL_LINES, 0, 2);
    }
}
