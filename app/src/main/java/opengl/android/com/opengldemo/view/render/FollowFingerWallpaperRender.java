package opengl.android.com.opengldemo.view.render;

import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import opengl.android.com.opengldemo.view.widget.Cuboid;
import opengl.android.com.opengldemo.view.widget.RainbowColor;

/**
 * 跟随触控位置壁纸渲染器
 * update by lucas on 2018/12/07.
 */
public class FollowFingerWallpaperRender implements GLSurfaceView.Renderer {
    private final static float ROTATE_SCALE = 0.2f;
    private final static float AUTO_ROTATE_ANGLE = 0.5f;
    private Cuboid mCuboid;
    private RainbowColor mRainbow;

    private float mAngleX = 0;//x轴旋转的角度
    private float mAngleY = 0;//y轴旋转的角度
    private float mOldX = 0;
    private float mOldY = 0;

    public FollowFingerWallpaperRender() {
        this.mCuboid = new Cuboid();
        this.mRainbow = new RainbowColor();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);//背景颜色
        gl.glDisable(GL10.GL_DITHER); //禁用抖动
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        //旋转
        gl.glRotatef(mAngleX, 1.0f, 0, 0);
        gl.glRotatef(mAngleY, 0, 1.0f, 0);

        gl.glFrontFace(GL10.GL_CCW);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glCullFace(GL10.GL_BACK);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        mRainbow.draw(gl);
        mCuboid.draw(gl);

        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisable(GL10.GL_CULL_FACE);
        gl.glTranslatef(0.0f, 0.0f, -5f);

        mAngleX += AUTO_ROTATE_ANGLE;
        mAngleY += AUTO_ROTATE_ANGLE;
    }

    /**
     * 触碰事件
     *
     * @param event
     */
    public void onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE: {
                float dx = x - mOldX;
                float dy = y - mOldY;

                mAngleX += dy * ROTATE_SCALE;
                mAngleY += dx * ROTATE_SCALE;
                break;
            }
        }
        mOldX = x;
        mOldY = y;
    }

    /**
     * 观察WallPaper服务destroy时
     */
    public void onRelease() {

    }
}
