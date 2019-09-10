package us.williamtucker.leaguepulse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.leagepulse.leaguepulse.R;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_settings, container, false);
        initVar(root_view);

        return root_view;

    }

    private void initVar(View root_view) {
       /// Toolbar toolbar = root_view.findViewById(R.id.settings_toolbar);
       /// ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
       /// Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle("Customize Alerts");
        SharedPreferences sharedPreferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences("shared preferences", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        RadioGroup reddit_radio_group = root_view.findViewById(R.id.reddit_radio_group);
        RadioGroup twitter_radio_group = root_view.findViewById(R.id.twitter_radio_group);
        AppCompatCheckBox na_checkbox = root_view.findViewById(R.id.na_twitter_checkbox);
        AppCompatCheckBox eu_checkbox = root_view.findViewById(R.id.eu_twitter_checkbox);
        SwitchCompat reddit_switch = root_view.findViewById(R.id.reddit_switch);
        SwitchCompat twitter_switch = root_view.findViewById(R.id.twitter_switch);
        SwitchCompat match_results_switch = root_view.findViewById(R.id.match_results_switch);
        SwitchCompat night_light_switch = root_view.findViewById(R.id.night_light_switch);

        if (Objects.requireNonNull(sharedPreferences.getString("reddit_alert_option", "top")).equals("all")) {
            reddit_radio_group.check(R.id.all_trending_reddit_radio);
        } else {
            reddit_radio_group.check(R.id.top_trending_reddit_radio);
        }
        if (Objects.requireNonNull(sharedPreferences.getString("twitter_alert_option", "top")).equals("all")) {
            twitter_radio_group.check(R.id.all_trending_twitter_radio);
        } else {
            twitter_radio_group.check(R.id.top_trending_twitter_radio);
        }

        if (sharedPreferences.getBoolean("na_region_checked", false)) {
            na_checkbox.setChecked(true);
        }
        if (sharedPreferences.getBoolean("eu_region_checked", false)) {
            eu_checkbox.setChecked(true);
        }

        if (sharedPreferences.getBoolean("receive_twitter_alerts", true)) {
            twitter_switch.setChecked(false);
        }else{
            twitter_switch.setChecked(true);
        }
        System.out.println("Receive reddit alerts: " + sharedPreferences.getBoolean("receive_reddit_alerts", true));

        if (sharedPreferences.getBoolean("receive_reddit_alerts", true)) {
            reddit_switch.setChecked(false);
        } else {
            reddit_switch.setChecked(true);
        }

        if (sharedPreferences.getBoolean("receive_match_alerts", true)) {
            match_results_switch.setChecked(false);
        } else {
            match_results_switch.setChecked(true);
        }
        System.out.println("Settings fragment night_light_enabled: " + sharedPreferences.getBoolean("night_light_enabled", false));
        if (sharedPreferences.getBoolean("night_light_enabled", false)){
            night_light_switch.setChecked(true);
        }else{
            night_light_switch.setChecked(false);
        }
        reddit_radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // This puts the value (true/false) into the variable
                if (checkedId == R.id.top_trending_reddit_radio) {
                    System.out.println("The redio button just pressed is top_trending_posts");
                    editor.putString("reddit_alert_option", "top");
                    editor.apply();
                } else {
                    editor.putString("reddit_alert_option", "all");
                    editor.apply();
                    System.out.println("The redio button just pressed is all_trending_posts");
                }
            }
        });
        twitter_radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.top_trending_twitter_radio) {
                    System.out.println("The redio button just pressed is top_twitter_posts");
                    editor.putString("twitter_alert_option", "top");
                    editor.apply();
                } else {
                    editor.putString("twitter_alert_option", "all");
                    editor.apply();
                }
            }
        });
        eu_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    System.out.println("eu checkbox has been checked");
                    editor.putBoolean("eu_region_checked", true);
                    editor.apply();
                } else {
                    System.out.println("eu checkbox has been unchecked");
                    editor.putBoolean("eu_region_checked", false);
                    editor.apply();
                }
            }
        });
        na_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    System.out.println("na checkbox has been checked");
                    editor.putBoolean("na_region_checked", true);
                    editor.apply();
                } else {
                    System.out.println("na checkbox has been unchecked");
                    editor.putBoolean("na_region_checked", false);
                    editor.apply();
                }
            }
        });
        match_results_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    System.out.println("match switch has been turned on");
                    editor.putBoolean("receive_match_alerts", false);
                    editor.apply();
                } else {
                    System.out.println("match switch has been turned off");
                    editor.putBoolean("receive_match_alerts", true);
                    editor.apply();
                }
            }
        });
        reddit_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    System.out.println("reddit switch has been turned on");
                    editor.putBoolean("receive_reddit_alerts", false);
                    editor.apply();
                } else {
                    System.out.println("reddit switch has been turned off");
                    editor.putBoolean("receive_reddit_alerts", true);
                    editor.apply();
                }
            }
        });
        twitter_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    System.out.println("twitter switch has been turned on");
                    editor.putBoolean("receive_twitter_alerts", false);
                    editor.apply();
                } else {
                    System.out.println("twitter switch has been turned off");
                    editor.putBoolean("receive_twitter_alerts", true);
                    editor.apply();
                }
            }
        });
        night_light_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    System.out.println("Night light has been turned on");
                    editor.putBoolean("night_light_enabled", true);
                    editor.apply();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                    startActivity(new Intent(getActivity(), SplashActivity.class));
                    Objects.requireNonNull(getActivity()).finish();
                }else{
                    System.out.println("Night light has been turned off");
                    editor.putBoolean("night_light_enabled", false);
                    editor.apply();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                    startActivity(new Intent(getActivity(), SplashActivity.class));
                    Objects.requireNonNull(getActivity()).finish();
                }

            }
        });
    }
}
