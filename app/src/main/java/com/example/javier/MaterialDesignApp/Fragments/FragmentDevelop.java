package com.example.javier.MaterialDesignApp.Fragments;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.javier.MaterialDesignApp.R;
import com.example.javier.MaterialDesignApp.RecyclerView.RecyclerViewAdapters.DevelopAdapter;
import com.example.javier.MaterialDesignApp.RecyclerView.RecyclerViewClasses.Develop;
import com.example.javier.MaterialDesignApp.RecyclerView.RecyclerViewDecorations.DividerItemDecoration;
import com.example.javier.MaterialDesignApp.Utilitis.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class FragmentDevelop extends Fragment {

    String urlPost;
    JSONObject jsonObjectDevelopPosts;
    JSONArray jsonArrayDevelopContent;
    ArrayList<Develop> develop;
    SwipeRefreshLayout swipeRefreshLayout;
    String[] developTitle, developExcerpt, developImage, developImageFull;
    int postNumber = 99;
    SharedPreferences sharedPreferences;
    Boolean error = false;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_develop, container, false);
        sharedPreferences = getActivity().getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        // Setup RecyclerView News
        recyclerViewDevelop(view);

        // Setup swipe to refresh
        swipeToRefresh(view);

        return view;
    }

    private void recyclerViewDevelop(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewDevelop);

        // Divider
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(android.R.drawable.divider_horizontal_bright)));

        // improve performance if you know that changes in content
        // do not change the size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        urlPost = "http://wordpressdesarrolladorandroid.hol.es/category/desarrollar?json=1";
        new AsyncTaskNewsParseJson().execute(urlPost);

    }

    public class AsyncTaskNewsParseJson extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
        }

        // get JSON Object
        @Override
        protected String doInBackground(String... url) {

            urlPost = url[0];
            try {
                jsonObjectDevelopPosts = JsonParser.readJsonFromUrl(urlPost);
                postNumber = jsonObjectDevelopPosts.getJSONArray("posts").length();
                jsonArrayDevelopContent = jsonObjectDevelopPosts.getJSONArray("posts");
                sharedPreferences.edit().putString("DEVELOP", jsonArrayDevelopContent.toString()).apply();
                developTitle = new String[postNumber];
                developExcerpt = new String[postNumber];
                developImage = new String[postNumber];
                developImageFull = new String[postNumber];
                for (int i = 0; i < postNumber; i++) {
                    developTitle[i] = Html.fromHtml(jsonObjectDevelopPosts.getJSONArray("posts").getJSONObject(i).getString("title")).toString();
                    developExcerpt[i] = Html.fromHtml(jsonObjectDevelopPosts.getJSONArray("posts").getJSONObject(i).getString("excerpt")).toString();
                    developImage[i] = Html.fromHtml(jsonObjectDevelopPosts.getJSONArray("posts").getJSONObject(i).getJSONObject("thumbnail_images").getJSONObject("thumbnail").getString("url")).toString();
                    developImageFull[i] = Html.fromHtml(jsonObjectDevelopPosts.getJSONArray("posts").getJSONObject(i).getJSONObject("thumbnail_images").getJSONObject("full").getString("url")).toString();
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                developTitle = new String[0];
                error = true;
            }
            return null;
        }

        // Set facebook items to the textviews and imageviews
        @Override
        protected void onPostExecute(String result) {

            develop = new ArrayList<>();

            //Data set used by the recyclerViewAdapter. This data will be displayed.
            if (developTitle.length != 0) {
                for (int i = 0; i < postNumber; i++) {
                    develop.add(new Develop(developTitle[i], developExcerpt[i], developImage[i]));
                }
            }
            if (error) {
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_LONG).show();
            }
            // Create the recyclerViewAdapter
            adapter = new DevelopAdapter(getActivity(), develop);
            recyclerView.setAdapter(adapter);

            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
            swipeRefreshLayout.setRefreshing(false);

            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
        }
    }
    private void swipeToRefresh(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);

        TypedValue typedValueColorPrimary = new TypedValue();
        TypedValue typedValueColorAccent = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorPrimary, typedValueColorPrimary, true);
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, typedValueColorAccent, true);
        final int colorPrimary = typedValueColorPrimary.data, colorAccent = typedValueColorAccent.data;
        swipeRefreshLayout.setColorSchemeColors(colorPrimary,colorAccent);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncTaskNewsParseJson().execute(urlPost);
            }
        });
    }
}

