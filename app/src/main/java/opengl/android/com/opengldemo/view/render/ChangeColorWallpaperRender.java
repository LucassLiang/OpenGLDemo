package opengl.android.com.opengldemo.view.render;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.WindowManager;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 颜色可变壁纸渲染器
 * update by lucas on 2018/12/06.
 */
public class ChangeColorWallpaperRender implements GLSurfaceView.Renderer {
    private final static float CHANGE_COLOR_SCALE = 0.5f;
    private float mColorSize = 0.0f;
    private float mOldX = 0.0f;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.8f, 0.8f, mColorSize, 1.0f);     //设置背景颜色
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
    }

    /**
     * 改变背景颜色
     *
     * @param event
     */
    public void changedBgColor(Context context, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                this.mOldX = event.getX();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                float dx = Math.abs(event.getX() - mOldX);
                float displayWidth = wm.getDefaultDisplay().getWidth();
                this.mColorSize = dx / (displayWidth * CHANGE_COLOR_SCALE);
                break;
            }
        }
    }

    /**
     * 观察壁纸Service被Destroy事件
     */
    public void onRelease() {

    }
}
