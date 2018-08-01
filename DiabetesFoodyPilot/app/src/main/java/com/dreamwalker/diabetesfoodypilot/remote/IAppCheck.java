package com.dreamwalker.diabetesfoodypilot.remote;

import com.dreamwalker.diabetesfoodypilot.model.AppVersion;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IAppCheck {

    @GET("version_factory/check_version.php")
    Call<AppVersion> checkFoodDatabase();
}
