package opengl.android.com.opengldemo.view.service;

import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

/**
 * function:
 * update by lucas on 2018/12/06.
 */
public class GLWallpaperService extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new GLEngine();
    }

    public class GLEngine extends Engine {
        private GLThread mGLThread;
        private GLSurfaceView.EGLConfigChooser mConfigChooser;
        private GLSurfaceView.EGLContextFactory mContextFactory;
        private GLSurfaceView.EGLWindowSurfaceFactory mWindowSurfaceFactory;
        private GLSurfaceView.GLWrapper mWrapper;

        public GLEngine() {
            super();
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            mGLThread.surfaceCreated(holder);
            super.onSurfaceCreated(holder);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mGLThread.onWindowResize(width, height);
            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            mGLThread.surfaceDestroyed();
            super.onSurfaceDestroyed(holder);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                onResume();
            } else {
                onPause();
            }
        }

        /**
         * 设置Render
         */
        public void setRender(GLSurfaceView.Renderer render) {
            checkRenderExisted();
            if (mConfigChooser == null) {
                mConfigChooser = new BaseConfigChooser.SimpleConfigChooser(true);
            }
            if (mContextFactory == null) {
                mContextFactory = new DefaultContextFactory();
            }
            if (mWindowSurfaceFactory == null) {
                mWindowSurfaceFactory = new DefaultWindowSurfaceFactory();
            }
            mGLThread = new GLThread(render, mConfigChooser, mContextFactory, mWindowSurfaceFactory, mWrapper);
            mGLThread.start();
        }

        /**
         * 检查Thread是否已存在
         */
        private void checkRenderExisted() {
            if (mGLThread != null) {
                throw new IllegalStateException("setRenderer has already been called for this instance.");
            }
        }

        /**
         * 设置Render模式
         *
         * @param renderMode
         */
        public void setRenderMode(int renderMode) {
            this.mGLThread.setRenderMode(renderMode);
        }

        public void setConfigChooser(GLSurfaceView.EGLConfigChooser configChooser) {
            this.mConfigChooser = configChooser;
        }

        /**
         * set Wrapper
         *
         * @param wrapper
         */
        public void setWrapper(GLSurfaceView.GLWrapper wrapper) {
            this.mWrapper = wrapper;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mGLThread.requestExitAndWait();
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
        }

        private void onResume() {
            mGLThread.onResume();
        }

        private void onPause() {
            mGLThread.onPause();
        }
    }
}

class EglHelper {

    private EGL10 mEgl;
    private EGLDisplay mEglDisplay;
    private EGLSurface mEglSurface;
    private EGLContext mEglContext;
    EGLConfig mEglConfig;

    private GLSurfaceView.EGLConfigChooser mEGLConfigChooser;
    private GLSurfaceView.EGLContextFactory mEGLContextFactory;
    private GLSurfaceView.EGLWindowSurfaceFactory mEGLWindowSurfaceFactory;
    private GLSurfaceView.GLWrapper mGLWrapper;

    public EglHelper(GLSurfaceView.EGLConfigChooser chooser, GLSurfaceView.EGLContextFactory contextFactory,
                     GLSurfaceView.EGLWindowSurfaceFactory surfaceFactory, GLSurfaceView.GLWrapper wrapper) {
        this.mEGLConfigChooser = chooser;
        this.mEGLContextFactory = contextFactory;
        this.mEGLWindowSurfaceFactory = surfaceFactory;
        this.mGLWrapper = wrapper;
    }

    /**
     * 初始化Egl设置
     */
    public void start() {
        /*
         * 获取Egl单例
         */
        if (mEgl == null) {
            mEgl = (EGL10) EGLContext.getEGL();
        }

        /*
         * 获取Egl display
         */
        if (mEglDisplay == null) {
            mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        }

        /*
         * 初始化Egl display设置
         */
        if (mEglConfig == null) {
            int[] version = new int[2];
            mEgl.eglInitialize(mEglDisplay, version);
            mEglConfig = mEGLConfigChooser.chooseConfig(mEgl, mEglDisplay);
        }

        /*
         * 创建Egl context
         */
        if (mEglContext == null) {
            mEglContext = mEGLContextFactory.createContext(mEgl, mEglDisplay, mEglConfig);
            if (mEglContext == null || mEglContext == EGL10.EGL_NO_CONTEXT) {
                throw new RuntimeException("createContext failed");
            }
        }

        mEglSurface = null;
    }

