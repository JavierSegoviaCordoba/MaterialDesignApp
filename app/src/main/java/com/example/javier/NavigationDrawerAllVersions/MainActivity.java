package com.example.javier.NavigationDrawerAllVersions;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.widget.ToggleButton;

import com.example.javier.NavigationDrawerAllVersions.Utilitis.CircleTransform;
import com.example.javier.NavigationDrawerAllVersions.Utilitis.ColorChooserDialog;
import com.example.javier.NavigationDrawerAllVersions.Utilitis.JsonParser;
import com.squareup.picasso.Picasso;

import java.io.IOException;

// You can check the methods that I use inside onCreate below menu methods

public class MainActivity extends ActionBarActivity {
    Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    SharedPreferences sharedPreferences;
    ScrollView scrollView;
    ActionBarDrawerToggle mDrawerToggle;
    int theme, scrollPositionX = 0, scrollPositionY = -100;
    Intent intent;
    FrameLayout statusBar;
    SharedPreferences.Editor editor;
    ActivityOptions options;
    final Context context = this;
    TextView textViewName, textViewUserName;
    ImageView imageViewToogle, imageViewCover, imageViewPicture;
    ToggleButton toggleButtonDrawer;
    RelativeLayout relativeLayoutDrawerTexts, relativeLayoutChooseTheme;
    LinearLayout linearLayoutMain, linearLayoutSecond;
    String urlName = "javiersegoviacordoba";
    String urlProfile = "https://graph.facebook.com/" + urlName;
    String urlPicture = "https://graph.facebook.com/" + urlName + "picture?type=large&redirect=false";
    String urlCover = "https://graph.facebook.com/" + urlName + "cover";
    String name, userName, cover, picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Select theme saved by user (always before setContentView)
        theme();

        // Set content to the view
        setContentView(R.layout.activity_main);

        //Setup Status Bar and Toolbar
        toolbarStatusBar();

        //Setup Navigation Drawer
        navigationDrawer();

        // Fix issues for each version and modes (check method at end of this file)
        navigationBarStatusBar();

        // Advanced Settings, setup Choose App Theme button (really is relative layout)
        chooseAppThemeButton();

        // Setup drawer accounts toggle.
        toogleButtonDrawer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void theme(){
        sharedPreferences = getSharedPreferences("THEMES", Context.MODE_PRIVATE);
        theme = sharedPreferences.getInt("THEME", 0);
        settingTheme(theme);
    }

