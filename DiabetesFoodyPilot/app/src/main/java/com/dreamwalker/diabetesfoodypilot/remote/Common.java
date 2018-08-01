package com.dreamwalker.diabetesfoodypilot.remote;

public class Common {
    public static final String BASE_URL = "http://kangwonelec.com/";
    public static IMenuRequest getMenuRequest(){
        return RetrofitClient.getClient("https://api.androidhive.info/").create(IMenuRequest.class);
    }

    public static IAppCheck getAppcheck(){
        return RetrofitClient.getClient(BASE_URL).create(IAppCheck.class);
    }

    public static IDatabaseRequest getFoodDatabase(){
        return RetrofitClient.getClient(BASE_URL).create(IDatabaseRequest.class);
    }
}
