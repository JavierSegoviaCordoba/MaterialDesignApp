package com.example.javier.MaterialDesignApp.Fragments;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.javier.MaterialDesignApp.MainActivity;
import com.example.javier.MaterialDesignApp.R;
import com.example.javier.MaterialDesignApp.RecyclerView.RecyclerViewClasses.Design;
import com.example.javier.MaterialDesignApp.Tabs.TabsAdapters.TabsDesignViewPagerAdapter;
import com.example.javier.MaterialDesignApp.Tabs.TabsUtils.SlidingTabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentDesign extends Fragment {

    String urlPost;
    JSONObject jsonObjectDesignPosts;
    JSONArray jsonArrayDesignContent;
    ArrayList<Design> designs;
    SwipeRefreshLayout swipeRefreshLayout;
    String[] designTitle, designExcerpt, designImage, designImageFull, designContent;
    int postNumber = 99;
    Boolean error = false;
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewAdapter;
    View view;
    SharedPreferences sharedPreferences;
    ViewPager pager;
    TabsDesignViewPagerAdapter tabsDesignViewPagerAdapter;
    SlidingTabLayout tabs;
    CharSequence titles[] = {"Get Started", "Material Design", "Style", "Patterns"};
    int tabNumber = titles.length;
    int tabsPaddingTop;
    TypedValue typedValueToolbarHeight = new TypedValue();
    Toolbar toolbar;
    FrameLayout statusBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_design, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Design");

        //  Setup tabs
        setupTabs();

        return view;
    }

    public void setupTabs() {
        tabsDesignViewPagerAdapter = new TabsDesignViewPagerAdapter(getFragmentManager(), titles, tabNumber);
        pager = (ViewPager) view.findViewById(R.id.pager);
        pager.setAdapter(tabsDesignViewPagerAdapter);
        tabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        tabs.setDistributeEvenly(false);
        // Tab indicator color
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                //TypedValue typedValue = new TypedValue();
                //getActivity().getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
                //final int color = typedValue.data;
                return getResources().getColor(R.color.md_white_1000);
            }
        });

        // Setup tabs top padding
        getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValueToolbarHeight, true);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (Build.VERSION.SDK_INT >= 19) {
                tabsPaddingTop = TypedValue.complexToDimensionPixelSize(typedValueToolbarHeight.data, getResources().getDisplayMetrics()) + convertToPx(25);
            }else{
                tabsPaddingTop = TypedValue.complexToDimensionPixelSize(typedValueToolbarHeight.data, getResources().getDisplayMetrics());
            }
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (Build.VERSION.SDK_INT >= 21) {
                tabsPaddingTop = TypedValue.complexToDimensionPixelSize(typedValueToolbarHeight.data, getResources().getDisplayMetrics());
            }
            if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21){
                tabsPaddingTop = TypedValue.complexToDimensionPixelSize(typedValueToolbarHeight.data, getResources().getDisplayMetrics()) + convertToPx(25);
            }
            if (Build.VERSION.SDK_INT < 19) {
                tabsPaddingTop = TypedValue.complexToDimensionPixelSize(typedValueToolbarHeight.data, getResources().getDisplayMetrics());
            }
        }
        tabs.setPadding(convertToPx(48), tabsPaddingTop, convertToPx(16), 0);

        tabs.setViewPager(pager);
    }

    public int convertToPx(int dp) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (dp * scale + 0.5f);
    }
}

