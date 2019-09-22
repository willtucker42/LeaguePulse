package us.williamtucker.leaguepulse;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.leagepulse.leaguepulse.R;
import com.parse.ParseException;
import com.parse.ParseObject;

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
        RelativeLayout activity_main_layout= findViewById(R.id.activity_main_layout);;
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
            Log.e(TAG, "ACTIVE FRAGMENT IS NOT NULL IN HERE1");
            fManager.beginTransaction().hide(activeFragment).show(activeFragment).commit();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        if (getSharedPreferences("shared preferences", MODE_PRIVATE)
                .getBoolean("night_light_enabled", false)){
            bottomNavigationView.setBackgroundColor(Color.parseColor("#252525"));
        }else{
            bottomNavigationView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        /*getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,
                new HomeFragment()).commit();*/
    }

    public BottomNavigationView getBotNavView() {
        return findViewById(R.id.bottom_navigation);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav, menu);
        return true;
    }*/

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send_bug_report:
                androidx.appcompat.app.AlertDialog.Builder builder = new
                        androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);

                //AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                //AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                @SuppressLint("InflateParams")
                final View alertView = getLayoutInflater().inflate(R.layout.send_bug_report_laout,
                        null);
                builder.setTitle("Report a bug");

                builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("Send email", "");
                        ParseObject bug = new ParseObject("LeaguePulseReports");
                        EditText bugText = alertView.findViewById(R.id.editText);
                        final String bugDesc = bugText.getText().toString();
                        if (!bugDesc.equals("")) {
                            bug.put("bug_reports", bugDesc);
                            try {
                                bug.save();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Bug report sent.",
                                    Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(MainActivity.this, "Text is empty", Toast.LENGTH_LONG).show();
                        }
                    }

                });
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setView(alertView);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            case R.id.send_suggestion:
                androidx.appcompat.app.AlertDialog.Builder suggestion_builder = new
                        androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);

                //AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                //AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final View suggestion_alertView = getLayoutInflater().inflate(R.layout.send_bug_report_laout,
                        null);
                suggestion_builder.setTitle("Submit a suggestion");

                suggestion_builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("Send email", "");
                        ParseObject suggestion = new ParseObject("LeaguePulseReports");
                        EditText suggestion_text = suggestion_alertView.findViewById(R.id.editText);
                        final String suggestion_desc = suggestion_text.getText().toString();
                        if (!suggestion_desc.equals("")) {
                            suggestion.put("suggestions", suggestion_desc);
                            try {
                                suggestion.save();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Suggestion sent!",
                                    Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(MainActivity.this, "Text is empty", Toast.LENGTH_LONG).show();
                        }
                    }

                });
                suggestion_builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                suggestion_builder.setView(suggestion_alertView);
                AlertDialog suggestion_dialog = suggestion_builder.create();
                suggestion_dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
