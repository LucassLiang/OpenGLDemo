package opengl.android.com.opengldemo.view.render;

import android.content.Context;
import android.view.MotionEvent;
import android.view.WindowManager;

import javax.microedition.khronos.opengles.GL10;

/**
 * 颜色可变壁纸渲染器
 * update by lucas on 2018/12/06.
 */
public class ChangeColorWallpaperRenderer extends BaseGLRenderer {
    private final static float CHANGE_COLOR_SCALE = 0.5f;
    private float mColorSize = 0.0f;
    private float mOldX = 0.0f;

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        gl.glClearColor(0.8f, 0.8f, mColorSize, 1.0f);     //设置背景颜色
    }

    /**
     * 触碰事件
     *
     * @param event
     */
    public void onTouchEvent(Context context, MotionEvent event) {
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

    @Override
    public void onRelease() {

    }
}