    /**
     * 创建Surface
     *
     * @param holder
     * @return
     */
    public GL createSurface(SurfaceHolder holder) {
        if (mEglSurface != null && mEglSurface != EGL10.EGL_NO_SURFACE) {//判断Surface是否已存在
            /*
             * 移除当前Surface
             */
            mEgl.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            mEGLWindowSurfaceFactory.destroySurface(mEgl, mEglDisplay, mEglSurface);
        }

        /*
         * 创建新的Surface
         */
        mEglSurface = mEGLWindowSurfaceFactory.createWindowSurface(mEgl, mEglDisplay, mEglConfig, holder);

        if (mEglSurface == null || mEglSurface == EGL10.EGL_NO_SURFACE) {
            throw new RuntimeException("createWindowSurface failed");
        }

        /*
         * 判断Egl context是否已绑定到Surface
         */
        if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {
            throw new RuntimeException("eglMakeCurrent failed.");
        }

        GL gl = mEglContext.getGL();
        if (mGLWrapper != null) {
            gl = mGLWrapper.wrap(gl);
        }
        return gl;
    }

    /**
     * 显示Surface
     *
     * @return .
     */
    public boolean swap() {
        mEgl.eglSwapBuffers(mEglDisplay, mEglSurface);

        /*
         * Always check for EGL_CONTEXT_LOST, which means the context and all associated data were lost (For instance
         * because the device went to sleep). We need to sleep until we get a new surface.
         */
        return mEgl.eglGetError() != EGL11.EGL_CONTEXT_LOST;
    }

    public void destroySurface() {
        if (mEglSurface != null && mEglSurface != EGL10.EGL_NO_SURFACE) {
            mEgl.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            mEGLWindowSurfaceFactory.destroySurface(mEgl, mEglDisplay, mEglSurface);
            mEglSurface = null;
        }
    }

    public void finish() {
        if (mEglContext != null) {
            mEGLContextFactory.destroyContext(mEgl, mEglDisplay, mEglContext);
            mEglContext = null;
        }
        if (mEglDisplay != null) {
            mEgl.eglTerminate(mEglDisplay);
            mEglDisplay = null;
        }
    }
}


class GLThread extends Thread {
    private final static boolean LOG_THREADS = false;
    public final static int DEBUG_CHECK_GL_ERROR = 1;
    public final static int DEBUG_LOG_GL_CALLS = 2;

    private final GLThreadManager sGLThreadManager = new GLThreadManager();
    private GLThread mEglOwner;

    private GLSurfaceView.EGLConfigChooser mEGLConfigChooser;
    private GLSurfaceView.EGLContextFactory mEGLContextFactory;
    private GLSurfaceView.EGLWindowSurfaceFactory mEGLWindowSurfaceFactory;
    private GLSurfaceView.GLWrapper mGLWrapper;

    public SurfaceHolder mHolder;
    private boolean mSizeChanged = true;

    // Once the thread is started, all accesses to the following member
    // variables are protected by the sGLThreadManager monitor
    public boolean mDone;
    private boolean mPaused;
    private boolean mHasSurface;
    private boolean mWaitingForSurface;
    private boolean mHaveEgl;
    private int mWidth;
    private int mHeight;
    private int mRenderMode;
    private boolean mRequestRender;
    private boolean mEventsWaiting;
    // End of member variables protected by the sGLThreadManager monitor.

    private GLSurfaceView.Renderer mRenderer;
    private ArrayList<Runnable> mEventQueue = new ArrayList<Runnable>();
    private EglHelper mEglHelper;

    GLThread(GLSurfaceView.Renderer renderer, GLSurfaceView.EGLConfigChooser chooser, GLSurfaceView.EGLContextFactory contextFactory,
             GLSurfaceView.EGLWindowSurfaceFactory surfaceFactory, GLSurfaceView.GLWrapper wrapper) {
        super();
        mDone = false;
        mWidth = 0;
        mHeight = 0;
        mRequestRender = true;
        mRenderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY;
        mRenderer = renderer;
        this.mEGLConfigChooser = chooser;
        this.mEGLContextFactory = contextFactory;
        this.mEGLWindowSurfaceFactory = surfaceFactory;
        this.mGLWrapper = wrapper;
    }

    @Override
    public void run() {
        setName("GLThread " + getId());
        if (LOG_THREADS) {
            Log.i("GLThread", "starting tid=" + getId());
        }

        try {
            guardedRun();
        } catch (InterruptedException e) {
            // fall thru and exit normally
        } finally {
            sGLThreadManager.threadExiting(this);
        }
    }

