package com.dreamwalker.diabetesfoodypilot.remote;

import com.dreamwalker.diabetesfoodypilot.model.MixedFood;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IDatabaseRequest {

    @GET("food/{name}")
    Call<ArrayList<MixedFood>> fetchTotalFood(@Path("name") String foodKind);


}
