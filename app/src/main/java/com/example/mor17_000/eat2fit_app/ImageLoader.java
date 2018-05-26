package com.example.mor17_000.eat2fit_app;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by mor17_000 on 25-May-18.
 */

public class ImageLoader {
    public static void imageLoadFromWeb(String url, ImageView imgView){
        Picasso.get()
                .load(url)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.meal)
                .error(R.drawable.meal)
                .into(imgView);
    }
}
