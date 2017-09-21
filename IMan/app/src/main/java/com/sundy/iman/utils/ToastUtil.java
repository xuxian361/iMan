package com.sundy.iman.utils;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * 用于显示Toast
 * 放在Application里面
 * Created by sundy on 17/9/21.
 */

public class ToastUtil {

    private Toast toast;
    private Context context;

    public ToastUtil(Context context) {
        this.context = context;
    }

    /**
     * 显示一个Toast
     *
     * @param text 需要显示的字符串
     */
    public void showToast(String text, int time) {
        if (toast == null) {
            toast = Toast.makeText(context, text, time);
        } else {
            toast.setText(text);
        }
        toast.show();
    }

    public void showToast(String text) {
        showToast(text, Toast.LENGTH_SHORT);
    }

    public void showToast(@StringRes int res) {
        showToast(res, Toast.LENGTH_SHORT);
    }

    /**
     * 显示一个Toast
     *
     * @param strRes 需要显示的文本资源
     * @throws Resources.NotFoundException
     */
    public void showToast(int strRes, int time) throws Resources.NotFoundException {
        if (toast == null) {
            toast = Toast.makeText(context, strRes, time);
        } else {
            toast.setText(strRes);
        }
        toast.show();
    }
}
