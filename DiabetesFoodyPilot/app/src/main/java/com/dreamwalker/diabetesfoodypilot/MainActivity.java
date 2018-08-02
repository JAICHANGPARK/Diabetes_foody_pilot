package com.dreamwalker.diabetesfoodypilot;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.dreamwalker.diabetesfoodypilot.adapter.CartListAdapter;
import com.dreamwalker.diabetesfoodypilot.adapter.CartListAdapterV2;
import com.dreamwalker.diabetesfoodypilot.adapter.RecyclerItemTouchHelperV2;
import com.dreamwalker.diabetesfoodypilot.adapter.SearchAdapter;
import com.dreamwalker.diabetesfoodypilot.database.food.FoodItem;
import com.dreamwalker.diabetesfoodypilot.model.Food;
import com.dreamwalker.diabetesfoodypilot.model.Suggestions;
import com.dreamwalker.diabetesfoodypilot.model.TestModel;
import com.dreamwalker.diabetesfoodypilot.remote.Common;
import com.dreamwalker.diabetesfoodypilot.remote.IMenuRequest;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RecyclerItemTouchHelperV2.RecyclerItemTouchHelperListener{

    private static final String TAG = "MainActivity";

    private final String URL_API = "https://api.androidhive.info/json/menu.json";

    private RecyclerView recyclerView;
    private List<TestModel> cartList;
    private CartListAdapter mAdapter;

    IMenuRequest service;
//
//    @BindView(R.id.bottomSheet)
//    LinearLayout mBottomSheet;

    @BindView(R.id.bottomSheet)
    CoordinatorLayout mBottomSheet;

    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.floating_search_view)
    FloatingSearchView floatingSearchView;

    @BindView(R.id.add)
    ImageView addItemButton;

    @BindView(R.id.bottom_recycler_view)
    RecyclerView bottomRecyclerView;

    SearchAdapter searchAdapter;

    ArrayList<FoodItem> searchList = new ArrayList<>();

    BottomSheetBehavior bottomSheetBehavior;

    Realm realm;


    CartListAdapterV2 adapterV2;

    ArrayList<Food> foodArrayList = new ArrayList<>();
    ArrayList<Integer> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setBottomSheetBehavior();

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        service = Common.getMenuRequest();

        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        RelativeLayout rl = findViewById(R.id.card_container);
        rl.startAnimation(slideUp);


        recyclerView = findViewById(R.id.recycler_view);
        cartList = new ArrayList<>();
        mAdapter = new CartListAdapter(this, cartList);


        setInitData();
        adapterV2 = new CartListAdapterV2(this, imageList, foodArrayList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapterV2);
