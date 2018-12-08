package opengl.android.com.opengldemo.view.render;

import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 视频壁纸渲染器
 * update by lucas on 2018/12/07.
 */
public class VideoWallpaperRender implements GLSurfaceView.Renderer {
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
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
     * 触碰事件
     *
     * @param event
     */
    public void onTouchEvent(MotionEvent event) {

    }

    /**
     * 观察Wallpaper服务destroy
     */
    public void onRelease() {

    }
}
