package lk.sheha.agriconnect.model;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

import lk.sheha.agriconnect.MainLogin;
import lk.sheha.agriconnect.OnboardActivity;
import lk.sheha.agriconnect.R;

public class IntroAdapter extends PagerAdapter {

    Context context;
    ViewPager pgv;
    List<ScreenItem> mlListArray;

    public IntroAdapter(Context context, ViewPager pgv, List<ScreenItem> mlListArray) {
        this.context = context;
        this.pgv = pgv;
        this.mlListArray = mlListArray;
    }



    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = inflater.inflate(R.layout.vid_screen,null);

        VideoView videoView =  layoutScreen.findViewById(R.id.videoView2);
        String videoPath = "android.resource://"+ context.getPackageName()+"/"+mlListArray.get(position).imageNumber;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        TextView Titleview = layoutScreen.findViewById(R.id.textView3);
        Titleview.setText(mlListArray.get(position).getTitle());

        TextView descrip = layoutScreen.findViewById(R.id.textView4);
        descrip.setText(mlListArray.get(position).getDes());

        Button b = layoutScreen.findViewById(R.id.button_pre_order);
        if(position==2){
            b.setText("Go Home");
        }

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                videoView.start();

            }

        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position < 2) {
                    pgv.setCurrentItem(position + 1);

                }
                if (position==2){

                    Intent i = new Intent(view.getContext(), MainLogin.class);
                    view.getContext().startActivity(i);
                    ((OnboardActivity) view.getContext()).finish();
                }


            }
        });
        container.addView(layoutScreen);


        return layoutScreen;
    }

    @Override
    public int getCount() {
        return mlListArray.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