    public void toolbarStatusBar(){

        // Cast toolbar and status bar
        statusBar = (FrameLayout) findViewById(R.id.statusBar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Get support to the toolbar and change its title
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Main Activity");
    }

    public void navigationDrawer(){

        // Fix right margin to 56dp
        View drawer = findViewById(R.id.scrimInsetsFrameLayout);
        ViewGroup.LayoutParams layoutParams = drawer.getLayoutParams();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        layoutParams.width = displayMetrics.widthPixels - (56 * Math.round(displayMetrics.density));

        // Cast drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Get facebook items (name, username, picture, cover)
        new AsyncTaskParseJson().execute();

        // Setup Drawer Icon
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
    }

    public void chooseAppThemeButton(){

        // Setup choose app theme button
        relativeLayoutChooseTheme = (RelativeLayout) findViewById(R.id.chooseTheme);
        relativeLayoutChooseTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                ColorChooserDialog dialog = new ColorChooserDialog();
                dialog.show(fm, "fragment_color_chooser");
            }
        });
    }

    public class AsyncTaskParseJson extends AsyncTask<String, String, String> {

        // facebook urls
        @Override
        protected void onPreExecute() {
            urlName = "javiersegoviacordoba";
            urlProfile = "https://graph.facebook.com/" + urlName;
            urlPicture = "https://graph.facebook.com/" + urlName + "/picture?type=large&redirect=false";
            urlCover = "https://graph.facebook.com/" + urlName + "/?fields=cover";
        }

        // get JSON Object
        @Override
        protected String doInBackground(String... arg0) {
            JSONObject jsonObjectProfile, jsonObjectPicture, jsonObjectCover;
            try {
                jsonObjectProfile = JsonParser.readJsonFromUrl(urlProfile);
                jsonObjectPicture = JsonParser.readJsonFromUrl(urlPicture);
                jsonObjectCover = JsonParser.readJsonFromUrl(urlCover);

                // Storing each json item in variable
                name = jsonObjectProfile.getString("name");
                userName = jsonObjectProfile.getString("username");
                picture = jsonObjectPicture.getJSONObject("data").getString("url");
                cover = jsonObjectCover.getJSONObject("cover").getString("source");

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        // Set facebook items to the textviews and imageviews
        @Override
        protected void onPostExecute(String strFromDoInBg) {
            textViewName = (TextView) findViewById(R.id.textViewName);
            textViewName.setText(name);
            textViewUserName = (TextView) findViewById(R.id.textViewUserName);
            textViewUserName.setText(userName);
            imageViewPicture = (ImageView) findViewById(R.id.imageViewPicture);
            Picasso.with(context).load(picture).transform(new CircleTransform()).into(imageViewPicture);
            imageViewCover = (ImageView) findViewById(R.id.imageViewCover);
            Picasso.with(context).load(cover).into(imageViewCover);
        }
    }

    public void setThemeFragment(int theme) {
        switch (theme) {
            case 1:
                editor = sharedPreferences.edit();
                editor.putInt("THEME", 1).apply();
                break;
            case 2:
                editor = sharedPreferences.edit();
                editor.putInt("THEME", 2).apply();
                break;
            case 3:
                editor = sharedPreferences.edit();
                editor.putInt("THEME", 3).apply();
                break;
            case 4:
                editor = sharedPreferences.edit();
                editor.putInt("THEME", 4).apply();
                break;
            case 5:
                editor = sharedPreferences.edit();
                editor.putInt("THEME", 5).apply();
                break;
            case 6:
                editor = sharedPreferences.edit();
                editor.putInt("THEME", 6).apply();
                break;
        }
    }

    public void navigationBarStatusBar() {

        // Fix portrait issues
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Fix issues for KitKat setting Status Bar color primary
            if (Build.VERSION.SDK_INT >= 19) {
                TypedValue typedValue19 = new TypedValue();
                MainActivity.this.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue19, true);
                final int color = typedValue19.data;
                FrameLayout statusBar = (FrameLayout) findViewById(R.id.statusBar);
                statusBar.setBackgroundColor(color);
            }

            // Fix issues for Lollipop, setting Status Bar color primary dark
            if (Build.VERSION.SDK_INT >= 21) {
                TypedValue typedValue21 = new TypedValue();
                MainActivity.this.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue21, true);
                final int color = typedValue21.data;
                FrameLayout statusBar = (FrameLayout) findViewById(R.id.statusBar);
                statusBar.setBackgroundColor(color);
            }
        }

        // Fix landscape issues (only Lollipop)
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (Build.VERSION.SDK_INT >= 19) {
                TypedValue typedValue19 = new TypedValue();
                MainActivity.this.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue19, true);
                final int color = typedValue19.data;
                FrameLayout statusBar = (FrameLayout) findViewById(R.id.statusBar);
                statusBar.setBackgroundColor(color);
            }
            if (Build.VERSION.SDK_INT >= 21) {
                TypedValue typedValue = new TypedValue();
                MainActivity.this.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
                final int color = typedValue.data;
                mDrawerLayout.setStatusBarBackgroundColor(color);
            }
        }
    }

    public void settingTheme(int theme) {
        switch (theme) {
            case 1:
                setTheme(R.style.AppTheme);
                break;
            case 2:
                setTheme(R.style.AppTheme2);
                break;
            case 3:
                setTheme(R.style.AppTheme3);
                break;
            case 4:
                setTheme(R.style.AppTheme4);
                break;
            case 5:
                setTheme(R.style.AppTheme5);
                break;
            case 6:
                setTheme(R.style.AppTheme6);
                break;
            default:
                setTheme(R.style.AppTheme);
                break;
        }
    }
    public void toogleButtonDrawer() {
        imageViewToogle = (ImageView) findViewById(R.id.imageViewToggle);
        toggleButtonDrawer = (ToggleButton) findViewById(R.id.toggleButtonDrawer);
        linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);
        linearLayoutSecond = (LinearLayout) findViewById(R.id.linearLayoutSecond);
        toggleButtonDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!toggleButtonDrawer.isChecked()) {
                    toggleButtonDrawer.setChecked(false);
                    imageViewToogle.setBackground(getResources().getDrawable(R.drawable.ic_action_navigation_arrow_drop_down));
                    linearLayoutMain.setVisibility(View.VISIBLE);
                    linearLayoutSecond.setVisibility(View.GONE);
                }
                if (toggleButtonDrawer.isChecked()) {
                    toggleButtonDrawer.setChecked(true);
                    imageViewToogle.setBackground(getResources().getDrawable(R.drawable.ic_action_navigation_arrow_drop_up));
                    linearLayoutMain.setVisibility(View.GONE);
                    linearLayoutSecond.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /*public void settingTransition() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= 21) {
                    LinearLayout contentLayout = (LinearLayout) findViewById(R.id.contentLayout);
                    contentLayout.setTransitionName("LAYOUT");
                    options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,
                            Pair.create(findViewById(R.id.contentLayout), "LAYOUT"));
                    startActivity(intent, options.toBundle());

                } else {
                    startActivity(intent);
                }
            }
        }, 300);
    }/*



    /*@Override protected void onStart() {
        super.onStart();
        Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();
    }

    @Override protected void onResume() {
        super.onResume();
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
    }

    @Override protected void onPause() {
        Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
        super.onPause();
    }

    @Override protected void onStop() {
        super.onStop();
        Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
    }

    @Override protected void onRestart() {
        super.onRestart();
        Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
        editor.putInt("POSITION", 0).apply();

    }*/
}
