package com.sundy.iman.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.entity.TabEntity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by sundy on 17/9/27.
 */

public class MainFragment extends BaseFragment {

    @BindView(R.id.frameMain)
    FrameLayout frameMain;
    @BindView(R.id.view_menu)
    CommonTabLayout viewMenu;
    Unbinder unbinder;
    private String[] mTitles = {"Message", "Me"};
    private int[] mIconUnSelectIds = {
            R.mipmap.tab_speech_unselect,
            R.mipmap.tab_contact_unselect};
    private int[] mIconSelectIds = {
            R.mipmap.tab_speech_select,
            R.mipmap.tab_contact_select};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    private Fragment msgFragment, meFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        initFragment();
        initMenuTab();
        switchContent(msgFragment);
        return view;
    }

    private void initFragment() {
        msgFragment = new MsgFragment();
        meFragment = new MeFragment();
    }

    private void initMenuTab() {
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnSelectIds[i]));
        }

        viewMenu.setTabData(mTabEntities);
        viewMenu.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                Logger.e("----->position = " + position);
                switch (position) {
                    case 0:
                        switchContent(msgFragment);
                        break;
                    case 1:
                        switchContent(meFragment);
                        break;
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        viewMenu.showMsg(0, 1);
        viewMenu.setMsgMargin(0, -15, 5);
    }

    public void switchContent(Fragment fragment) {
        mCallback.switchContent(fragment, R.id.frameMain);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
