package opengl.android.com.opengldemo.view.service;

import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import opengl.android.com.opengldemo.view.render.ChangeColorWallpaperRenderer;

/**
 * 可变颜色壁纸Service
 * update by lucas on 2018/12/06.
 */
public class ChangeColorWallpaperService extends GLWallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new ChangeColorEngine();
    }

    private class ChangeColorEngine extends GLEngine {
        private ChangeColorWallpaperRenderer mWallpaperRender;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            mWallpaperRender = new ChangeColorWallpaperRenderer();
            setRender(mWallpaperRender);
            setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        }

        @Override
        public void onDestroy() {
            if (mWallpaperRender != null) {
                mWallpaperRender.onRelease();
                mWallpaperRender = null;
            }
            super.onDestroy();
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            mWallpaperRender.onTouchEvent(getApplicationContext(), event);
        }
    }
}
