package opengl.android.com.opengldemo.view.service;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.io.IOException;

import opengl.android.com.opengldemo.R;

/**
 * 视频壁纸
 * update by lucas on 2018/12/07.
 */
public class VideoWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new VideoEngine();
    }

    private class VideoEngine extends Engine {
        private MediaPlayer mMediaPlayer;

        public VideoEngine() {
            try {
                AssetFileDescriptor fd = getApplicationContext().getResources().openRawResourceFd(R.raw.test_video);
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
                mMediaPlayer.setLooping(true);
                mMediaPlayer.setScreenOnWhilePlaying(false);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            mMediaPlayer.setSurface(holder.getSurface());
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                mMediaPlayer.start();
            } else {
                mMediaPlayer.pause();
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
                mMediaPlayer.release();
            }
            super.onSurfaceDestroyed(holder);
        }
    }
}
