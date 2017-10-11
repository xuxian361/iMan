package com.sundy.iman.view.dialog;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sundy.iman.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sundy on 17/10/11.
 */

public class FlipProgressDialog extends DialogFragment {

    ImageView image;
    int counter = 0;
    Handler handler;
    Runnable r;

    //Set Animation stuff
    private int duration = 600;
    private String orientation = "rotationY";
    private float startAngle = 0.0f;
    private float endAngle = 180.0f;
    private float minAlpha = 0.0f;
    private float maxAlpha = 1.0f;

    private int backgroundColor = -1;
    private float backgroundAlpha = 0.5f;
    private int backgroundColorWIthAlpha = 452984831;
    private int borderStroke = 0;
    private int borderColor = -1;
    private int cornerRadius = 16;
    private float dimAmount = 0.0f;

    // Set Image settings
    private int imageMargin = 10;
    private int imageSize = 200;
    //	private int imageSize = FrameLayout.LayoutParams.MATCH_PARENT;
    private List<Integer> imageList = new ArrayList<Integer>();

    // Set cancelOnTouch
    private boolean canceledOnTouchOutside = true;


    public void setImage(ImageView image) {
        this.image = image;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
    }

    public void setEndAngle(float endAngle) {
        this.endAngle = endAngle;
    }

    public void setMinAlpha(float minAlpha) {
        this.minAlpha = minAlpha;
    }

    public void setMaxAlpha(float maxAlpha) {
        this.maxAlpha = maxAlpha;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setBackgroundAlpha(float backgroundAlpha) {
        this.backgroundAlpha = backgroundAlpha;
    }

    public void setBorderStroke(int borderStroke) {
        this.borderStroke = borderStroke;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    public void setDimAmount(float dimAmount) {
        this.dimAmount = dimAmount;
    }

    public void setImageMargin(int imageMargin) {
        this.imageMargin = imageMargin;
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }

    public void setImageList(List<Integer> imageList) {
        this.imageList = imageList;
    }

    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        this.canceledOnTouchOutside = canceledOnTouchOutside;
    }

    public FlipProgressDialog() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        setBackgroundDim();
    }

    @Override
    public void onResume() {
        super.onResume();
        repeatChangeImages();
    }

    private void repeatChangeImages() {
        final int numberImageList = imageList.size() - 1;

        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, duration);
                //Check if is the showing image last image in the ArrayList

                try {
                    if (numberImageList == counter) {
                        counter = 0;
                        image.setImageResource(imageList.get(counter));
                    } else {
                        counter++;
                        image.setImageResource(imageList.get(counter));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("image null error", "Try to set imageList!");
                }
            }
        };

        handler.postDelayed(r, duration);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Can not dismiss when pressing outside the dialog
        getDialog().setCanceledOnTouchOutside(canceledOnTouchOutside);

        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.NoDimDialogFragmentStyle);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.fragment_dialog, null, false);

        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.drawable.corner_dialog_bg);

        // Let's create the missing ImageView
        image = new ImageView(getActivity());

        // Now the layout parameters, these are a little tricky at first
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imageSize, imageSize);
        // Set margins for image
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        try {
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            image.setImageResource(imageList.get(0));
            image.setLayoutParams(params);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("image null error", "Try to set imageList!");
        }

        // Let's get the root layout and add our ImageView
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.root);
        layout.addView(image, 0, params);

        animateAnimatorSetSample(image);

        return view;
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        if (handler != null) {
            handler.removeCallbacks(r);
        }
        super.onDestroyView();
    }

    private void animateAnimatorSetSample(ImageView target) {
        // Set AnimatorList(Will set on AnimatorSet)
        List<Animator> animatorList = new ArrayList<Animator>();
        PropertyValuesHolder alphaAnimator = PropertyValuesHolder.ofFloat("alpha", minAlpha, maxAlpha, minAlpha);
        PropertyValuesHolder flipAnimator = PropertyValuesHolder.ofFloat(orientation, startAngle, endAngle);
        ObjectAnimator translationAnimator =
                ObjectAnimator.ofPropertyValuesHolder(target, alphaAnimator, flipAnimator);
        translationAnimator.setDuration(duration);
        translationAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        // repeat translationAnimator
        translationAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        // Set all animation to animatorList
        animatorList.add(translationAnimator);
        final AnimatorSet animatorSet = new AnimatorSet();
        // Set animatorList to animatorSet
        animatorSet.playSequentially(animatorList);
        // Start Animation
        animatorSet.start();
    }

    private void setBackgroundDim() {
        int height = imageSize + 60;
        int width = imageSize + 60;

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = dimAmount;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        windowParams.height = height;
        windowParams.width = width;
        window.setAttributes(windowParams);
    }
}