//        recyclerView.setAdapter(mAdapter);

        searchAdapter = new SearchAdapter(this, searchList);
        //bottomRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
        bottomRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL));
        bottomRecyclerView.setItemAnimator(new DefaultItemAnimator());
       // bottomRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
       // bottomRecyclerView.setHasFixedSize(true);
        //bottomRecyclerView.setNestedScrollingEnabled(true);
        bottomRecyclerView.setAdapter(searchAdapter);


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelperV2(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        addItemToCart();

        RealmResults<FoodItem> results = realm.where(FoodItem.class).findAll();

        Log.e(TAG, "onCreate: " + results.size());



//        for (int i = 0 ; i < results.size(); i++){
//            Log.e(TAG, "onCreate: " + results.get(i).getFoodName() );
//        }

        setFloatingSearchView();



    }

    private void setInitData(){
        foodArrayList.add(new Food("","식품 입력","","","","",""));
        imageList.add(R.drawable.rice);

        foodArrayList.add(new Food("","식품 입력","","","","",""));
        imageList.add(R.drawable.soup);

        foodArrayList.add(new Food("","식품 입력","","","","",""));
        imageList.add(R.drawable.side_dish_01);

        foodArrayList.add(new Food("","식품 입력","","","","",""));
        imageList.add(R.drawable.side_dish_02);

        foodArrayList.add(new Food("","식품 입력","","","","",""));
        imageList.add(R.drawable.side_dish_03);

        foodArrayList.add(new Food("","식품 입력","","","","",""));
        imageList.add(R.drawable.side_dish_04);



    }

    private void setFloatingSearchView(){

        floatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                List<Suggestions> foodName = new ArrayList<>();
                Log.e(TAG, ": oldQuery --> " + oldQuery + " new Query --> " + newQuery);
                RealmResults<FoodItem> result1 = realm.where(FoodItem.class).contains("foodName", newQuery).findAll();
                //floatingSearchView.swapSuggestions();
                //RealmResults<FoodItem> result1 = realm.where(FoodItem.class).like("foodName", newQuery, Case.INSENSITIVE).findAllSortedAsync("foodName", Sort.ASCENDING);
                if (result1.size() != 0) {
                    for (FoodItem item : result1) {
                        foodName.add(new Suggestions(item.getFoodName()));
                    }
                }

                floatingSearchView.swapSuggestions(foodName);


                //floatingSearchView.swapSuggestions(foodName);

            }
        });


        floatingSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                // Log.e(TAG, "onSuggestionClicked: ");
                Log.e(TAG, "onSuggestionClicked: " + searchSuggestion.getBody());

            }

            @Override
            public void onSearchAction(String currentQuery) {
                Log.e(TAG, "onSearchAction: " + currentQuery);
                searchList.clear();
                List<String> foodName = new ArrayList<>();
                //floatingSearchView.swapSuggestions();
//                RealmResults<FoodItem> result1 = realm.where(FoodItem.class).like("foodName", currentQuery, Case.INSENSITIVE).findAllSortedAsync("foodName", Sort.ASCENDING);
                RealmResults<FoodItem> result1 = realm.where(FoodItem.class).contains("foodName", currentQuery).findAll();
                if (result1.size() != 0) {
                    for (FoodItem item : result1) {
                        Log.e(TAG, "onSearchAction: " + item.getFoodName());
                        foodName.add(item.getFoodName());
                    }
                }

                searchList.addAll(result1);
                searchAdapter.notifyDataSetChanged();

            }
        });

    }

    private void setBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        //finish();
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        bottomSheet.requestLayout();
                        setStatusBarDim(false);
                        break;
                    default:
                        setStatusBarDim(true);
                        break;
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void addItemToCart() {

        service.getMenuList(URL_API).enqueue(new Callback<List<TestModel>>() {
            @Override
            public void onResponse(Call<List<TestModel>> call, Response<List<TestModel>> response) {
                cartList.clear();
                cartList.addAll(response.body());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<TestModel>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if (viewHolder instanceof CartListAdapterV2.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = foodArrayList.get(viewHolder.getAdapterPosition()).getFoodName();

            // backup of removed item for undo purpose
            final Food deletedItem = foodArrayList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            adapterV2.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    adapterV2.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
//        if (viewHolder instanceof CartListAdapter.MyViewHolder) {
//            // get the removed item name to display it in snack bar
//            String name = cartList.get(viewHolder.getAdapterPosition()).getName();
//
//            // backup of removed item for undo purpose
//            final TestModel deletedItem = cartList.get(viewHolder.getAdapterPosition());
//            final int deletedIndex = viewHolder.getAdapterPosition();
//
//            // remove the item from recycler view
//            mAdapter.removeItem(viewHolder.getAdapterPosition());
//
//            // showing snack bar with Undo option
//            Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), name + " removed from cart!", Snackbar.LENGTH_LONG);
//            snackbar.setAction("UNDO", new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    // undo is selected, restore the deleted item
//                    mAdapter.restoreItem(deletedItem, deletedIndex);
//                }
//            });
//            snackbar.setActionTextColor(Color.YELLOW);
//            snackbar.show();
//        }
    }

    @OnClick(R.id.fab)
    public void fadClicked() {

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();

        int width = dm.widthPixels;

        int height = dm.heightPixels;
        Log.e(TAG, "fadClicked: " + height);


        //bottomSheetBehavior.setPeekHeight(60);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//        floatingActionButton.setVisibility(View.GONE);
    }

    private void setStatusBarDim(boolean dim) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(dim ? Color.TRANSPARENT : ContextCompat.getColor(this, getThemedResId(R.attr.colorPrimaryDark)));
        }
    }

    private int getThemedResId(@AttrRes int attr) {
        TypedArray a = getTheme().obtainStyledAttributes(new int[]{attr});
        int resId = a.getResourceId(0, 0);
        a.recycle();
        return resId;
    }

    @OnClick(R.id.add)
    public void onClickedAddButton(){
//        foodArrayList.add(new Food("","식품 입력","","","","",""));
//        imageList.add(R.drawable.rice);

        adapterV2.addItem(new Food("","식품 입력","","","","",""),
                R.drawable.side_dish_04);

        adapterV2.notifyDataSetChanged();
    }


}
