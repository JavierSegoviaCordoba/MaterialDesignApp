package com.example.javier.MaterialDesignApp;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.javier.MaterialDesignApp.Fragments.FragmentDesign;
import com.example.javier.MaterialDesignApp.Fragments.FragmentDevelop;
import com.example.javier.MaterialDesignApp.RecyclerView.RecyclerViewAdapters.DrawerAdapter;
import com.example.javier.MaterialDesignApp.RecyclerView.RecyclerViewClasses.DrawerItem;
import com.example.javier.MaterialDesignApp.RecyclerView.RecyclerViewUtils.ItemClickSupport;
import com.example.javier.MaterialDesignApp.Utils.JsonParser;
import com.example.javier.MaterialDesignApp.Utils.PicassoTransform.CircleTransformWhite;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

// You can check the methods that I use inside onCreate below menu methods

public class MainActivity extends ActionBarActivity {
    final Context context = this;
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
    TextView textViewName, textViewUsername;
    ImageView imageViewToogle, imageViewCover, imageViewPicture;
    ToggleButton toggleButtonDrawer;
    RelativeLayout relativeLayoutDrawerTexts, relativeLayoutChooseTheme, relativeLayoutSettings;
    LinearLayout linearLayoutMain, linearLayoutSecond;
    String urlName = "", urlPost = "";
    String urlProfile = "https://graph.facebook.com/" + urlName;
    String urlPicture = "https://graph.facebook.com/" + urlName + "picture?type=large&redirect=false";
    String urlCover = "https://graph.facebook.com/" + urlName + "cover";
    String facebookID, name, username, cover, picture;
    Bitmap bitmapPicture, bitmapCover;
    Drawable drawablePicture, drawableCover;
    File file, folder;
    Boolean downloaded, error = false;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    String[] newsTitle, newsExcerpt, newsImage, newsContent, drawerArray;
    RecyclerView recyclerViewDrawer;
    int postNumber = 99;
    JSONObject jsonObjectNewsPosts;
    JSONArray jsonArrayNewsContent;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView.Adapter adapterDrawer;
    private RecyclerView.LayoutManager layoutManagerDrawer;
    private boolean mActionBarAutoHideEnabled = false;
    private int mActionBarAutoHideSensivity = 0;
    private int mActionBarAutoHideMinY = 0;
    private int mActionBarAutoHideSignal = 0;
    private boolean mActionBarShown = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Select theme saved by user
        theme();

        super.onCreate(savedInstanceState);

        // Set content to the view
        setContentView(R.layout.activity_main);

        //Setup Status Bar and Toolbar
        toolbarStatusBar();

        // Setup Fragments
        setFragment(sharedPreferences.getInt("FRAGMENT", 0));

        //Setup Navigation Drawer
        navigationDrawer();

        // Setup drawer accounts toggle.
        toogleButtonDrawer();

