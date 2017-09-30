package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.utils.DeviceUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.bgabanner.BGABanner;

/**
 * 引导页
 * Created by sundy on 17/9/21.
 */

public class GuideActivity extends BaseActivity {

    @BindView(R.id.banner_guide_background)
    BGABanner mBackgroundBanner;
    @BindView(R.id.banner_guide_foreground)
    BGABanner mForegroundBanner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_guide);
        ButterKnife.bind(this);


        setListener();
        initData();
    }

    private void setListener() {
        /**
         * 设置进入按钮和跳过按钮控件资源 id 及其点击事件
         * 如果进入按钮和跳过按钮有一个不存在的话就传 0
         * 在 BGABanner 里已经帮开发者处理了防止重复点击事件
         * 在 BGABanner 里已经帮开发者处理了「跳过按钮」和「进入按钮」的显示与隐藏
         */
        mForegroundBanner.setEnterSkipViewIdAndDelegate(R.id.btn_guide_enter, R.id.tv_guide_skip, new BGABanner.GuideDelegate() {
            @Override
            public void onClickEnterOrSkip() {
                goMain();
            }
        });
    }

    private void initData() {
        // 设置数据源
        mBackgroundBanner.setData(R.mipmap.iman_guide_background_1,
                R.mipmap.iman_guide_background_2,
                R.mipmap.iman_guide_background_3);

        String lang = DeviceUtils.getLocaleLanguage();
        if (lang.startsWith("en")) { //英文
            PaperUtils.setLanguage(Constants.LANG_EN);
            mForegroundBanner.setData(R.mipmap.iman_guide_foreground_1,
                    R.mipmap.iman_guide_foreground_2,
                    R.mipmap.iman_guide_foreground_3);
        } else if (lang.startsWith("zh")) { //中文简体/繁体
            if (lang.equals("zh-HK")) { //繁体
                PaperUtils.setLanguage(Constants.LANG_TC);
                mForegroundBanner.setData(R.mipmap.iman_guide_foreground_1,
                        R.mipmap.iman_guide_foreground_2,
                        R.mipmap.iman_guide_foreground_3);
            } else { //简体
                PaperUtils.setLanguage(Constants.LANG_SC);
                mForegroundBanner.setData(R.mipmap.iman_guide_foreground_1,
                        R.mipmap.iman_guide_foreground_2,
                        R.mipmap.iman_guide_foreground_3);
            }
        } else { //英文
            PaperUtils.setLanguage(Constants.LANG_EN);
            mForegroundBanner.setData(R.mipmap.iman_guide_foreground_1,
                    R.mipmap.iman_guide_foreground_2,
                    R.mipmap.iman_guide_foreground_3);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 如果开发者的引导页主题是透明的，需要在界面可见时给背景 Banner 设置一个白色背景，避免滑动过程中两个 Banner 都设置透明度后能看到 Launcher
        mBackgroundBanner.setBackgroundResource(android.R.color.white);
    }

    //跳转主页
    private void goMain() {
        UIHelper.jump(this, MainActivity.class);
        finish();
    }
}
