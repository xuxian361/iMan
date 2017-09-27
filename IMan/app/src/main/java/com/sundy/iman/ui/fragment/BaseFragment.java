package com.sundy.iman.ui.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.orhanobut.logger.Logger;
import com.sundy.iman.interfaces.OnBaseListener;

/**
 * Created by sundy on 17/9/14.
 */

public class BaseFragment extends Fragment implements OnBaseListener {

    protected OnBaseListener mCallback;
    protected FragmentActivity mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnBaseListener) (mContext = (FragmentActivity) context);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void switchContent(Fragment fragment, int id) {
        Logger.i("------------->switchContent");
        try {
            if (fragment == null) {
                return;
            } else {
                if (fragment.isAdded()) {
                    getActivity().getSupportFragmentManager().beginTransaction().show(fragment).commit();
                } else {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(id, fragment)
                            .commitAllowingStateLoss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addContent(Fragment fragment, int id) {
        Logger.i("------------->addContent");
        try {
            if (fragment == null) {
                return;
            } else {
                if (fragment.isAdded()) {
                    getActivity().getSupportFragmentManager().beginTransaction().show(fragment).commit();
                } else {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .add(id, fragment)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBack() {
        Logger.i("-------->onBack");
        try {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            int count = manager.getBackStackEntryCount();
            if (count > 0) {
                manager.popBackStack();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
