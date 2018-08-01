package com.dreamwalker.diabetesfoodypilot.remote;

import com.dreamwalker.diabetesfoodypilot.model.TestModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IMenuRequest {


    @GET
    Call<List<TestModel>> getMenuList(@Url String url);



}
