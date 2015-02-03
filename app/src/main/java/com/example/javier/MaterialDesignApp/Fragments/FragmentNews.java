package com.example.javier.MaterialDesignApp.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.javier.MaterialDesignApp.R;
import com.example.javier.MaterialDesignApp.RecyclerView.RecyclerViewAdapters.NewsAdapter;
import com.example.javier.MaterialDesignApp.RecyclerView.RecyclerViewClasses.News;
import com.example.javier.MaterialDesignApp.RecyclerView.RecyclerViewDecorations.DividerItemDecoration;
import com.example.javier.MaterialDesignApp.Utilitis.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class FragmentNews extends Fragment {

    String urlPost;
    JSONObject jsonObjectNewsPosts;
    JSONArray jsonArrayNewsContent;
    ArrayList<News> newses;
    SwipeRefreshLayout swipeRefreshLayout;
    String[] newsTitle, newsExcerpt, newsImage, newsContent;
    int postNumber = 99;
    SharedPreferences sharedPreferences;
    Boolean error = false;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_news, container, false);
        sharedPreferences = getActivity().getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        // Setup RecyclerView News
        recyclerViewNews(view);

        // Setup swipe to refresh
        swipeToRefresh(view);

        return view;
    }

    private void recyclerViewNews(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewNews);

        // Divider
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(android.R.drawable.divider_horizontal_bright)));

        // improve performance if you know that changes in content
        // do not change the size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        urlPost = "http://wordpressdesarrolladorandroid.hol.es/?json=1";
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
                jsonObjectNewsPosts = JsonParser.readJsonFromUrl(urlPost);
                postNumber = jsonObjectNewsPosts.getJSONArray("posts").length();
                jsonArrayNewsContent = jsonObjectNewsPosts.getJSONArray("posts");
                sharedPreferences.edit().putString("NEWSCONTENT", jsonArrayNewsContent.toString()).apply();
                newsTitle = new String[postNumber];
                newsExcerpt = new String[postNumber];
                newsImage = new String[postNumber];
                for (int i = 0; i < postNumber; i++) {
                    newsTitle[i] = Html.fromHtml(jsonObjectNewsPosts.getJSONArray("posts").getJSONObject(i).getString("title")).toString();
                    newsExcerpt[i] = Html.fromHtml(jsonObjectNewsPosts.getJSONArray("posts").getJSONObject(i).getString("excerpt")).toString();
                    newsImage[i] = Html.fromHtml(jsonObjectNewsPosts.getJSONArray("posts").getJSONObject(i).getString("thumbnail")).toString();
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                newsTitle = new String[0];
                error = true;
            }
            return null;
        }

        // Set facebook items to the textviews and imageviews
        @Override
        protected void onPostExecute(String result) {

            newses = new ArrayList<>();

            //Data set used by the adapter. This data will be displayed.
            if (newsTitle.length != 0) {
                for (int i = 0; i < postNumber; i++) {
                    newses.add(new News(newsTitle[i], newsExcerpt[i], newsImage[i]));
                }
            }
            if (error) {
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_LONG).show();
            }
            // Create the adapter
            adapter = new NewsAdapter(getActivity(), newses);
            recyclerView.setAdapter(adapter);

            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);

            swipeRefreshLayout = (android.support.v4.widget.SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
            swipeRefreshLayout.setRefreshing(false);
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
                urlPost = "http://wordpressdesarrolladorandroid.hol.es/?json=1";
                new AsyncTaskNewsParseJson().execute(urlPost);
            }
        });
    }
}

