package com.dreamwalker.diabetesfoodypilot.remote;

import com.dreamwalker.diabetesfoodypilot.model.Food;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IDatabaseRequest {

    @GET("food/{name}")
    Call<ArrayList<Food>> fetchTotalFood(@Path("name") String foodKind);


}
