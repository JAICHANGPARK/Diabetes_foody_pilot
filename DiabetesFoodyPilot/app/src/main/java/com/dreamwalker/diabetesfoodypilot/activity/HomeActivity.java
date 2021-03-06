package com.dreamwalker.diabetesfoodypilot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.dreamwalker.diabetesfoodypilot.R;
import com.dreamwalker.diabetesfoodypilot.activity.diary.VoiceRecordActivity;
import com.dreamwalker.diabetesfoodypilot.adapter.HomeTestAdapter;
import com.dreamwalker.diabetesfoodypilot.adapter.HomeTestAdapterV2;
import com.dreamwalker.diabetesfoodypilot.database.food.FoodCard;
import com.dreamwalker.diabetesfoodypilot.database.food.FoodDock;
import com.dreamwalker.diabetesfoodypilot.database.food.FoodTotal;
import com.dreamwalker.diabetesfoodypilot.model.HomeFood;
import com.dreamwalker.diabetesfoodypilot.utils.VerticalSpaceItemDecoration;
import com.dreamwalker.spacebottomnav.SpaceItem;
import com.dreamwalker.spacebottomnav.SpaceNavigationView;
import com.dreamwalker.spacebottomnav.SpaceOnClickListener;
import com.dreamwalker.spacebottomnav.SpaceOnLongClickListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    @BindView(R.id.space)
    SpaceNavigationView spaceNavigationView;

//    @BindView(R.id.list_view)
//    ListView listView;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.animation_view)
    LottieAnimationView animationView;
    @BindView(R.id.animation_layout)
    LinearLayout animationLinearLayout;

    Realm realm;
    RealmConfiguration realmConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setSpaceNavigationView(savedInstanceState);

        Date testDate = new Date();
        Log.e(TAG, "onCreate: testDate" + testDate);
        Timestamp timestamp = new Timestamp(testDate.getTime());
        Log.e(TAG, "onCreate: timestamp.getTime()" + timestamp.getTime());

        Realm.init(this);
        realmConfiguration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        realm = Realm.getInstance(realmConfiguration);
//        realm = Realm.getDefaultInstance();

        RealmResults<FoodDock> result = realm.where(FoodDock.class).findAll();
        Log.e(TAG, "전제 데이터베이스 Size -->" + result.size());

        if (result.size() == 0) {
            animationLinearLayout.setVisibility(View.VISIBLE);
            animationView.setVisibility(View.VISIBLE);
            animationView.playAnimation();
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            animationLinearLayout.setVisibility(View.GONE);
            animationView.setVisibility(View.GONE);
            animationView.cancelAnimation();
        }

        List<FoodDock> foodDockArrayList = realm.copyFromRealm(result);
        List<RealmList<FoodTotal>> foodTotalList = new ArrayList<>();
        List<RealmList<FoodCard>> foodCardList = new ArrayList<>();

        ArrayList<HomeFood> homeFoods = new ArrayList<>();

        for (int i = 0; i < result.size(); i++) {
            homeFoods.add(new HomeFood(foodDockArrayList.get(i).getFoodTotals(),
                    foodDockArrayList.get(i).getSaveDate(),
                    foodDockArrayList.get(i).getUserSelectDate(),
                    foodDockArrayList.get(i).getStartIntakeDate(),
                    foodDockArrayList.get(i).getEndIntakeDate(),
                    foodDockArrayList.get(i).getTimestamp()));

        }

//        homeFoods.get(0).setRequestBtnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
////                ((FoldingCell) view).toggle(false);
////                Toast.makeText(getApplicationContext(), "CUSTOM HANDLER FOR FIRST BUTTON", Toast.LENGTH_SHORT).show();
//            }
//        });

        // create custom adapter that holds elements and their state (we need hold a id's of unfolded elements for reusable elements)
        final HomeTestAdapter adapter = new HomeTestAdapter(this, homeFoods);
        HomeTestAdapterV2 homeTestAdapterV2 = new HomeTestAdapterV2(this, homeFoods);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(10));
        recyclerView.setAdapter(homeTestAdapterV2);

