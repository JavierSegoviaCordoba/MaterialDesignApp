package com.example.javier.MaterialDesignApp.Fragments;

import android.content.SharedPreferences;
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

        getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValueToolbarHeight, true);

        if (Build.VERSION.SDK_INT >= 19) {
            tabsPaddingTop = TypedValue.complexToDimensionPixelSize(typedValueToolbarHeight.data, getResources().getDisplayMetrics()) + convertToPx(25);
        }else{
            tabsPaddingTop = TypedValue.complexToDimensionPixelSize(typedValueToolbarHeight.data, getResources().getDisplayMetrics());
        }

        tabs.setPadding(0, tabsPaddingTop, 0, 0);

        tabs.setViewPager(pager);
    }


    /*public class AsyncTaskNewsParseJson extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
        }

        // get JSON Object
        @Override
        protected String doInBackground(String... url) {

            urlPost = url[0];
            try {
                jsonObjectDesignPosts = JsonParser.readJsonFromUrl(urlPost);
                postNumber = jsonObjectDesignPosts.getJSONArray("posts").length();
                jsonArrayDesignContent = jsonObjectDesignPosts.getJSONArray("posts");
                sharedPreferences.edit().putString("DESIGN", jsonArrayDesignContent.toString()).apply();
                designTitle = new String[postNumber];
                designExcerpt = new String[postNumber];
                designContent = new String[postNumber];
                designImage = new String[postNumber];
                designImageFull = new String[postNumber];
                for (int i = 0; i < postNumber; i++) {
                    designTitle[i] = Html.fromHtml(jsonObjectDesignPosts.getJSONArray("posts").getJSONObject(i).getString("title")).toString();
                    designExcerpt[i] = Html.fromHtml(jsonObjectDesignPosts.getJSONArray("posts").getJSONObject(i).getString("excerpt")).toString();
                    designContent[i] = jsonObjectDesignPosts.getJSONArray("posts").getJSONObject(i).getString("content");
                    designImage[i] = Html.fromHtml(jsonObjectDesignPosts.getJSONArray("posts").getJSONObject(i).getJSONObject("thumbnail_images").getJSONObject("thumbnail").getString("url")).toString();
                    designImageFull[i] = Html.fromHtml(jsonObjectDesignPosts.getJSONArray("posts").getJSONObject(i).getJSONObject("thumbnail_images").getJSONObject("full").getString("url")).toString();
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                designTitle = new String[0];
                error = true;
            }
            return null;
        }

        // Set facebook items to the textviews and imageviews
        @Override
        protected void onPostExecute(String result) {

            designs = new ArrayList<>();

            //Data set used by the recyclerViewAdapter. This data will be displayed.
            if (designTitle.length != 0) {
                for (int i = 0; i < postNumber; i++) {
                    designs.add(new Design(designTitle[i], designExcerpt[i], designImage[i]));
                }
            }
            if (error) {
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_LONG).show();
            }
            // Create the recyclerViewAdapter
            recyclerViewAdapter = new DesignAdapter(getActivity(), designs);
            recyclerView.setAdapter(recyclerViewAdapter);

            swipeRefreshLayout = (android.support.v4.widget.SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
            swipeRefreshLayout.setRefreshing(false);

            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
        }
    }
    private void swipeToRefresh(View view) {
        swipeRefreshLayout = (android.support.v4.widget.SwipeRefreshLayout) view.findViewById(R.id.swipe_container);

        TypedValue typedValueColorPrimary = new TypedValue();
        TypedValue typedValueColorAccent = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorPrimary, typedValueColorPrimary, true);
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, typedValueColorAccent, true);
        final int colorPrimary = typedValueColorPrimary.data, colorAccent = typedValueColorAccent.data;
        swipeRefreshLayout.setColorSchemeColors(colorPrimary,colorAccent);

        swipeRefreshLayout.setOnRefreshListener(new android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncTaskNewsParseJson().execute(urlPost);
            }
        });
    }*/

    public int convertToPx(int dp) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (dp * scale + 0.5f);
    }
}

