package com.dreamwalker.diabetesfoodypilot.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.dreamwalker.diabetesfoodypilot.R;
import com.dreamwalker.diabetesfoodypilot.database.food.MixedFoodItem;
import com.dreamwalker.diabetesfoodypilot.model.AppVersion;
import com.dreamwalker.diabetesfoodypilot.model.MixedFood;
import com.dreamwalker.diabetesfoodypilot.remote.Common;
import com.dreamwalker.diabetesfoodypilot.remote.IAppCheck;
import com.dreamwalker.diabetesfoodypilot.remote.IDatabaseRequest;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InitAppCheckActivity extends AppCompatActivity {

    private static final String TAG = "InitAppCheckActivity";
    private static final String FOOD_FETCH_V2_URL = "mix_food_v1.php";
    private static final String FOOD_FETCH_V3_URL = "food_db_fetch.php";

    @BindView(R.id.animation_view)
    LottieAnimationView lottieAnimationView;
    @BindView(R.id.text_view)
    TextView textView;

    IAppCheck service;

    IDatabaseRequest dbService;

    String versionFoodCode;
    String fetchVersionCode;

    ArrayList<MixedFood> foodsList;

    Realm realm;
    RealmConfiguration realmConfiguration;

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
        realmConfiguration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        realm = Realm.getInstance(realmConfiguration);

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
                            Intent intent = new Intent(InitAppCheckActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();

                        } else {

                            textView.setText("서버로부터 데이터베이스 다운로드 중..");

                            Log.e(TAG, "onResponse: 버전 업그레이드 된 경우   ");
                            // TODO: 2018-08-01 버전 업그레이드 된 경우
                            dbService.fetchTotalFood(FOOD_FETCH_V3_URL).enqueue(new Callback<ArrayList<MixedFood>>() {
                                @Override
                                public void onResponse(Call<ArrayList<MixedFood>> call, Response<ArrayList<MixedFood>> response) {
                                    foodsList.clear();
                                    foodsList.addAll(response.body());
                                    Log.e(TAG, "onResponse: " + foodsList.size());

                                    //realm = Realm.getDefaultInstance();

                                    realm.executeTransactionAsync(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            realm.deleteAll();

                                            MixedFoodItem foodItem = realm.createObject(MixedFoodItem.class);

                                            for (MixedFood f : foodsList) {

                                                foodItem.setDbClass(f.getDbClass());
                                                foodItem.setFoodClass(f.getFoodClass());
                                                foodItem.setFoodName(f.getFoodName());
                                                foodItem.setFoodAmount(f.getFoodAmount());
                                                foodItem.setFoodGroup1(f.getFoodGroup1());
                                                foodItem.setFoodGroup2(f.getFoodGroup2());
                                                foodItem.setFoodGroup3(f.getFoodGroup3());
                                                foodItem.setFoodGroup4(f.getFoodGroup4());
                                                foodItem.setFoodGroup5(f.getFoodGroup5());
                                                foodItem.setFoodGroup6(f.getFoodGroup6());
                                                foodItem.setTotalExchange(f.getTotalExchange());

                                                foodItem.setKcal(f.getKcal());
                                                foodItem.setCarbo(f.getCarbo());
                                                foodItem.setFatt(f.getFatt());
                                                foodItem.setProt(f.getProt());
                                                foodItem.setFiber(f.getFiber());
                                            }
                                        }
                                    }, new Realm.Transaction.OnSuccess() {
                                        @Override
                                        public void onSuccess() {
                                            Paper.book().write("food_version_code", fetchVersionCode);
                                            textView.setText("다운로드 및 저장 완료");
                                            Log.e(TAG, "onSuccess: " + "저장 완료 ");
                                            startActivity(new Intent(InitAppCheckActivity.this, HomeActivity.class));
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
                                public void onFailure(Call<ArrayList<MixedFood>> call, Throwable t) {

                                    Toast.makeText(InitAppCheckActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }

                    } else {

                        textView.setText("서버로부터 데이터베이스 다운로드 중..");
                        Log.e(TAG, "onResponse: 처음 사용자  처리 합니다 ");
                        // TODO: 2018-08-01 처음 사용자 
                        long start = System.currentTimeMillis();
                        dbService.fetchTotalFood(FOOD_FETCH_V3_URL).enqueue(new Callback<ArrayList<MixedFood>>() {
                            @Override
                            public void onResponse(Call<ArrayList<MixedFood>> call, Response<ArrayList<MixedFood>> response) {

                                foodsList.clear();
                                foodsList.addAll(response.body());
                                Log.e(TAG, "onResponse: " + foodsList.size());
                                long end = System.currentTimeMillis();


                                Log.e(TAG, "onResponse: first Phase --> " + (end - start) / 1000.0);
//                                for (Food f : foodsList){
//                                    Log.e(TAG, "onResponse: " + f.getFoodName() );
//                                }

                                long startTwo = System.currentTimeMillis();
//                                for (int i = 0; i < foodsList.size(); i++) {
//                                    realm.beginTransaction();
//                                    FoodItem foodItem = realm.createObject(FoodItem.class);
//                                    foodItem.setFoodNumber(foodsList.get(i).getFoodNumber());
//                                    foodItem.setFoodGroup(foodsList.get(i).getFoodGroup());
//                                    foodItem.setFoodName(foodsList.get(i).getFoodName());
//                                    foodItem.setFoodAmount(foodsList.get(i).getFoodAmount());
//                                    foodItem.setFoodKcal(foodsList.get(i).getFoodKcal());
//                                    foodItem.setFoodCarbo(foodsList.get(i).getFoodCarbo());
//                                    foodItem.setFoodProtein(foodsList.get(i).getFoodProtein());
//                                    foodItem.setFoodFat(foodsList.get(i).getFoodFat());
//                                    foodItem.setFoodSugar(foodsList.get(i).getFoodSugar());
//                                    foodItem.setFoodNatrium(foodsList.get(i).getFoodNatrium());
//                                    foodItem.setFoodCholest(foodsList.get(i).getFoodCholest());
//                                    foodItem.setFoodFatty(foodsList.get(i).getFoodFatty());
//                                    foodItem.setFoodTransFatty(foodsList.get(i).getFoodTransFatty());
//                                    realm.commitTransaction();
//                                }

//                                for (Food f : foodsList) {
//                                    Log.e(TAG, "execute: " + "called" + f.getFoodNumber()  + " | " + f.getFoodKcal());
//                                    foodItem.setFoodNumber(f.getFoodNumber());
//                                    foodItem.setFoodGroup(f.getFoodGroup());
//                                    foodItem.setFoodName(f.getFoodName());
//                                    foodItem.setFoodAmount(f.getFoodAmount());
//                                    foodItem.setFoodKcal(f.getFoodKcal());
//                                    foodItem.setFoodCarbo(f.getFoodCarbo());
//                                    foodItem.setFoodProtein(f.getFoodProtein());
//                                    foodItem.setFoodFat(f.getFoodFat());
//                                    foodItem.setFoodSugar(f.getFoodSugar());
//                                    foodItem.setFoodNatrium(f.getFoodNatrium());
//                                    foodItem.setFoodCholest(f.getFoodCholest());
//                                    foodItem.setFoodFatty(f.getFoodFatty());
//                                    foodItem.setFoodTransFatty(f.getFoodTransFatty());
//                                }


//                                for (int i = 0; i < foodsList.size(); i++){
//                                    int index = i;
//                                    String foodNumber = foodsList.get(i).getFoodNumber();
//                                    String foodGroup = foodsList.get(i).getFoodGroup();
//                                    String foodName = foodsList.get(i).getFoodName();
//                                    String foodAmount = foodsList.get(i).getFoodAmount();
//                                    String foodKcal = foodsList.get(i).getFoodKcal();
//                                    String foodCarbo = foodsList.get(i).getFoodCarbo();
//                                    String foodProtein = foodsList.get(i).getFoodProtein();
//                                    String foodFat = foodsList.get(i).getFoodFat() ;
//                                    String foodSugar = foodsList.get(i).getFoodSugar();
//                                    String foodNatrium = foodsList.get(i).getFoodNatrium();
//                                    String foodCholest = foodsList.get(i).getFoodCholest();
//                                    String foodFatty = foodsList.get(i).getFoodFatty();
//                                    String foodTransFatty = foodsList.get(i).getFoodTransFatty();
//
//
//                                    realm.executeTransactionAsync(new Realm.Transaction() {
//                                        @Override
//                                        public void execute(Realm realm) {
//                                            //realm.deleteAll();
//                                            FoodItem foodItem = realm.createObject(FoodItem.class);
//                                            foodItem.setFoodNumber(foodNumber);
//                                            foodItem.setFoodGroup(foodGroup);
//                                            foodItem.setFoodName(foodName);
//                                            foodItem.setFoodAmount(foodAmount);
//                                            foodItem.setFoodKcal(foodKcal);
//                                            foodItem.setFoodCarbo(foodCarbo);
//                                            foodItem.setFoodProtein(foodProtein);
//                                            foodItem.setFoodFat(foodFat);
//                                            foodItem.setFoodSugar(foodSugar);
//                                            foodItem.setFoodNatrium(foodNatrium);
//                                            foodItem.setFoodCholest(foodCholest);
//                                            foodItem.setFoodFatty(foodFatty);
//                                            foodItem.setFoodTransFatty(foodTransFatty);
//
//
//                                        }
//                                    }, new Realm.Transaction.OnSuccess() {
//                                        @Override
//                                        public void onSuccess() {
//                                            Log.e(TAG, "onSuccess: "  + index );
////                                            long endTwo = System.currentTimeMillis();
////                                            Log.e(TAG, "onResponse: Second Phase --> " + (endTwo - startTwo) / 1000.0);
////                                            Paper.book().write("food_version_code", fetchVersionCode);
////                                            textView.setText("다운로드 및 저장 완료");
////                                            Log.e(TAG, "onSuccess: " + "저장 완료 ");
////                                            startActivity(new Intent(InitAppCheckActivity.this, MainActivity.class));
////                                            finish();
//                                        }
//                                    }, new Realm.Transaction.OnError() {
//                                        @Override
//                                        public void onError(Throwable error) {
//                                            Log.e(TAG, "onError: ");
//                                        }
//                                    });
//                                }

                                new BackgroundTask().execute();
                            }

                            @Override
                            public void onFailure(Call<ArrayList<MixedFood>> call, Throwable t) {
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

    // TODO: 2018-02-27 저장했을 경우와 저장 안했을 경우를 생각해야함
    class BackgroundTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;
        int index = 0;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(InitAppCheckActivity.this);
            //progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("파일 저장중...");
            progressDialog.setCancelable(false);
            progressDialog.setMax(100);
            progressDialog.show();

            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            realmConfiguration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            realm = Realm.getInstance(realmConfiguration);

            //realm = Realm.getDefaultInstance();
            for (int i = 0; i < foodsList.size(); i++) {
                index = i;
                String dbClass = foodsList.get(i).getDbClass();
                String foodClass = foodsList.get(i).getFoodClass();
                String foodName = foodsList.get(i).getFoodName();
                String foodAmount = foodsList.get(i).getFoodAmount();
                String foodGroup1 = foodsList.get(i).getFoodGroup1();
                String foodGroup2 = foodsList.get(i).getFoodGroup2();
                String foodGroup3 = foodsList.get(i).getFoodGroup3();
                String foodGroup4 = foodsList.get(i).getFoodGroup4();
                String foodGroup5 = foodsList.get(i).getFoodGroup5();
                String foodGroup6 = foodsList.get(i).getFoodGroup6();
                String totalExchange = foodsList.get(i).getTotalExchange();
                String kcal = foodsList.get(i).getKcal();
                String carbo = foodsList.get(i).getCarbo();
                String fatt = foodsList.get(i).getFatt();
                String prot = foodsList.get(i).getProt();
                String fiber = foodsList.get(i).getFiber();


//                String foodNumber = foodsList.get(i).getFoodNumber();
//                String foodGroup = foodsList.get(i).getFoodGroup();
//                String foodName = foodsList.get(i).getFoodName();
//                String foodAmount = foodsList.get(i).getFoodAmount();
//                String foodKcal = foodsList.get(i).getFoodKcal();
//                String foodCarbo = foodsList.get(i).getFoodCarbo();
//                String foodProtein = foodsList.get(i).getFoodProtein();
//                String foodFat = foodsList.get(i).getFoodFat();
//                String foodSugar = foodsList.get(i).getFoodSugar();
//                String foodNatrium = foodsList.get(i).getFoodNatrium();
//                String foodCholest = foodsList.get(i).getFoodCholest();
//                String foodFatty = foodsList.get(i).getFoodFatty();
//                String foodTransFatty = foodsList.get(i).getFoodTransFatty();

                realm.executeTransaction(realm -> {
                    MixedFoodItem foodItem = realm.createObject(MixedFoodItem.class);
                    foodItem.setDbClass(dbClass);
                    foodItem.setFoodClass(foodClass);
                    foodItem.setFoodName(foodName);
                    foodItem.setFoodAmount(foodAmount);
                    foodItem.setFoodGroup1(foodGroup1);
                    foodItem.setFoodGroup2(foodGroup2);
                    foodItem.setFoodGroup3(foodGroup3);
                    foodItem.setFoodGroup4(foodGroup4);
                    foodItem.setFoodGroup5(foodGroup5);
                    foodItem.setFoodGroup6(foodGroup6);

                    foodItem.setTotalExchange(totalExchange);

                    foodItem.setKcal(kcal);
                    foodItem.setCarbo(carbo);
                    foodItem.setFatt(fatt);
                    foodItem.setProt(prot);
                    foodItem.setFiber(fiber);

                });
                progressDialog.setProgress((int) (100 * i / foodsList.size()));
                //Log.e(TAG, "doInBackground: " + i );
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            Toast.makeText(InitAppCheckActivity.this, "저장완료", Toast.LENGTH_SHORT).show();
            long endTwo = System.currentTimeMillis();
            //Log.e(TAG, "onResponse: Second Phase --> " + (endTwo - startTwo) / 1000.0);
            Paper.book().write("food_version_code", fetchVersionCode);
            textView.setText("다운로드 및 저장 완료");
            Log.e(TAG, "onSuccess: " + "저장 완료 ");
            startActivity(new Intent(InitAppCheckActivity.this, HomeActivity.class));
            finish();
            //finish();
            super.onPostExecute(aVoid);
        }
    }
}
