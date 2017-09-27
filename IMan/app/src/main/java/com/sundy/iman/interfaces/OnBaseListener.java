package com.sundy.iman.interfaces;

import android.support.v4.app.Fragment;

/**
 * Created by sundy on 17/9/26.
 */

public interface OnBaseListener {

    void switchContent(Fragment fragment, int id);

    void addContent(Fragment fragment, int id);

    void onBack();

}
