package opengl.android.com.opengldemo.view.widget;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * 彩虹色
 * update by lucas on 2018/12/07.
 */
public class RainbowColor {
    private FloatBuffer mColorBuffer;
    private float[] mColors = new float[]{
            1.0f, 0.0f, 0.0f, 1.0f,//红
            1.0f, 0.5f, 0.0f, 1.0f,//橙
            1.0f, 1.0f, 0.0f, 1.0f,//黄
            0.0f, 1.0f, 0.0f, 1.0f,//绿
            0.0f, 0.0f, 1.0f, 1.0f,//蓝
            0.0f, 1.0f, 1.0f, 1.0f,//青
            1.0f, 0.0f, 1.0f, 1.0f,//紫
    };

    public RainbowColor() {
        initBuff();
    }

    /**
     * 初始化缓存
     */
    private void initBuff() {
        //为颜色缓存区配置空间
        mColorBuffer = ByteBuffer.allocateDirect(mColors.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(mColors);
        mColorBuffer.position(0);
    }

    public void draw(GL10 gl) {
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
    }
}
