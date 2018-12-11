package opengl.android.com.opengldemo.view.render;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * GLRender 基类
 * update by lucas on 2018/12/08.
 */
public abstract class BaseGLRenderer implements GLSurfaceView.Renderer {
    private long mLastFrameTimeMs;
    private float[] mMatrix = new float[16];

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glDisable(GL10.GL_DITHER); //禁用抖动
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glShadeModel(GL10.GL_SMOOTH);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        countOrthographicProjectionMatrix(width, height);
    }

    /**
     * 计算正交投影矩阵
     *
     * @param width
     * @param height
     */
    private void countOrthographicProjectionMatrix(int width, int height) {
        final float ratio;
        if (width > height) {
            ratio = (float) width / (float) height;
            Matrix.orthoM(mMatrix, 0, -ratio, ratio, -1, 1, -1f, 1f);
        } else {
            ratio = (float) height / (float) width;
            Matrix.orthoM(mMatrix, 0, -1, 1, -ratio, ratio, -1f, 1f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        limitFrameRate(30);//限制30帧
        gl.glTranslatef(0, 0, -5);//重置坐标位置
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glLoadMatrixf(mMatrix, 0);//等比例缩放屏幕尺寸
    }

    /**
     * 限制每秒帧数
     * 设置较低的帧数，以降低功耗与性能消耗
     *
     * @param framePerSecond
     */
    private void limitFrameRate(int framePerSecond) {
        long elapsedFrameTimeMs = SystemClock.elapsedRealtime() - mLastFrameTimeMs;
        long expectFrameTimeMs = 1000 / framePerSecond;//每一帧所消耗时间
        long sleepTimeMs = expectFrameTimeMs - elapsedFrameTimeMs;
        if (sleepTimeMs > 0) {
            SystemClock.sleep(sleepTimeMs);
        }
        mLastFrameTimeMs = SystemClock.elapsedRealtime();
    }

    /**
     * release Renderer
     */
    public abstract void onRelease();
}
