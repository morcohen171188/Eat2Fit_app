package com.example.mor17_000.eat2fit_app;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.mor17_000.eat2fit_app.ImageLoader.imageLoadFromWeb;

/**
 * Created by mor17_000 on 26-May-18.
 */

public class DishesAdapter extends ArrayAdapter<RestaurantDish> {
    // View lookup cache
    private static class ViewHolder {
        TextView dishName;
        TextView restName;
        ImageView dishImg;
        ImageView likeImg;
    }

    public DishesAdapter(Context context, ArrayList<RestaurantDish> dishes) {
        super(context, R.layout.dish_item, dishes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        RestaurantDish Dish = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.dish_item, parent, false);
            viewHolder.dishName = (TextView) convertView.findViewById(R.id.tvDishName);
            viewHolder.restName = (TextView) convertView.findViewById(R.id.tvRestName);
            viewHolder.dishImg = (ImageView) convertView.findViewById(R.id.imgDish);
            viewHolder.likeImg = (ImageView) convertView.findViewById(R.id.imgLike);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.dishName.setText(Dish.dishName);
        viewHolder.restName.setText(Dish.restaurantName);
        imageLoadFromWeb(Dish.imgUrl,viewHolder.dishImg);
        if(Dish.imgLike.equals("LIKE")){
            viewHolder.likeImg.setImageResource(R.drawable.like);
        }else if (Dish.imgLike.equals("DISLIKE")){
            viewHolder.likeImg.setImageResource(R.drawable.dislike);
        }else{
            viewHolder.likeImg.setImageResource(R.drawable.transparent);
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
