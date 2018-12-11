package opengl.android.com.opengldemo.view.service;

import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import opengl.android.com.opengldemo.view.render.DynamicBeamWallpaperRenderer;

/**
 * 动态光束壁纸
 * update by lucas on 2018/12/08.
 */
public class DynamicBeamWallpaperService extends GLWallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new DynamicBeamEngine();
    }

    protected class DynamicBeamEngine extends GLEngine {
        private DynamicBeamWallpaperRenderer mRender;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            mRender = new DynamicBeamWallpaperRenderer();
            setRender(mRender);
            setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            mRender.onTouchEvent(getApplication(), event);
        }

        @Override
        public void onDestroy() {
            if (mRender != null) {
                mRender.onRelease();
                mRender = null;
            }
            super.onDestroy();
        }
    }
}