    /*
     * This private method should only be called inside a synchronized(sGLThreadManager) block.
     */
    private void stopEglLocked() {
        if (mHaveEgl) {
            mHaveEgl = false;
            mEglHelper.destroySurface();
            sGLThreadManager.releaseEglSurface(this);
        }
    }

    private void guardedRun() throws InterruptedException {
        mEglHelper = new EglHelper(mEGLConfigChooser, mEGLContextFactory, mEGLWindowSurfaceFactory, mGLWrapper);
        try {
            GL10 gl = null;
            boolean tellRendererSurfaceCreated = true;
            boolean tellRendererSurfaceChanged = true;

            /*
             * This is our main activity thread's loop, we go until asked to quit.
             */
            while (!isDone()) {
                /*
                 * Update the asynchronous state (window size)
                 */
                int w = 0;
                int h = 0;
                boolean changed = false;
                boolean needStart = false;
                boolean eventsWaiting = false;

                synchronized (sGLThreadManager) {
                    while (true) {
                        // Manage acquiring and releasing the SurfaceView
                        // surface and the EGL surface.
                        if (mPaused) {
                            stopEglLocked();
                        }
                        if (!mHasSurface) {
                            if (!mWaitingForSurface) {
                                stopEglLocked();
                                mWaitingForSurface = true;
                                sGLThreadManager.notifyAll();
                            }
                        } else {
                            if (!mHaveEgl) {
                                if (sGLThreadManager.tryAcquireEglSurface(this)) {
                                    mHaveEgl = true;
                                    mEglHelper.start();
                                    mRequestRender = true;
                                    needStart = true;
                                }
                            }
                        }

                        // Check if we need to wait. If not, update any state
                        // that needs to be updated, copy any state that
                        // needs to be copied, and use "break" to exit the
                        // wait loop.

                        if (mDone) {
                            return;
                        }

                        if (mEventsWaiting) {
                            eventsWaiting = true;
                            mEventsWaiting = false;
                            break;
                        }

                        if ((!mPaused) && mHasSurface && mHaveEgl && (mWidth > 0) && (mHeight > 0)
                                && (mRequestRender || (mRenderMode == GLSurfaceView.RENDERMODE_CONTINUOUSLY))) {
                            changed = mSizeChanged;
                            w = mWidth;
                            h = mHeight;
                            mSizeChanged = false;
                            mRequestRender = false;
                            if (mHasSurface && mWaitingForSurface) {
                                changed = true;
                                mWaitingForSurface = false;
                                sGLThreadManager.notifyAll();
                            }
                            break;
                        }

                        // By design, this is the only place where we wait().

                        if (LOG_THREADS) {
                            Log.i("GLThread", "waiting tid=" + getId());
                        }
                        sGLThreadManager.wait();
                    }
                } // end of synchronized(sGLThreadManager)

                /*
                 * Handle queued events
                 */
                if (eventsWaiting) {
                    Runnable r;
                    while ((r = getEvent()) != null) {
                        r.run();
                        if (isDone()) {
                            return;
                        }
                    }
                    // Go back and see if we need to wait to render.
                    continue;
                }

                if (needStart) {
                    tellRendererSurfaceCreated = true;
                    changed = true;
                }
                if (changed) {
                    gl = (GL10) mEglHelper.createSurface(mHolder);
                    tellRendererSurfaceChanged = true;
                }
                if (tellRendererSurfaceCreated) {
                    mRenderer.onSurfaceCreated(gl, mEglHelper.mEglConfig);
                    tellRendererSurfaceCreated = false;
                }
                if (tellRendererSurfaceChanged) {
                    mRenderer.onSurfaceChanged(gl, w, h);
                    tellRendererSurfaceChanged = false;
                }
                if ((w > 0) && (h > 0)) {
                    /* draw a frame here */
                    mRenderer.onDrawFrame(gl);

                    /*
                     * Once we're done with GL, we need to call swapBuffers() to instruct the system to display the
                     * rendered frame
                     */
                    mEglHelper.swap();
                    Thread.sleep(10);
                }
            }
        } finally {
            /*
             * clean-up everything...
             */
            synchronized (sGLThreadManager) {
                stopEglLocked();
                mEglHelper.finish();
            }
        }
    }

    private boolean isDone() {
        synchronized (sGLThreadManager) {
            return mDone;
        }
    }