//        // add default btn handler for each request btn on each item if custom handler not found
//        adapter.setDefaultRequestBtnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "DEFAULT HANDLER FOR ALL BUTTONS", Toast.LENGTH_SHORT).show();
//            }
//        });

//        // set elements to adapter
//        listView.setAdapter(adapter);
//
//        // set on click event listener to list view
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
////                 toggle clicked cell state
//                ((FoldingCell) view).toggle(false);
//                // register in adapter that state for selected cell is toggled
//                adapter.registerToggle(pos);
//            }
//        });


        // TODO: 2018-08-20 FoodDock 모델에서 FoodTotal 리스트 값을 가져온다.
        for (int i = 0; i < foodDockArrayList.size(); i++) {
            Log.e(TAG, "onCreate: Big Count i --> " + i);
            Log.e(TAG, "foodDockArrayList: getFoodTotals->" + foodDockArrayList.get(i).getFoodTotals());
            Log.e(TAG, "foodDockArrayList: getSaveDate->" + foodDockArrayList.get(i).getSaveDate());
            Log.e(TAG, "foodDockArrayList: getTimestamp->" + foodDockArrayList.get(i).getTimestamp());
            RealmList<FoodTotal> tmpFoodTotalList = foodDockArrayList.get(i).getFoodTotals(); // 1개의 푸드 독 안의 리스트를 가져온다.
            for (int k = 0; k < tmpFoodTotalList.size(); k++) {
                Log.e(TAG, "onCreate: mid Count k --> " + k);
                Log.e(TAG, "tmpFoodTotalList: " + tmpFoodTotalList.get(k).getIntakeType());
                RealmList<FoodCard> tmpFoodCardList = tmpFoodTotalList.get(k).getFoodCardArrayList(); // 1개의 푸드 포탈 리스트 안의 푸트 카드 리스트를 가져온다.
                for (int j = 0; j < tmpFoodCardList.size(); j++) {
                    Log.e(TAG, "onCreate: small Count j --> " + j);
                    Log.e(TAG, "tmpFoodCardList: " + tmpFoodCardList.get(j).getCardClass());
                    Log.e(TAG, "tmpFoodCardList: " + tmpFoodCardList.get(j).getFoodName());
                    Log.e(TAG, "tmpFoodCardList: " + tmpFoodCardList.get(j).getFoodAmount());
                    Log.e(TAG, "tmpFoodCardList: " + tmpFoodCardList.get(j).getKcal());
                }
            }
//            realm.copyFromRealm(foodDockArrayList.get(i).getFoodTotals());
            foodTotalList.add(foodDockArrayList.get(i).getFoodTotals());
        }

