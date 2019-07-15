package us.williamtucker.leaguepulse;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.leagepulse.leaguepulse.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ImageFragment extends Fragment {
    private static final String TAG = "ImageFragment";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_image, container, false);
        initializeVariables(root_view);
        return root_view;
    }
    private void initializeVariables(View root_view) {
        ImageView main_image = root_view.findViewById(R.id.big_image_view);
        ImageView back_arrow = root_view.findViewById(R.id.back_arrow_img);
        ImageView download_icon = root_view.findViewById(R.id.download_icon_img);
        String image_url = null;
        if (getArguments() != null) {
            image_url = getArguments().getString("image_url");
        }else{
            Log.e(TAG, "getArguments is null");
        }
        Picasso.get().load(image_url)
                .placeholder(R.drawable.placeholder2).into(main_image);
        System.out.println(image_url);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Back arrow pressed");
                assert getFragmentManager() != null;
                ImageFragment imageFragment = (ImageFragment)getFragmentManager().findFragmentByTag("IMAGE_FRAGMENT");
                if (imageFragment!=null){
                    if (imageFragment.isVisible()){
                        System.out.println("IS VISIBLE");
                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().remove(imageFragment).commit();
                    }else{
                        System.out.println("IS NOT VISIBLE");
                    }
                }else{
                    System.out.println("IS NULL");
                }
            }
        });
    }
}
