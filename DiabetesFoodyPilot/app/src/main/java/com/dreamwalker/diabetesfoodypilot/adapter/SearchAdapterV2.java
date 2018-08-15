package com.dreamwalker.diabetesfoodypilot.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dreamwalker.diabetesfoodypilot.R;
import com.dreamwalker.diabetesfoodypilot.database.food.MixedFoodItem;

import java.util.ArrayList;

public class SearchAdapterV2 extends RecyclerView.Adapter<SearchAdapterV2.SearchViewHolder>{
    Context context;
    ArrayList<MixedFoodItem> foodItems;

    public SearchAdapterV2(Context context, ArrayList<MixedFoodItem> foodItems) {
        this.context = context;
        this.foodItems = foodItems;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_list_v2,parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {

        holder.name.setText(foodItems.get(position).getFoodName());
        holder.group.setText(foodItems.get(position).getFoodClass());
        holder.amount.setText(foodItems.get(position).getFoodAmount());
        holder.kcal.setText(foodItems.get(position).getKcal());
        holder.carbo.setText(foodItems.get(position).getCarbo());
        holder.protein.setText(foodItems.get(position).getProt());
        holder.fat.setText(foodItems.get(position).getFatt());

        holder.totalExchange.setText(foodItems.get(position).getTotalExchange());
        holder.group1.setText(foodItems.get(position).getFoodGroup1());
        holder.group2.setText(foodItems.get(position).getFoodGroup2());
        holder.group3.setText(foodItems.get(position).getFoodGroup3());
        holder.group4.setText(foodItems.get(position).getFoodGroup4());
        holder.group5.setText(foodItems.get(position).getFoodGroup5());
        holder.group6.setText(foodItems.get(position).getFoodGroup6());

    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }


    public class SearchViewHolder extends RecyclerView.ViewHolder{
        TextView name, group, amount, kcal, carbo, protein, fat;
        TextView totalExchange, group1, group2, group3, group4, group5, group6;

        public SearchViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.text_name);
            group = itemView.findViewById(R.id.text_group);
            amount = itemView.findViewById(R.id.text_amount);
            kcal = itemView.findViewById(R.id.text_kcal);
            carbo = itemView.findViewById(R.id.text_carbo);
            protein = itemView.findViewById(R.id.text_protein);
            fat = itemView.findViewById(R.id.text_fat);

            totalExchange = itemView.findViewById(R.id.text_total_exchange);
            group1 = itemView.findViewById(R.id.text_group1);
            group2 = itemView.findViewById(R.id.text_group2);
            group3 = itemView.findViewById(R.id.text_group3);
            group4 = itemView.findViewById(R.id.text_group4);
            group5 = itemView.findViewById(R.id.text_group5);
            group6 = itemView.findViewById(R.id.text_group6);

        }
    }
}
