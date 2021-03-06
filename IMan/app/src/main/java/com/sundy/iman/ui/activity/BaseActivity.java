package com.sundy.iman.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.sundy.iman.R;
import com.sundy.iman.utils.SizeUtils;
import com.sundy.iman.view.dialog.FlipProgressDialog;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sundy on 17/9/14.
 */

public class BaseActivity extends AppCompatActivity {

    protected FlipProgressDialog slow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        initProgressDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * 退出APP
     */
    public void exitApp() {
        MobclickAgent.onKillProcess(this);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initProgressDialog() {
        List<Integer> imageList = new ArrayList<Integer>();
        imageList.add(R.drawable.ic_directions_bike_white);
        imageList.add(R.drawable.ic_directions_bus_white);
        imageList.add(R.drawable.ic_directions_car_white);
        imageList.add(R.drawable.ic_directions_subway_white);
        imageList.add(R.drawable.ic_flight_white);

        slow = new FlipProgressDialog();
        slow.setImageList(imageList);
        slow.setOrientation("rotationY");
        slow.setDuration(500);
        slow.setBackgroundColor(ContextCompat.getColor(this, R.color.main_green_light));
        slow.setImageSize(SizeUtils.dp2px(this, 36));
    }

    /**
     * 显示Progress
     */
    public void showProgress() {
        if (slow != null)
            slow.show(getFragmentManager(), "");
    }

    /**
     * 隐藏Progress
     */
    public void hideProgress() {
        if (slow != null) {
            slow.dismissAllowingStateLoss();
        }
    }

    //自动弹出键盘
    public void autoPopupSoftInput(final View view) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {
                               InputMethodManager inputManager =
                                       (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(view, 0);
                           }
                       },
                500);
    }

    //关闭键盘
    public void closeKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}
