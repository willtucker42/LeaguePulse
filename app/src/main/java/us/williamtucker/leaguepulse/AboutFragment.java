package us.williamtucker.leaguepulse;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leagepulse.leaguepulse.R;

import java.util.Objects;

public class AboutFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_about, container, false);
        initVar(root_view);
        return root_view;
    }
    private void initVar(View root_view){
        RelativeLayout about_main_layout = root_view.findViewById(R.id.about_main_layout);
        TextView about_text = root_view.findViewById(R.id.about_text);
        TextView coming_soon_text = root_view.findViewById(R.id.coming_soon_text);

        if (Objects.requireNonNull(getActivity()).getSharedPreferences("shared preferences",
                Context.MODE_PRIVATE).getBoolean("night_light_enabled", false)){
            about_main_layout.setBackgroundColor(Color.parseColor("#000000"));
            about_text.setTextColor(Color.parseColor("#ACACAC"));
            coming_soon_text.setTextColor(Color.parseColor("#ACACAC"));
        }
    }
}