        // Open settings method
        openSettings();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item_post clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.about_dialog);
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        sharedPreferences = getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean("DOWNLOAD", false);
        editor.apply();

        intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    public void theme() {
        sharedPreferences = getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        theme = sharedPreferences.getInt("THEME", 0);
        settingTheme(theme);
    }

    public void toolbarStatusBar() {

        // Cast toolbar and status bar
        statusBar = (FrameLayout) findViewById(R.id.statusBar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Get support to the toolbar and change its title
        setSupportActionBar(toolbar);
    }

    public void navigationDrawer() {

        // Get ID saved in settings
        sharedPreferences = getSharedPreferences("VALUES", MODE_PRIVATE);
        facebookID = sharedPreferences.getString("FACEBOOKID", "javiersegoviacordoba");

        // Cast drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Get preferences
        sharedPreferences = getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        downloaded = sharedPreferences.getBoolean("DOWNLOAD", false);

        // Fix right margin to 56dp (portrait)
        View drawer = findViewById(R.id.scrimInsetsFrameLayout);
        ViewGroup.LayoutParams layoutParams = drawer.getLayoutParams();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutParams.width = displayMetrics.widthPixels - (56 * Math.round(displayMetrics.density));
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutParams.width = displayMetrics.widthPixels + (20 * Math.round(displayMetrics.density)) - displayMetrics.widthPixels / 2;
        }

        name = sharedPreferences.getString("NAME", "");
        if (!name.equals("")) {
            textViewName = (TextView) findViewById(R.id.textViewName);
            textViewName.setText(name);
        }
        username = sharedPreferences.getString("USERNAME", "");
        if (!username.equals("")) {
            textViewUsername = (TextView) findViewById(R.id.textViewUsername);
            textViewUsername.setText(username);
        }
        file = new File(Environment.getExternalStorageDirectory().getPath() + "/MaterialDesignApp/picture.png");
        if (file.length() != 0) {
            imageViewPicture = (ImageView) findViewById(R.id.imageViewPicture);
            imageViewPicture.setImageDrawable(Drawable.createFromPath(file.toString()));
        }
        file = new File(Environment.getExternalStorageDirectory().getPath() + "/MaterialDesignApp/cover.png");
        if (file.length() != 0) {
            imageViewCover = (ImageView) findViewById(R.id.imageViewCover);
            imageViewCover.setImageDrawable(Drawable.createFromPath(file.toString()));
        }

        if (!downloaded) {

            // Get facebook items (name, username, picture, cover)
            new AsyncTaskFacebookParseJson().execute(facebookID);

        } else {
            Toast.makeText(MainActivity.this, downloaded.toString(), Toast.LENGTH_SHORT).show();
        }

        // Setup Drawer Icon
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

        // statusBar color behind navigation drawer
        TypedValue typedValueStatusBarColor = new TypedValue();
        MainActivity.this.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValueStatusBarColor, true);
        final int colorStatusBar = typedValueStatusBarColor.data;
        mDrawerLayout.setStatusBarBackgroundColor(colorStatusBar);

        // Setup RecyclerView inside drawer
        recyclerViewDrawer = (RecyclerView) findViewById(R.id.recyclerViewDrawer);
        recyclerViewDrawer.setHasFixedSize(true);
        recyclerViewDrawer.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        ArrayList<DrawerItem> drawerItems = new ArrayList<>();
        final String[] drawerTitles = getResources().getStringArray(R.array.drawer);
        final TypedArray drawerIcons = getResources().obtainTypedArray(R.array.drawerIcons);
        for (int i = 0; i < drawerTitles.length; i++) {
            drawerItems.add(new DrawerItem(drawerTitles[i], drawerIcons.getDrawable(i)));
        }
        drawerIcons.recycle();
        adapterDrawer = new DrawerAdapter(drawerItems);
        recyclerViewDrawer.setAdapter(adapterDrawer);
        final TypedValue typedValue = new TypedValue();
        MainActivity.this.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        final int color = typedValue.data;

        //TODO try to get status bar translucent in landscape mode (lollipop)
        //mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(android.R.color.transparent));
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        recyclerViewDrawer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                for (int i = 0; i < drawerTitles.length; i++) {
                    if (i == sharedPreferences.getInt("FRAGMENT", 0)) {
                        ImageView imageViewDrawerIcon = (ImageView) recyclerViewDrawer.getChildAt(i).findViewById(R.id.imageViewDrawerIcon);
                        TextView textViewDrawerTitle = (TextView) recyclerViewDrawer.getChildAt(i).findViewById(R.id.textViewDrawerItemTitle);
                        imageViewDrawerIcon.setColorFilter(color);
                        if (Build.VERSION.SDK_INT > 15) {
                            imageViewDrawerIcon.setImageAlpha(255);
                        } else {
                            imageViewDrawerIcon.setAlpha(255);
                        }
                        textViewDrawerTitle.setTextColor(color);
                        RelativeLayout relativeLayoutDrawerItem = (RelativeLayout) recyclerViewDrawer.getChildAt(i).findViewById(R.id.relativeLayoutDrawerItem);
                        TypedValue typedValueDrawerSelected = new TypedValue();
                        getTheme().resolveAttribute(R.attr.colorPrimary, typedValueDrawerSelected, true);
                        int colorDrawerItemSelected = typedValueDrawerSelected.data;
                        colorDrawerItemSelected = (colorDrawerItemSelected & 0x00FFFFFF) | 0x30000000;
                        relativeLayoutDrawerItem.setBackgroundColor(colorDrawerItemSelected);

                    } else {
                        ImageView imageViewDrawerIcon = (ImageView) recyclerViewDrawer.getChildAt(i).findViewById(R.id.imageViewDrawerIcon);
                        TextView textViewDrawerTitle = (TextView) recyclerViewDrawer.getChildAt(i).findViewById(R.id.textViewDrawerItemTitle);
                        imageViewDrawerIcon.setColorFilter(getResources().getColor(R.color.md_text));
                        if (Build.VERSION.SDK_INT > 15) {
                            imageViewDrawerIcon.setImageAlpha(138);
                        } else {
                            imageViewDrawerIcon.setAlpha(138);
                        }
                        textViewDrawerTitle.setTextColor(getResources().getColor(R.color.md_text));
                        RelativeLayout relativeLayoutDrawerItem = (RelativeLayout) recyclerViewDrawer.getChildAt(i).findViewById(R.id.relativeLayoutDrawerItem);
                        relativeLayoutDrawerItem.setBackgroundColor(getResources().getColor(R.color.md_white_1000));
                    }
                }

                // unregister listener (this is important)
                recyclerViewDrawer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });


        // RecyclerView item listener.
        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(recyclerViewDrawer);
        itemClickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, final int position, long id) {

                for (int i = 0; i < drawerTitles.length; i++) {
                    if (i == position) {
                        ImageView imageViewDrawerIcon = (ImageView) recyclerViewDrawer.getChildAt(i).findViewById(R.id.imageViewDrawerIcon);
                        TextView textViewDrawerTitle = (TextView) recyclerViewDrawer.getChildAt(i).findViewById(R.id.textViewDrawerItemTitle);
                        imageViewDrawerIcon.setColorFilter(color);
                        if (Build.VERSION.SDK_INT > 15) {
                            imageViewDrawerIcon.setImageAlpha(255);
                        } else {
                            imageViewDrawerIcon.setAlpha(255);
                        }
                        textViewDrawerTitle.setTextColor(color);
                        RelativeLayout relativeLayoutDrawerItem = (RelativeLayout) recyclerViewDrawer.getChildAt(i).findViewById(R.id.relativeLayoutDrawerItem);
                        TypedValue typedValueDrawerSelected = new TypedValue();
                        getTheme().resolveAttribute(R.attr.colorPrimary, typedValueDrawerSelected, true);
                        int colorDrawerItemSelected = typedValueDrawerSelected.data;
                        colorDrawerItemSelected = (colorDrawerItemSelected & 0x00FFFFFF) | 0x30000000;
                        relativeLayoutDrawerItem.setBackgroundColor(colorDrawerItemSelected);

                    } else {
                        ImageView imageViewDrawerIcon = (ImageView) recyclerViewDrawer.getChildAt(i).findViewById(R.id.imageViewDrawerIcon);
                        TextView textViewDrawerTitle = (TextView) recyclerViewDrawer.getChildAt(i).findViewById(R.id.textViewDrawerItemTitle);
                        imageViewDrawerIcon.setColorFilter(getResources().getColor(R.color.md_text));
                        if (Build.VERSION.SDK_INT > 15) {
                            imageViewDrawerIcon.setImageAlpha(138);
                        } else {
                            imageViewDrawerIcon.setAlpha(138);
                        }
                        textViewDrawerTitle.setTextColor(getResources().getColor(R.color.md_text));
                        RelativeLayout relativeLayoutDrawerItem = (RelativeLayout) recyclerViewDrawer.getChildAt(i).findViewById(R.id.relativeLayoutDrawerItem);
                        relativeLayoutDrawerItem.setBackgroundColor(getResources().getColor(R.color.md_white_1000));
                    }
                }

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after some time
                        setFragment(position);
                    }
                }, 250);
                mDrawerLayout.closeDrawers();
            }
        });
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
            case 7:
                setTheme(R.style.AppTheme7);
                break;
            case 8:
                setTheme(R.style.AppTheme8);
                break;
            case 9:
                setTheme(R.style.AppTheme9);
                break;
            case 10:
                setTheme(R.style.AppTheme10);
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
                    imageViewToogle.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_action_navigation_arrow_drop_down));
                    linearLayoutMain.setVisibility(View.VISIBLE);
                    linearLayoutSecond.setVisibility(View.GONE);
                }
                if (toggleButtonDrawer.isChecked()) {
                    toggleButtonDrawer.setChecked(true);
                    imageViewToogle.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_action_navigation_arrow_drop_up));
                    linearLayoutMain.setVisibility(View.GONE);
                    linearLayoutSecond.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        sharedPreferences = getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //editor.putInt("POSITION", 0).apply();
        editor.putBoolean("DOWNLOAD", false);
        editor.apply();

        Boolean prueba = sharedPreferences.getBoolean("DOWNLOAD", false);
        Toast.makeText(this, "Leave " + prueba.toString(), Toast.LENGTH_SHORT).show();
    }

    private void openSettings() {
        relativeLayoutSettings = (RelativeLayout) findViewById(R.id.relativeLayoutSettings);
        relativeLayoutSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after some time
                        mDrawerLayout.closeDrawers();
                    }
                }, 500);
            }
        });
    }

    public class AsyncTaskFacebookParseJson extends AsyncTask<String, String, String> {

        // facebook urls
        @Override
        protected void onPreExecute() {
        }

        // get JSON Object
        @Override
        protected String doInBackground(String... url) {

            urlName = url[0];
            urlProfile = "https://graph.facebook.com/" + urlName;
            urlPicture = "https://graph.facebook.com/" + urlName + "/picture?type=large&redirect=false";
            urlCover = "https://graph.facebook.com/" + urlName + "/?fields=cover";

            JSONObject jsonObjectProfile, jsonObjectPicture, jsonObjectCover;
            try {
                jsonObjectProfile = JsonParser.readJsonFromUrl(urlProfile);
                jsonObjectPicture = JsonParser.readJsonFromUrl(urlPicture);
                jsonObjectCover = JsonParser.readJsonFromUrl(urlCover);

                // Storing each json item_post in variable
                name = jsonObjectProfile.getString("name");
                username = "Facebook ID: " + jsonObjectProfile.getString("username");
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
            textViewUsername = (TextView) findViewById(R.id.textViewUsername);
            textViewUsername.setText(username);
            imageViewPicture = (ImageView) findViewById(R.id.imageViewPicture);
            imageViewCover = (ImageView) findViewById(R.id.imageViewCover);

            sharedPreferences = getSharedPreferences("VALUES", Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putString("NAME", name);
            editor.putString("USERNAME", username);
            editor.apply();

            folder = new File(Environment.getExternalStorageDirectory() + "/MaterialDesignApp");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            Target targetPicture = new Target() {
                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            file = new File(Environment.getExternalStorageDirectory().getPath() + "/MaterialDesignApp/picture.png");
                            try {
                                file.createNewFile();
                                FileOutputStream ostream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                                ostream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    Toast.makeText(MainActivity.this, "Creating Picture", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };
            Target targetCover = new Target() {
                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/MaterialDesignApp/cover.png");
                            try {
                                file.createNewFile();
                                FileOutputStream ostream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                                ostream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };

            Picasso.with(context).load(picture).transform(new CircleTransformWhite()).into(targetPicture);
            Picasso.with(context).load(cover).into(targetCover);

            imageViewPicture.setTag(targetPicture);
            imageViewCover.setTag(targetCover);

            Picasso.with(context).load(picture).placeholder(imageViewPicture.getDrawable()).transform(new CircleTransformWhite()).into(imageViewPicture);
            Picasso.with(context).load(cover).placeholder(imageViewCover.getDrawable()).into(imageViewCover);

            sharedPreferences = getSharedPreferences("VALUES", Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putBoolean("DOWNLOAD", true);
            editor.apply();
        }
    }

    public void setFragment(int position) {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        switch (position) {
            case 0:
                sharedPreferences.edit().putInt("FRAGMENT", 0).apply();
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                FragmentDesign fragmentDesign = new FragmentDesign();
                fragmentTransaction.replace(R.id.fragment, fragmentDesign);
                fragmentTransaction.commit();
                break;
            case 1:
                sharedPreferences.edit().putInt("FRAGMENT", 1).apply();
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                FragmentDevelop fragmentDevelop = new FragmentDevelop();
                fragmentTransaction.replace(R.id.fragment, fragmentDevelop);
                fragmentTransaction.commit();
                break;
        }
    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            sharedPreferences = getSharedPreferences("VALUES", Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            //editor.putInt("POSITION", 0).apply();
            editor.putBoolean("DOWNLOAD", false);
            editor.apply();

            Boolean prueba = sharedPreferences.getBoolean("DOWNLOAD", false);
            Toast.makeText(this, "Destroy " + prueba.toString(), Toast.LENGTH_SHORT).show();
        }
    }*/

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
    }*/

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
    }*/
}
