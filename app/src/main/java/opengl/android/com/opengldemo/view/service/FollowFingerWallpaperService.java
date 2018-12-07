package opengl.android.com.opengldemo.view.service;

import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import opengl.android.com.opengldemo.view.render.FollowFingerWallpaperRender;

/**
 * 跟随触控位置壁纸
 * update by lucas on 2018/12/07.
 */
public class FollowFingerWallpaperService extends GLWallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new FollowFingerEngine();
    }

    private class FollowFingerEngine extends GLEngine {
        private FollowFingerWallpaperRender mRender;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            mRender = new FollowFingerWallpaperRender();
            setRender(mRender);
            setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            mRender.onTouchEvent(event);
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
