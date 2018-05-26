package com.example.mor17_000.eat2fit_app;

/**
 * Created by mor17_000 on 26-May-18.
 */

public class RestaurantDish {
    public String restaurantName;
    public String dishName;
    public String imgUrl;
    public String imgLike;

    public RestaurantDish(String restaurantName, String DishName, String imgUrl) {
        this.restaurantName = restaurantName;
        this.dishName = DishName;
        this.imgUrl = imgUrl;
        this.imgLike = "";
    }
}
