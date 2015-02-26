package com.example.javier.MaterialDesignApp.Utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.example.javier.MaterialDesignApp.R;

import java.util.HashMap;

public class ScrollManagerToolbarTabs extends RecyclerView.OnScrollListener {

    private static final int MIN_SCROLL_TO_HIDE = 50;
    private boolean hidden;
    private int accummulatedDy;
    private int totalDy;
    private int initialOffset;
    int statusBarHeight, tabsHeight, toolbarHeight;
    View statusBar, tabs, toolbar, viewPager;
    Activity activity;
    private HashMap<View, Direction> viewsToHide = new HashMap<>();
    int colorStatusBar19, colorStatusBar21;
    TypedValue typedValueStatusBarColor = new TypedValue();

    public static enum Direction {UP, DOWN}

    public ScrollManagerToolbarTabs(Activity activity) {
        this.activity = activity;
        statusBar = activity.findViewById(R.id.statusBar);
        toolbar = activity.findViewById(R.id.toolbar);
        tabs = activity.findViewById(R.id.tabs);
        viewPager = activity.findViewById(R.id.pager);
        statusBarHeight = statusBar.getHeight();
        tabsHeight = tabs.getHeight();
        toolbarHeight = toolbar.getHeight();

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
        int translateYTabs = direction == Direction.UP ? -toolbarHeight : height;
        int translateYTabsViewPager = direction == Direction.UP ? -toolbarHeight : height;
        runTranslateAnimation(view, translateY, new AccelerateInterpolator(3));
        runTranslateAnimationTabs(view, translateYTabs, new AccelerateInterpolator(3));
        runTranslateAnimationTabsViewPager(view, translateYTabsViewPager, new AccelerateInterpolator(3));
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
        runTranslateAnimationTabs(view, 0, new DecelerateInterpolator(3));
        runTranslateAnimationTabsViewPager(view, convertToPx(56+48+25,activity), new AccelerateInterpolator(3));
    }

    private void runTranslateAnimation(View view, int translateY, Interpolator interpolator) {
        Animator slideInAnimation = ObjectAnimator.ofFloat(view, "translationY", translateY);
        slideInAnimation.setDuration(view.getContext().getResources().getInteger(android.R.integer.config_mediumAnimTime));
        slideInAnimation.setStartDelay(view.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
        slideInAnimation.setInterpolator(interpolator);
        slideInAnimation.start();
    }

    private void runTranslateAnimationTabs(View view, int translateYTabs, Interpolator interpolator) {
        Animator slideInAnimationTabs = ObjectAnimator.ofFloat(tabs, "translationY", translateYTabs);
        slideInAnimationTabs.setDuration(tabs.getContext().getResources().getInteger(android.R.integer.config_mediumAnimTime));
        slideInAnimationTabs.setStartDelay(tabs.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
        slideInAnimationTabs.setInterpolator(interpolator);
        slideInAnimationTabs.start();
    }
    private void runTranslateAnimationTabsViewPager(View view, int translateYTabsViewPager, Interpolator interpolator) {
        Animator slideInAnimationTabsViewPager = ObjectAnimator.ofFloat(viewPager, "translationY", translateYTabsViewPager);
        slideInAnimationTabsViewPager.setDuration(viewPager.getContext().getResources().getInteger(android.R.integer.config_mediumAnimTime));
        slideInAnimationTabsViewPager.setStartDelay(viewPager.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
        slideInAnimationTabsViewPager.setInterpolator(interpolator);
        slideInAnimationTabsViewPager.start();
    }


    public int convertToPx(int dp, Activity activity) {
        // Get the screen's density scale
        final float scale = activity.getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (dp * scale + 0.5f);
    }
}
