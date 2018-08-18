package com.dreamwalker.diabetesfoodypilot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.dreamwalker.diabetesfoodypilot.R;
import com.dreamwalker.diabetesfoodypilot.database.food.FoodCard;
import com.dreamwalker.diabetesfoodypilot.database.food.FoodDock;
import com.dreamwalker.diabetesfoodypilot.database.food.FoodTotal;
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
import io.realm.RealmList;
import io.realm.RealmResults;

public class HomeActivity extends AppCompatActivity {


    private static final String TAG = "HomeActivity";
    @BindView(R.id.space)
    SpaceNavigationView spaceNavigationView;

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setSpaceNavigationView(savedInstanceState);

        Date testDate = new Date();
        Log.e(TAG, "onCreate: " + testDate);
        Timestamp timestamp = new Timestamp(testDate.getTime());
        Log.e(TAG, "onCreate: " + timestamp.getTime());

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        RealmResults<FoodDock> result = realm.where(FoodDock.class).findAll();
        Log.e(TAG, "onCreate: " + result.size());

        List<FoodDock> foodDockArrayList = realm.copyFromRealm(result);
        List<RealmList<FoodTotal>> foodTotalList = new ArrayList<>();
        List<RealmList<FoodCard>> foodCardList = new ArrayList<>();

        for (int i = 0; i < foodDockArrayList.size(); i++) {
            Log.e(TAG, "onCreate:1 " + foodDockArrayList.get(i).getFoodTotals());
            Log.e(TAG, "onCreate:2 " + foodDockArrayList.get(i).getSaveDate());
            Log.e(TAG, "onCreate: 3" + foodDockArrayList.get(i).getTimestamp());
//            realm.copyFromRealm(foodDockArrayList.get(i).getFoodTotals());
            foodTotalList.add(foodDockArrayList.get(i).getFoodTotals());
        }

        for (int i = 0; i < foodTotalList.size(); i++) {

            Log.e(TAG, "onCreate: " + i);
            Log.e(TAG, "onCreate: " + foodTotalList.get(i));
//            Log.e(TAG, "onCreate: " + foodTotalList.get(i));
            RealmList<FoodTotal> fT = foodTotalList.get(i);

            for (int k = 0; k < fT.size(); k++) {
                Log.e(TAG, "onCreate: fT " + fT.get(k).getIntakeType());
                foodCardList.add(fT.get(k).getFoodCardArrayList());
            }

//            Log.e(TAG, "onCreate: " + "Total Count "  +  foodTotalList.get(i).get(i).getIntakeType());
//            Log.e(TAG, "onCreate: " + "Total Count "  +  foodTotalList.get(i).get(i).getFoodCardArrayList().get(i).getCardClass());
//            Log.e(TAG, "onCreate: " + "Total Count "  +  foodTotalList.get(i).get(i).getFoodCardArrayList().get(i).getFoodName());
//            Log.e(TAG, "onCreate: " + "Total Count "  +  foodTotalList.get(i).get(i).getFoodCardArrayList().get(i).getFoodClass());
//            Log.e(TAG, "onCreate: " + "Total Count "  +  foodTotalList.get(i).get(i).getFoodCardArrayList().get(i).getFoodAmount());
//            foodCardList.add(foodTotalList.get(i).get(i).getFoodCardArrayList());
        }

        for (int i = 0; i < foodCardList.size(); i++) {
            RealmList<FoodCard> fC = foodCardList.get(i);
            for (int k = 0; k < fC.size(); k++) {
                Log.e(TAG, "onCreate: fC " + fC.get(k).getFoodName());
            }
//            Log.e(TAG, "onCreate: " + "Total Count "  +  foodCardList.get(i).get(i).getFoodName());
//
//            foodCardList.add(foodTotalList.get(i).get(i).getFoodCardArrayList());
        }


    }

    private void setSpaceNavigationView(Bundle sis) {
        spaceNavigationView.initWithSaveInstanceState(sis);
        spaceNavigationView.setCentreButtonIcon(R.drawable.ic_add_white_24dp);
        spaceNavigationView.addSpaceItem(new SpaceItem("HOME", R.drawable.ic_home_white_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("SEARCH", R.drawable.ic_search_white_24dp));
        spaceNavigationView.shouldShowFullBadgeText(true);
        spaceNavigationView.setCentreButtonIconColorFilterEnabled(false);

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Log.d("onCentreButtonClick ", "onCentreButtonClick");
                spaceNavigationView.shouldShowFullBadgeText(true);
                Snackbar.make(getWindow().getDecorView().getRootView(), "길게눌러 기록하기", Snackbar.LENGTH_SHORT).setAction(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).show();
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {

                Log.d("onItemClick ", "" + itemIndex + " " + itemName);
                switch (itemIndex) {
                    case 0:
                        break;
                    case 1:
                        startActivity(new Intent(HomeActivity.this, DBSearchActivity.class));
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
                        startActivity(new Intent(HomeActivity.this, DBSearchActivity.class));

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
