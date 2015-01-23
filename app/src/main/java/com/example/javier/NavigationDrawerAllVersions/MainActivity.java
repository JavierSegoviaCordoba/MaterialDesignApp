package com.example.javier.NavigationDrawerAllVersions;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ScrollView;

public class MainActivity extends ActionBarActivity {
    Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    SharedPreferences sharedPreferences;
    CardView cardView, cardView1, cardView2, cardView3, cardView4, cardView5, cardView6;
    ScrollView scrollView;
    int theme, scrollPositionX = 0, scrollPositionY = -100;
    Intent intent;
    FrameLayout statusBar;
    SharedPreferences.Editor editor;
    ActivityOptions options;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Select theme saved by user
        sharedPreferences = getSharedPreferences("THEMES", Context.MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("POSITIONS", Context.MODE_PRIVATE);
        theme = sharedPreferences.getInt("THEME", 0);
        scrollPositionY = sharedPreferences.getInt("POSITION", 0);
        settingTheme(theme);

        // Set content to the view
        setContentView(R.layout.activity_main);

        //Setup Status Bar and Toolbar
        statusBar = (FrameLayout) findViewById(R.id.statusBar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Main Activity");

        //Setup Navigation Drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Fix issues for each version and modes (check method at end of this file)
        setNavigationStatusBar();

        // Setup Navigation Drawer icon
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

        // Setup Scroll View
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        ViewTreeObserver vto = scrollView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                scrollView.scrollTo(scrollPositionX, scrollPositionY);
            }
        });

        // Save one theme pressing a button (check style to see five themes)
        editor = sharedPreferences.edit();
        intent = new Intent(MainActivity.this, MainActivity.class);
        cardView1 = (CardView) findViewById(R.id.card_view1);
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return (event.getAction() == MotionEvent.ACTION_MOVE);
                    }
                });
                editor.putInt("THEME", 1).apply();
                editor.putInt("POSITION", scrollView.getScrollY()).apply();
                settingTransition(1);
            }
        });
        cardView2 = (CardView) findViewById(R.id.card_view2);
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return (event.getAction() == MotionEvent.ACTION_MOVE);
                    }
                });
                editor.putInt("THEME", 2).apply();
                editor.putInt("POSITION", scrollView.getScrollY()).apply();
                settingTransition(2);
            }
        });
        cardView3 = (CardView) findViewById(R.id.card_view3);
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return (event.getAction() == MotionEvent.ACTION_MOVE);
                    }
                });
                editor.putInt("THEME", 3).apply();
                editor.putInt("POSITION", scrollView.getScrollY()).apply();
                settingTransition(3);
            }
        });
        cardView4 = (CardView) findViewById(R.id.card_view4);
        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return (event.getAction() == MotionEvent.ACTION_MOVE);
                    }
                });
                editor.putInt("THEME", 4).apply();
                editor.putInt("POSITION", scrollView.getScrollY()).apply();
                settingTransition(4);
            }
        });
        cardView5 = (CardView) findViewById(R.id.card_view5);
        cardView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return (event.getAction() == MotionEvent.ACTION_MOVE);
                    }
                });
                editor.putInt("THEME", 5).apply();
                editor.putInt("POSITION", scrollView.getScrollY()).apply();
                settingTransition(5);
            }
        });
        cardView6 = (CardView) findViewById(R.id.card_view6);
        cardView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return (event.getAction() == MotionEvent.ACTION_MOVE);
                    }
                });
                editor.putInt("THEME", 6).apply();
                editor.putInt("POSITION", scrollView.getScrollY()).apply();
                settingTransition(6);
            }
        });
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
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.theme_dialog);
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setNavigationStatusBar() {

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

    public void settingTransition(final int card) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= 21) {
                    switch (card) {
                        case 1:
                            cardView1.setTransitionName("CARD_VIEW1");
                            options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,
                                    Pair.create(findViewById(R.id.card_view1), "CARD_VIEW1"));
                            startActivity(intent, options.toBundle());
                            break;
                        case 2:
                            cardView2.setTransitionName("CARD_VIEW2");
                            options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,
                                    Pair.create(findViewById(R.id.card_view2), "CARD_VIEW2"));
                            startActivity(intent, options.toBundle());
                            break;
                        case 3:
                            cardView3.setTransitionName("CARD_VIEW3");
                            options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,
                                    Pair.create(findViewById(R.id.card_view3), "CARD_VIEW3"));
                            startActivity(intent, options.toBundle());
                            break;
                        case 4:
                            cardView4.setTransitionName("CARD_VIEW4");
                            options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,
                                    Pair.create(findViewById(R.id.card_view4), "CARD_VIEW4"));
                            startActivity(intent, options.toBundle());
                            break;
                        case 5:
                            cardView5.setTransitionName("CARD_VIEW5");
                            options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,
                                    Pair.create(findViewById(R.id.card_view5), "CARD_VIEW5"));
                            startActivity(intent, options.toBundle());
                            break;
                        case 6:
                            cardView6.setTransitionName("CARD_VIEW6");
                            options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,
                                    Pair.create(findViewById(R.id.card_view6), "CARD_VIEW6"));
                            startActivity(intent, options.toBundle());
                            break;
                    }
                } else {
                    startActivity(intent);
                }
            }
        }, 300);
    }



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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
        editor.putInt("POSITION", 0).apply();

    }
}
