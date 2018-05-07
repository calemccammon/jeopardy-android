package mccammon.cale.jeopardyandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

//The game's primary activity. This is the root activity for all the fragments.
public class ClueActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private FragmentManager fragmentManager;
    private static int sessionScore = 0;
    private static final String PREFS_DATA = "jeopardy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_clue);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);

        //Create action bar.
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_main);

        NavigationView navigationView = findViewById(R.id.nav_view);
        setupDrawerContent(navigationView);
        setTitle("Play");

        if(savedInstanceState == null) {
            PlayFragment playFragment = new PlayFragment();
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.flContent, playFragment).commit();
        }
    }

    //Opens and closes the drawer menu.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Creates drawer.
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass = null;
        switch (menuItem.getItemId()) {
            case R.id.nav_about:
                fragmentClass = AboutFragment.class;
                break;
            case R.id.nav_play:
                fragmentClass = PlayFragment.class;
                break;
            case R.id.nav_help:
                fragmentClass = HelpFragment.class;
                break;
            case R.id.nav_stats:
                fragmentClass = StatsFragment.class;
                break;
        }

        if(!menuItem.isChecked()) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Insert the fragment by replacing any existing fragment
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawerLayout.closeDrawers();
    }

    //Dismiss drawer when back button is pressed.
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public static int getSessionScore() {
        return sessionScore;
    }

    public static void setSessionScore(int input) {
        sessionScore = input;
    }

    public static String getPrefsData() {
        return PREFS_DATA;
    }

    //Get the lifetime score from the shared preferences.
    public static int getLifetimeScore(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(ClueActivity.getPrefsData(),
                Context.MODE_PRIVATE);
        int lifetimeScore = prefs.getInt("lifetimeScore", 0);
        return lifetimeScore;
    }

}