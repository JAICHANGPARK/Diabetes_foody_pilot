package com.dreamwalker.diabetesfoodypilot.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dreamwalker.diabetesfoodypilot.R;
import com.dreamwalker.diabetesfoodypilot.database.food.FoodItem;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>{
    Context context;
    ArrayList<FoodItem> foodItems;

    public SearchAdapter(Context context, ArrayList<FoodItem> foodItems) {
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
        holder.group.setText(foodItems.get(position).getFoodGroup());
        holder.amount.setText(foodItems.get(position).getFoodAmount());
        holder.kcal.setText(foodItems.get(position).getFoodKcal());
        holder.carbo.setText(foodItems.get(position).getFoodCarbo());
        holder.protein.setText(foodItems.get(position).getFoodProtein());
        holder.fat.setText(foodItems.get(position).getFoodFat());


    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }


    public class SearchViewHolder extends RecyclerView.ViewHolder{
        TextView name, group, amount, kcal, carbo, protein, fat;

        public SearchViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.text_name);
            group = itemView.findViewById(R.id.text_group);
            amount = itemView.findViewById(R.id.text_amount);
            kcal = itemView.findViewById(R.id.text_kcal);
            carbo = itemView.findViewById(R.id.text_carbo);
            protein = itemView.findViewById(R.id.text_protein);
            fat = itemView.findViewById(R.id.text_fat);

        }
    }
}
