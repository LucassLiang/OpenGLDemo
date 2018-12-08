package opengl.android.com.opengldemo.view.activity;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import opengl.android.com.opengldemo.R;
import opengl.android.com.opengldemo.databinding.ActivityMainBinding;
import opengl.android.com.opengldemo.presenter.MainPresenter;
import opengl.android.com.opengldemo.view.service.ChangeColorWallpaperService;
import opengl.android.com.opengldemo.view.service.FollowFingerWallpaperService;
import opengl.android.com.opengldemo.view.service.VideoWallpaperService;

/**
 * 主页
 * update by lucas on 2018/12/06.
 */
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;
    private MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mPresenter = new MainPresenter();
        mBinding.setPresenter(mPresenter);
    }

    /**
     * 切换可变背景颜色壁纸
     */
    public void clickOpenGLWallpaper(View view) {
        intentForWallPaper(ChangeColorWallpaperService.class);
    }

    /**
     * 切换跟随点击位置壁纸
     *
     * @param view
     */
    public void clickFollowFingerWallpaper(View view) {
        intentForWallPaper(FollowFingerWallpaperService.class);
    }

    /**
     * 视频壁纸
     *
     * @param view
     */
    public void clickVideoWallpaper(View view) {
        intentForWallPaper(VideoWallpaperService.class);
    }

    private void intentForWallPaper(Class wallpaperService) {
        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(this, wallpaperService));
        startActivity(intent);
    }
}