    public void setRenderMode(int renderMode) {
        if (!((GLSurfaceView.RENDERMODE_WHEN_DIRTY <= renderMode) && (renderMode <= GLSurfaceView.RENDERMODE_CONTINUOUSLY))) {
            throw new IllegalArgumentException("renderMode");
        }
        synchronized (sGLThreadManager) {
            mRenderMode = renderMode;
            if (renderMode == GLSurfaceView.RENDERMODE_CONTINUOUSLY) {
                sGLThreadManager.notifyAll();
            }
        }
    }

    public int getRenderMode() {
        synchronized (sGLThreadManager) {
            return mRenderMode;
        }
    }

    public void requestRender() {
        synchronized (sGLThreadManager) {
            mRequestRender = true;
            sGLThreadManager.notifyAll();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
        synchronized (sGLThreadManager) {
            if (LOG_THREADS) {
                Log.i("GLThread", "surfaceCreated tid=" + getId());
            }
            mHasSurface = true;
            sGLThreadManager.notifyAll();
        }
    }

    public void surfaceDestroyed() {
        synchronized (sGLThreadManager) {
            if (LOG_THREADS) {
                Log.i("GLThread", "surfaceDestroyed tid=" + getId());
            }
            mHasSurface = false;
            sGLThreadManager.notifyAll();
            while (!mWaitingForSurface && isAlive() && !mDone) {
                try {
                    sGLThreadManager.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void onPause() {
        synchronized (sGLThreadManager) {
            mPaused = true;
            sGLThreadManager.notifyAll();
        }
    }

    public void onResume() {
        synchronized (sGLThreadManager) {
            mPaused = false;
            mRequestRender = true;
            sGLThreadManager.notifyAll();
        }
    }

    public void onWindowResize(int w, int h) {
        synchronized (sGLThreadManager) {
            mWidth = w;
            mHeight = h;
            mSizeChanged = true;
            sGLThreadManager.notifyAll();
        }
    }

    public void requestExitAndWait() {
        // don't call this from GLThread thread or it is a guaranteed
        // deadlock!
        synchronized (sGLThreadManager) {
            mDone = true;
            sGLThreadManager.notifyAll();
        }
        try {
            join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Queue an "event" to be run on the GL rendering thread.
     *
     * @param r the runnable to be run on the GL rendering thread.
     */
    public void queueEvent(Runnable r) {
        synchronized (this) {
            mEventQueue.add(r);
            synchronized (sGLThreadManager) {
                mEventsWaiting = true;
                sGLThreadManager.notifyAll();
            }
        }
    }

    private Runnable getEvent() {
        synchronized (this) {
            if (mEventQueue.size() > 0) {
                return mEventQueue.remove(0);
            }

        }
        return null;
    }

    private class GLThreadManager {

        public synchronized void threadExiting(GLThread thread) {
            if (LOG_THREADS) {
                Log.i("GLThread", "exiting tid=" + thread.getId());
            }
            thread.mDone = true;
            if (mEglOwner == thread) {
                mEglOwner = null;
            }
            notifyAll();
        }

        /*
         * Tries once to acquire the right to use an EGL surface. Does not block.
         *
         * @return true if the right to use an EGL surface was acquired.
         */
        public synchronized boolean tryAcquireEglSurface(GLThread thread) {
            if (mEglOwner == thread || mEglOwner == null) {
                mEglOwner = thread;
                notifyAll();
                return true;
            }
            return false;
        }

        public synchronized void releaseEglSurface(GLThread thread) {
            if (mEglOwner == thread) {
                mEglOwner = null;
            }
            notifyAll();
        }
    }
}


abstract class BaseConfigChooser implements GLSurfaceView.EGLConfigChooser {
    protected int[] mConfigSpe;

    public BaseConfigChooser(int[] configSpe) {
        this.mConfigSpe = configSpe;
    }

    @Override
    public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
        int[] numConfigs = new int[1];
        egl.eglChooseConfig(display, mConfigSpe, null, 0, numConfigs);

        //获取系统可适配的设置数量
        int configNum = numConfigs[0];
        if (configNum <= 0) {
            throw new IllegalArgumentException("No config match");//没有可适配的配置
        }

        EGLConfig[] eglConfigs = new EGLConfig[configNum];
        egl.eglChooseConfig(display, mConfigSpe, eglConfigs, configNum, numConfigs);

        //获取可适配的设置
        EGLConfig config = chooseConfig(egl, display, eglConfigs);
        if (config == null) {
            throw new IllegalArgumentException("No config chosen");
        }
        return config;
    }

    abstract EGLConfig chooseConfig(EGL10 egl, EGLDisplay eglDisplay, EGLConfig[] eglConfigs);

    public static class ComponentSizeChooser extends BaseConfigChooser {
        private int[] mValue;
        protected int mRedSize;
        protected int mGreenSize;
        protected int mBlueSize;
        protected int mAlphaSize;
        protected int mDepthSize;
        protected int mStencilSize;

        public ComponentSizeChooser(int redSize, int blueSize, int greenSize, int alphaSize, int depthSize, int stencilSize) {
            super(new int[]{EGL10.EGL_RED_SIZE, redSize,
                    EGL10.EGL_BLUE_SIZE, blueSize,
                    EGL10.EGL_GREEN_SIZE, greenSize,
                    EGL10.EGL_ALPHA_SIZE, alphaSize,
                    EGL10.EGL_DEPTH_SIZE, depthSize,
                    EGL10.EGL_STENCIL_SIZE, stencilSize,
                    EGL10.EGL_NONE});
            this.mValue = new int[1];
            this.mRedSize = redSize;
            this.mBlueSize = blueSize;
            this.mGreenSize = greenSize;
            this.mAlphaSize = alphaSize;
            this.mDepthSize = depthSize;
            this.mStencilSize = stencilSize;
        }

        /**
         * 获取最适配设配的配置值
         *
         * @param egl
         * @param eglDisplay
         * @param eglConfigs
         * @return
         */
        @Override
        EGLConfig chooseConfig(EGL10 egl, EGLDisplay eglDisplay, EGLConfig[] eglConfigs) {
            EGLConfig closestConfig = null;
            int closestDistance = 1000;
            for (EGLConfig eglConfig : eglConfigs) {
                int depth = findConfigAttr(egl, eglDisplay, eglConfig, EGL10.EGL_DEPTH_SIZE, 0);
                int stencil = findConfigAttr(egl, eglDisplay, eglConfig, EGL10.EGL_STENCIL_SIZE, 0);
                if (depth >= mDepthSize && stencil >= mStencilSize) {
                    int red = findConfigAttr(egl, eglDisplay, eglConfig, EGL10.EGL_RED_SIZE, 0);
                    int green = findConfigAttr(egl, eglDisplay, eglConfig, EGL10.EGL_GREEN_SIZE, 0);
                    int blue = findConfigAttr(egl, eglDisplay, eglConfig, EGL10.EGL_BLUE_SIZE, 0);
                    int alpha = findConfigAttr(egl, eglDisplay, eglConfig, EGL10.EGL_ALPHA_SIZE, 0);
                    int distance = Math.abs(red - mRedSize)
                            + Math.abs(green - mGreenSize)
                            + Math.abs(blue - mBlueSize)
                            + Math.abs(alpha - mAlphaSize);
                    if (distance < closestDistance) {
                        closestConfig = eglConfig;
                        closestDistance = distance;
                    }
                }
            }
            return closestConfig;
        }

        /**
         * 获取设置值
         *
         * @param egl
         * @param eglDisplay
         * @param config
         * @param attribute
         * @param defaultValue
         * @return
         */
        private int findConfigAttr(EGL10 egl, EGLDisplay eglDisplay, EGLConfig config, int attribute, int defaultValue) {
            if (egl.eglGetConfigAttrib(eglDisplay, config, attribute, mValue)) {
                return mValue[0];
            }
            return defaultValue;
        }
    }

    /**
     * 一般配置选择
     * RGB_565且depth和stencil值为0
     */
    public static class SimpleConfigChooser extends ComponentSizeChooser {

        public SimpleConfigChooser(boolean hasDepth) {
            super(4, 4, 4, 0, hasDepth ? 16 : 0, 0);
            this.mRedSize = 5;
            this.mGreenSize = 6;
            this.mBlueSize = 5;
        }
    }
}

class DefaultContextFactory implements GLSurfaceView.EGLContextFactory {

    @Override
    public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
        return egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, null);
    }

    @Override
    public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
        egl.eglDestroyContext(display, context);
    }
}

class DefaultWindowSurfaceFactory implements GLSurfaceView.EGLWindowSurfaceFactory {

    @Override
    public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig config, Object nativeWindow) {
        EGLSurface surface = null;
        while (surface == null) {
            try {
                surface = egl.eglCreateWindowSurface(display, config, nativeWindow, null);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (surface == null) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return surface;
    }

    @Override
    public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {
        egl.eglDestroySurface(display, surface);
    }
}