package us.williamtucker.leaguepulse;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.leagepulse.leaguepulse.R;

public class MainActivity extends AppCompatActivity {
    final Fragment homeFragment = new HomeFragment();
    final Fragment settingsFragment = new SettingsFragment();
    final Fragment aboutFragment = new AboutFragment();
    final FragmentManager fManager = getSupportFragmentManager();
    private static final String TAG = "MainActivity";

    Fragment activeFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("Main activity oncreate");
        if (activeFragment == null) {
            System.out.println("IN HERE1");
            fManager.beginTransaction().add(R.id.main_frame_layout, aboutFragment, "3")
                    .hide(aboutFragment).commit();
            System.out.println("IN HERE 2");
            fManager.beginTransaction().add(R.id.main_frame_layout, settingsFragment, "2")
                    .hide(settingsFragment).commit();
            System.out.println("IN HERE 3");
            fManager.beginTransaction().add(R.id.main_frame_layout, homeFragment, "1").commit();
            activeFragment = homeFragment;
        } else {
            Log.e(TAG,"ACTIVE FRAGMENT IS NOT NULL IN HERE1");
            fManager.beginTransaction().hide(activeFragment).show(activeFragment).commit();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        /*getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,
                new HomeFragment()).commit();*/
    }
    public BottomNavigationView getBotNavView(){
        return findViewById(R.id.bottom_navigation);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav, menu);
        return true;
    }*/

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    //  Fragment selectedFragment = null;
                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            fManager.beginTransaction().hide(activeFragment)
                                    .show(homeFragment).commit();

                            activeFragment = homeFragment;
                            //selectedFragment = new HomeFragment();
                            return true;
                        case R.id.nav_settings:
                            fManager.beginTransaction().hide(activeFragment)
                                    .show(settingsFragment).commit();

                            activeFragment = settingsFragment;
                            //selectedFragment = new SettingsFragment();
                            return true;
                        case R.id.nav_about:
                            fManager.beginTransaction().hide(activeFragment)
                                    .show(aboutFragment).commit();

                            activeFragment = aboutFragment;
                            // selectedFragment = new AboutFragment();
                            return true;
                    }
                   /* assert selectedFragment != null;
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,
                            selectedFragment).commit();*/
                    return false;
                }
            };

   /* @Override
    protected void onUserLeaveHint() {
        System.out.println("userleave called");
        fManager.beginTransaction().hide(activeFragment).commit();
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        fManager.beginTransaction().hide(activeFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fManager.beginTransaction().show(activeFragment).commit();
    }

    @Override
    public void onBackPressed() {
        System.out.println("onbackpressed");
        Intent i = new Intent();
        i.setAction(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        this.startActivity(i);
    }
}