//        for (int i = 0; i < foodTotalList.size(); i++) {
//
//            Log.e(TAG, "onCreate: count i->" + i);
//            Log.e(TAG, "onCreate: " + foodTotalList.get(i));
////            Log.e(TAG, "onCreate: " + foodTotalList.get(i));
//            RealmList<FoodTotal> fT = foodTotalList.get(i);
//
//            for (int k = 0; k < fT.size(); k++) {
//                Log.e(TAG, "onCreate: fT " + fT.get(k).getIntakeType());
//                foodCardList.add(fT.get(k).getFoodCardArrayList());
//            }
//
////            Log.e(TAG, "onCreate: " + "Total Count "  +  foodTotalList.get(i).get(i).getIntakeType());
////            Log.e(TAG, "onCreate: " + "Total Count "  +  foodTotalList.get(i).get(i).getFoodCardArrayList().get(i).getCardClass());
////            Log.e(TAG, "onCreate: " + "Total Count "  +  foodTotalList.get(i).get(i).getFoodCardArrayList().get(i).getFoodName());
////            Log.e(TAG, "onCreate: " + "Total Count "  +  foodTotalList.get(i).get(i).getFoodCardArrayList().get(i).getFoodClass());
////            Log.e(TAG, "onCreate: " + "Total Count "  +  foodTotalList.get(i).get(i).getFoodCardArrayList().get(i).getFoodAmount());
////            foodCardList.add(foodTotalList.get(i).get(i).getFoodCardArrayList());
//        }
//
//        for (int i = 0; i < foodCardList.size(); i++) {
//            RealmList<FoodCard> fC = foodCardList.get(i);
//            for (int k = 0; k < fC.size(); k++) {
//                Log.e(TAG, "onCreate: fC " + fC.get(k).getFoodName());
//            }
////            Log.e(TAG, "onCreate: " + "Total Count "  +  foodCardList.get(i).get(i).getFoodName());
////
////            foodCardList.add(foodTotalList.get(i).get(i).getFoodCardArrayList());
//        }
    }

    private void setSpaceNavigationView(Bundle sis) {
        spaceNavigationView.initWithSaveInstanceState(sis);
        spaceNavigationView.setCentreButtonIcon(R.drawable.ic_add_white_24dp);
        spaceNavigationView.addSpaceItem(new SpaceItem("HOME", R.drawable.ic_home_white_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("SEARCH", R.drawable.ic_search_white_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("CHART", R.drawable.ic_bubble_chart_white_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("PROFILE", R.drawable.ic_person_outline_white_24dp));
        spaceNavigationView.shouldShowFullBadgeText(true);
        spaceNavigationView.setCentreButtonIconColorFilterEnabled(false);
        spaceNavigationView.showIconOnly();

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Log.d("onCentreButtonClick ", "onCentreButtonClick");
                spaceNavigationView.shouldShowFullBadgeText(true);
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("기록하기");
                builder.setMessage("음성 메모하시겠어요? 기록버튼을 길게 누르면 식단기록이 가능합니다.");

                builder.setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    dialog.dismiss();
                    Intent intent = new Intent(HomeActivity.this,  VoiceRecordActivity.class);
                    startActivity(intent);


                });

                builder.setNegativeButton(android.R.string.no, (dialog, which) -> {
                    dialog.dismiss();
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(intent);

                });

                builder.setNeutralButton("섭식기록하기", ((dialog, which) -> {
                    dialog.dismiss();
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(intent);

                }));
                builder.show();
//                Snackbar.make(getWindow().getDecorView().getRootView(), "길게눌러 기록하기", Snackbar.LENGTH_SHORT).setAction(android.R.string.ok, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                    }
//                }).show();
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {

                Log.d("onItemClick ", "" + itemIndex + " " + itemName);
                switch (itemIndex) {
                    case 0:
                        break;
                    case 1:
                        Toast.makeText(HomeActivity.this, "공사중--업데이트 예정이에요", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(HomeActivity.this, DBSearchActivity.class));
                        break;
                    case 2:
                        Toast.makeText(HomeActivity.this, "공사중--업데이트 예정이에요", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(HomeActivity.this, DBSearchActivity.class));
                        break;
                    case 3:
//                        Toast.makeText(HomeActivity.this, "공사중--업데이트 예정이에요", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(HomeActivity.this, ProfileHomeActivity.class));
                        finish();
                        break;
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Log.d("onItemReselected ", "" + itemIndex + " " + itemName);
                switch (itemIndex) {
                    case 0:
                        break;
                    case 1:
                        Toast.makeText(HomeActivity.this, "공사중--업데이트 예정이에요", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(HomeActivity.this, DBSearchActivity.class));
                        break;
                    case 2:
                        Toast.makeText(HomeActivity.this, "공사중--업데이트 예정이에요", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(HomeActivity.this, DBSearchActivity.class));
                        break;
                    case 3:
//                        Toast.makeText(HomeActivity.this, "공사중--업데이트 예정이에요", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(HomeActivity.this, DBSearchActivity.class));
                        startActivity(new Intent(HomeActivity.this, ProfileHomeActivity.class));
                        finish();
                        break;
                }
            }
        });

        spaceNavigationView.setSpaceOnLongClickListener(new SpaceOnLongClickListener() {
            @Override
            public void onCentreButtonLongClick() {
//                Toast.makeText(MainActivity.this, "onCentreButtonLongClick", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onItemLongClick(int itemIndex, String itemName) {
                Toast.makeText(HomeActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        spaceNavigationView.onSaveInstanceState(outState);
    }
}
