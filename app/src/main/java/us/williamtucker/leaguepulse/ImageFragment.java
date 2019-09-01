package us.williamtucker.leaguepulse;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.leagepulse.leaguepulse.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

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
        //ImageView download_icon = root_view.findViewById(R.id.download_icon_img);
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
        final String finalImage_url = image_url;

    }
    private static void downloadImage(String image_url, Context context, String twitter_name){
        Random random = new Random();
        String filename = twitter_name+random.nextInt(10000);
        Picasso.get().load(image_url).into(picassoImageTarget(context, "imageDir", filename));
        ;
    }
    private static Target getTarget(final String filename, final Context context){
        Target target = new Target(){

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        String folderPath = Environment.getExternalStorageDirectory() + "/LeaguePulse";
                        File folder = new File(folderPath);


                        /*File file = new File(Environment.getExternalStorageDirectory()
                                .getPath()+"/"+filename);*/
                        if (!folder.exists()) {
                            File imageDirectory = new File(folderPath);
                            imageDirectory.mkdirs();
                        }
                        File newFile = new File(folderPath, filename);
                        try {
                            Looper.prepare();
                            System.out.println("Downloading image?...");
                            FileOutputStream ostream = new FileOutputStream(newFile);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                            ostream.flush();
                            ostream.close();
                            Toast.makeText(context, "Image Saved", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Log.e("IOException", e.toString());
                            Toast.makeText(context, "Image save failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).start();

            }
            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        return target;
    }
    private static Target picassoImageTarget(Context context, final String imageDir, final String imageName) {
       // Log.d("picassoImageTarget", " picassoImageTarget");
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir(imageDir, Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final File myImageFile = new File(directory, imageName); // Create image file
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(myImageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (fos != null) {
                                    fos.close();
                                }else{
                                    Log.e(TAG,"file outputstream is null");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.i("image", "image saved to >>>" + myImageFile.getAbsolutePath());

                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
               // if (placeHolderDrawable != null) {}
            }
        };
    }
}
