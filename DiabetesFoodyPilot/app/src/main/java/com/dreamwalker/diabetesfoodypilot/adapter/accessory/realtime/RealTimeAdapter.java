package com.dreamwalker.diabetesfoodypilot.adapter.accessory.realtime;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dreamwalker.diabetesfoodypilot.R;
import com.dreamwalker.diabetesfoodypilot.model.accessory.RealTime;

import java.util.ArrayList;

public class RealTimeAdapter extends RecyclerView.Adapter<RealTimeAdapter.RealTimeViewHolder> {

    Context context;
    ArrayList<RealTime> realTimeArrayList = new ArrayList<>();

    public RealTimeAdapter(Context context, ArrayList<RealTime> realTimeArrayList) {
        this.context = context;
        this.realTimeArrayList = realTimeArrayList;
    }

    @NonNull
    @Override
    public RealTimeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_tray_layout, viewGroup, false);
        return new RealTimeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RealTimeViewHolder realTimeViewHolder, int i) {
        realTimeViewHolder.foodTitle.setText(realTimeArrayList.get(i).getKind());
        realTimeViewHolder.foodName.setText(realTimeArrayList.get(i).getName());
        realTimeViewHolder.foodAmount.setText(realTimeArrayList.get(i).getAmount());
    }

    @Override
    public int getItemCount() {
        return realTimeArrayList.size();
    }

    class RealTimeViewHolder extends RecyclerView.ViewHolder{
        TextView foodTitle, foodName, foodAmount;
        RealTimeViewHolder(@NonNull View itemView) {
            super(itemView);
            foodTitle = (TextView)itemView.findViewById(R.id.foodTitle);
            foodAmount = (TextView)itemView.findViewById(R.id.foodAmount);
            foodName = (TextView)itemView.findViewById(R.id.foodName);
        }
    }
}
