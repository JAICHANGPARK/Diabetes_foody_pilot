package com.dreamwalker.diabetesfoodypilot;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.dreamwalker.diabetesfoodypilot.database.food.FoodItem;
import com.dreamwalker.diabetesfoodypilot.model.AppVersion;
import com.dreamwalker.diabetesfoodypilot.model.Food;
import com.dreamwalker.diabetesfoodypilot.remote.Common;
import com.dreamwalker.diabetesfoodypilot.remote.IAppCheck;
import com.dreamwalker.diabetesfoodypilot.remote.IDatabaseRequest;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InitAppCheckActivity extends AppCompatActivity {

    private static final String TAG = "InitAppCheckActivity";
    private static final String FOOD_FETCH_V2_URL = "food_fetch_total_v2.php";
    IAppCheck service;

    IDatabaseRequest dbService;

    String versionFoodCode;
    String fetchVersionCode;

    ArrayList<Food> foodsList;

    Realm realm;

    @BindView(R.id.animation_view)
    LottieAnimationView lottieAnimationView;
    @BindView(R.id.text_view)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_app_check);
        ButterKnife.bind(this);
        Paper.init(this);
        Realm.init(this);
        //realm = Realm.getDefaultInstance();

        service = Common.getAppcheck();
        dbService = Common.getFoodDatabase();


        foodsList = new ArrayList<>();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        textView.setText("데이터베이스 버전 확인 중..");

        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.e(TAG, "onCreate: " + "Wi-FI 연결됨");
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.e(TAG, "onCreate: " + "모바일 네트워크 연결됨");
            }

            service.checkFoodDatabase().enqueue(new Callback<AppVersion>() {
                @Override
                public void onResponse(Call<AppVersion> call, Response<AppVersion> response) {
                    AppVersion appVersion = response.body();
                    Log.e(TAG, "onResponse: " + appVersion.getSuccess());
                    Log.e(TAG, "onResponse: " + appVersion.getVersion());
                    fetchVersionCode = appVersion.getVersion();

                    versionFoodCode = Paper.book().read("food_version_code");

                    if (versionFoodCode != null) {

                        if (versionFoodCode.equals(appVersion.getVersion())) {
                            Log.e(TAG, "onResponse: 이전 버전 과 같은경우   ");
                            Intent intent = new Intent(InitAppCheckActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {

                            textView.setText("서버로부터 데이터베이스 다운로드 중..");

                            Log.e(TAG, "onResponse: 버전 업그레이드 된 경우   ");
                            // TODO: 2018-08-01 버전 업그레이드 된 경우
                            dbService.fetchTotalFood(FOOD_FETCH_V2_URL).enqueue(new Callback<ArrayList<Food>>() {
                                @Override
                                public void onResponse(Call<ArrayList<Food>> call, Response<ArrayList<Food>> response) {
                                    foodsList.clear();
                                    foodsList.addAll(response.body());
                                    Log.e(TAG, "onResponse: " + foodsList.size());

                                    realm = Realm.getDefaultInstance();

                                    realm.executeTransactionAsync(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            realm.deleteAll();
                                            FoodItem foodItem = realm.createObject(FoodItem.class);

                                            for (Food f : foodsList) {
                                                foodItem.setFoodNumber(f.getFoodNumber());
                                                foodItem.setFoodGroup(f.getFoodGroup());
                                                foodItem.setFoodName(f.getFoodName());
                                                foodItem.setFoodAmount(f.getFoodAmount());
                                                foodItem.setFoodKcal(f.getFoodKcal());
                                                foodItem.setFoodCarbo(f.getFoodCarbo());
                                                foodItem.setFoodProtein(f.getFoodProtein());
                                                foodItem.setFoodFat(f.getFoodFat());
                                                foodItem.setFoodSugar(f.getFoodSugar());
                                                foodItem.setFoodNatrium(f.getFoodNatrium());
                                                foodItem.setFoodCholest(f.getFoodCholest());
                                                foodItem.setFoodFatty(f.getFoodFatty());
                                                foodItem.setFoodTransFatty(f.getFoodTransFatty());
                                            }
                                        }
                                    }, new Realm.Transaction.OnSuccess() {
                                        @Override
                                        public void onSuccess() {
                                            Paper.book().write("food_version_code", fetchVersionCode);
                                            textView.setText("다운로드 및 저장 완료");
                                            Log.e(TAG, "onSuccess: " + "저장 완료 ");
                                            startActivity(new Intent(InitAppCheckActivity.this, MainActivity.class));
                                            finish();
                                        }
                                    }, new Realm.Transaction.OnError() {
                                        @Override
                                        public void onError(Throwable error) {
                                            Log.e(TAG, "onError: ");
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Call<ArrayList<Food>> call, Throwable t) {

                                    Toast.makeText(InitAppCheckActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    } else {

                        textView.setText("서버로부터 데이터베이스 다운로드 중..");
                        Log.e(TAG, "onResponse: 처음 사용자  처리 합니다 ");
                        // TODO: 2018-08-01 처음 사용자 
                        long start = System.currentTimeMillis();
                        dbService.fetchTotalFood(FOOD_FETCH_V2_URL).enqueue(new Callback<ArrayList<Food>>() {
                            @Override
                            public void onResponse(Call<ArrayList<Food>> call, Response<ArrayList<Food>> response) {

                                foodsList.clear();
                                foodsList.addAll(response.body());
                                Log.e(TAG, "onResponse: " + foodsList.size());
                                long end = System.currentTimeMillis();

                                Log.e(TAG, "onResponse: first Phase --> " + (end - start) / 1000.0);

                                realm = Realm.getDefaultInstance();

                                long startTwo = System.currentTimeMillis();
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.deleteAll();

                                        FoodItem foodItem = realm.createObject(FoodItem.class);

                                        for (Food f : foodsList) {
                                            foodItem.setFoodNumber(f.getFoodNumber());
                                            foodItem.setFoodGroup(f.getFoodGroup());
                                            foodItem.setFoodName(f.getFoodName());
                                            foodItem.setFoodAmount(f.getFoodAmount());
                                            foodItem.setFoodKcal(f.getFoodKcal());
                                            foodItem.setFoodCarbo(f.getFoodCarbo());
                                            foodItem.setFoodProtein(f.getFoodProtein());
                                            foodItem.setFoodFat(f.getFoodFat());
                                            foodItem.setFoodSugar(f.getFoodSugar());
                                            foodItem.setFoodNatrium(f.getFoodNatrium());
                                            foodItem.setFoodCholest(f.getFoodCholest());
                                            foodItem.setFoodFatty(f.getFoodFatty());
                                            foodItem.setFoodTransFatty(f.getFoodTransFatty());
                                        }
                                    }
                                }, new Realm.Transaction.OnSuccess() {
                                    @Override
                                    public void onSuccess() {
                                        long endTwo = System.currentTimeMillis();
                                        Log.e(TAG, "onResponse: Second Phase --> " + (endTwo - startTwo) / 1000.0);
                                        Paper.book().write("food_version_code", fetchVersionCode);
                                        textView.setText("다운로드 및 저장 완료");
                                        Log.e(TAG, "onSuccess: " + "저장 완료 ");
                                        startActivity(new Intent(InitAppCheckActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }, new Realm.Transaction.OnError() {
                                    @Override
                                    public void onError(Throwable error) {
                                        Log.e(TAG, "onError: ");
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call<ArrayList<Food>> call, Throwable t) {
                                Toast.makeText(InitAppCheckActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<AppVersion> call, Throwable t) {
                    Toast.makeText(InitAppCheckActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {

            Toast.makeText(this, "네트워크 연결 없음", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("알림");
            builder.setMessage("인터넷 연결이 필요합니다.");
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> finish());

            builder.show();

        }


    }
}
