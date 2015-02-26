package com.example.javier.MaterialDesignApp.Utils;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.example.javier.MaterialDesignApp.R;

import java.util.HashMap;

public class ScrollManagerToolbar extends RecyclerView.OnScrollListener {

    private static final int MIN_SCROLL_TO_HIDE = 50;
    private boolean hidden;
    private int accummulatedDy;
    private int totalDy;
    private int initialOffset;
    int statusBarHeight;
    View statusBar;
    Activity activity;
    private HashMap<View, Direction> viewsToHide = new HashMap<>();
    int colorStatusBar19, colorStatusBar21;
    TypedValue typedValueStatusBarColor = new TypedValue();

    public static enum Direction {UP, DOWN}

    public ScrollManagerToolbar(Activity activity) {
        this.activity = activity;
        statusBar = activity.findViewById(R.id.statusBar);
        statusBarHeight = statusBar.getHeight();

        activity.getTheme().resolveAttribute(R.attr.colorPrimary, typedValueStatusBarColor, true);
        colorStatusBar19 = typedValueStatusBarColor.data;
        activity.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValueStatusBarColor, true);
        colorStatusBar21 = typedValueStatusBarColor.data;
    }

    public void attach(RecyclerView recyclerView) {
        recyclerView.setOnScrollListener(this);
    }

    public void addView(View view, Direction direction) {
        viewsToHide.put(view, direction);
    }

    public void setInitialOffset(int initialOffset) {
        this.initialOffset = initialOffset;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        totalDy += dy;

        if (totalDy < initialOffset) {
            return;
        }

        if (dy > 0) {
            accummulatedDy = accummulatedDy > 0 ? accummulatedDy + dy : dy;
            if (accummulatedDy > MIN_SCROLL_TO_HIDE) {
                hideViews();
            }
        } else if (dy < 0) {
            accummulatedDy = accummulatedDy < 0 ? accummulatedDy + dy : dy;
            if (accummulatedDy < -MIN_SCROLL_TO_HIDE) {
                showViews();
            }
        }
    }

    public void hideViews() {
        if (!hidden) {
            hidden = true;
            for (View view : viewsToHide.keySet()) {
                hideView(view, viewsToHide.get(view));
            }
        }
    }

    public void showViews() {
        if (hidden) {
            hidden = false;
            for (View view : viewsToHide.keySet()) {
                showView(view);
            }
        }
    }


    private void hideView(final View view, Direction direction) {
        int height = calculateTranslation(view);
        int translateY = direction == Direction.UP ? -height - statusBarHeight : height;
        runTranslateAnimation(view, translateY, new AccelerateInterpolator(3));

        /*if (Build.VERSION.SDK_INT >= 21) {
            ValueAnimator colorAnimation = ValueAnimator.ofArgb(colorStatusBar21, activity.getResources().getColor(R.color.inset));
            colorAnimation.setDuration(activity.getResources().getInteger(android.R.integer.config_longAnimTime));
            colorAnimation.setStartDelay(activity.getResources().getInteger(android.R.integer.config_mediumAnimTime));
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    statusBar.invalidate();
                    statusBar.setBackgroundColor((Integer) animator.getAnimatedValue());
                }
            });
            colorAnimation.start();
        }

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21 ) {
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorStatusBar19, activity.getResources().getColor(android.R.color.transparent));
            colorAnimation.setDuration(activity.getResources().getInteger(android.R.integer.config_longAnimTime));
            colorAnimation.setStartDelay(activity.getResources().getInteger(android.R.integer.config_mediumAnimTime));
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    statusBar.invalidate();
                    statusBar.setBackgroundColor((Integer) animator.getAnimatedValue());
                }
            });
            colorAnimation.start();
        }*/
    }

    /**
     * Takes height + margins
     *
     * @param view View to translate
     * @return translation in pixels
     */
    private int calculateTranslation(View view) {
        int height = view.getHeight();

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        int margins = params.topMargin + params.bottomMargin;

        return height + margins;
    }

    private void showView(View view) {
        runTranslateAnimation(view, 0, new DecelerateInterpolator(3));
        /*if (Build.VERSION.SDK_INT >= 21) {
            ValueAnimator colorAnimation = ValueAnimator.ofArgb(activity.getResources().getColor(R.color.inset), colorStatusBar21);
            colorAnimation.setDuration(activity.getResources().getInteger(android.R.integer.config_mediumAnimTime));
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    statusBar.invalidate();
                    statusBar.setBackgroundColor((Integer) animator.getAnimatedValue());
                }
            });
            colorAnimation.start();
        }

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21 ) {
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), activity.getResources().getColor(android.R.color.transparent), colorStatusBar19);
            colorAnimation.setDuration(activity.getResources().getInteger(android.R.integer.config_mediumAnimTime));
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    statusBar.invalidate();
                    statusBar.setBackgroundColor((Integer) animator.getAnimatedValue());
                }
            });
            colorAnimation.start();
        }*/
    }

    private void runTranslateAnimation(View view, int translateY, Interpolator interpolator) {
        Animator slideInAnimation = ObjectAnimator.ofFloat(view, "translationY", translateY);
        slideInAnimation.setDuration(view.getContext().getResources().getInteger(android.R.integer.config_mediumAnimTime));
        slideInAnimation.setStartDelay(view.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
        slideInAnimation.setInterpolator(interpolator);
        slideInAnimation.start();
    }

    public int convertToPx(int dp, Activity activity) {
        // Get the screen's density scale
        final float scale = activity.getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (dp * scale + 0.5f);
    }
}
