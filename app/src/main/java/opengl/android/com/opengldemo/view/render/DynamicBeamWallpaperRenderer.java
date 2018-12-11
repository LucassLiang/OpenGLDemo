package opengl.android.com.opengldemo.view.render;

import android.content.Context;
import android.view.MotionEvent;
import android.view.WindowManager;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import opengl.android.com.opengldemo.view.widget.Beam;
import opengl.android.com.opengldemo.view.widget.RainbowColor;

/**
 * 动态光束壁纸渲染器
 * update by lucas on 2018/12/07.
 */
public class DynamicBeamWallpaperRenderer extends BaseGLRenderer {
    private Beam mBeam;
    private RainbowColor mRainbow;
    private float mX;
    private float mY;

    public DynamicBeamWallpaperRenderer() {
        this.mBeam = new Beam();
        this.mRainbow = new RainbowColor();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glEnable(GL10.GL_DIFFUSE);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        gl.glTranslatef(mX, mY, 0);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

//        mRainbow.draw(gl);
//        mBeam.draw(gl);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    }

    /**
     * 触碰事件
     *
     * @param context
     * @param event
     */
    public void onTouchEvent(Context context, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN: {
                updateCurrentCoordinate(context, x, y);
                break;
            }
        }
    }

    /**
     * 更新坐标位置
     * 根据屏幕位置重新计算坐标的相对位置
     *
     * @param context
     * @param x
     * @param y
     */
    private void updateCurrentCoordinate(Context context, float x, float y) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        float windowWidth = wm.getDefaultDisplay().getWidth();
        float windowHeight = wm.getDefaultDisplay().getHeight();
        mX = x / (windowWidth / 2) - 1;
        mY = 1 - y / (windowHeight / 2);
    }

    @Override
    public void onRelease() {

    }
}
