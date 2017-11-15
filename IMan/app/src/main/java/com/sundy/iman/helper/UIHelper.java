package com.sundy.iman.helper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.sundy.iman.ui.activity.LoginType2Activity;

/**
 * Created by sundy on 17/9/21.
 */

public class UIHelper {

    public static void jump(Activity context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        context.startActivity(intent);
    }

    public static void jump(Activity context, Class clazz, Bundle bundle) {
        Intent intent = new Intent(context, clazz);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void jumpForResult(Activity context, Class clazz, int requestCode) {
        Intent intent = new Intent(context, clazz);
        context.startActivityForResult(intent, requestCode);
    }

    public static void jumpForResult(Activity context, Class clazz, Bundle bundle, int requestCode) {
        Intent intent = new Intent(context, clazz);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, requestCode);
    }

    public static void jumpForResultByFragment(Fragment fragment, Class clazz, Bundle bundle, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), clazz);
        intent.putExtras(bundle);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void jumpForResultByFragment(Fragment fragment, Class clazz, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), clazz);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void setResultBack(Activity context, Class clazz, Bundle bundle, int resultCode) {
        Intent intent = new Intent(context, clazz);
        intent.putExtras(bundle);
        context.setResult(resultCode, intent);
        context.finish();
    }

    //打电话
    public static void callPhone(Activity context, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNum));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    //Login 登陆
    public static void login(Activity context) {
//        jump(context, LoginActivity.class);
        jump(context, LoginType2Activity.class);
    }


}
