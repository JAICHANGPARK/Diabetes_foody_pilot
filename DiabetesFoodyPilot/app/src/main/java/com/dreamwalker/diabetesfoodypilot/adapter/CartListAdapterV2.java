package com.dreamwalker.diabetesfoodypilot.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dreamwalker.diabetesfoodypilot.R;
import com.dreamwalker.diabetesfoodypilot.model.Food;
import com.dreamwalker.diabetesfoodypilot.model.TestModel;
import com.lid.lib.LabelImageView;

import java.util.ArrayList;
import java.util.List;

public class CartListAdapterV2 extends RecyclerView.Adapter<CartListAdapterV2.MyViewHolder> {

    private Context context;
    private List<TestModel> cartList;
    ArrayList<Integer> imageList;
    ArrayList<Food> foodArrayList;

    OnItemClickListrner listrner;

    public void setOnItemClickListrner(OnItemClickListrner listrner){
        this.listrner = listrner;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, description, price;
        //public ImageView thumbnail;
        public LabelImageView thumbnail;
        public RelativeLayout viewBackground, viewForeground;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            description = view.findViewById(R.id.description);
            price = view.findViewById(R.id.price);
            thumbnail = view.findViewById(R.id.thumbnail);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
            // TODO: 2018-08-15 클릭 리스너 추가
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listrner != null){
                listrner.onItemClick(v, getAdapterPosition());

            }
        }
    }

    public CartListAdapterV2(Context context, List<TestModel> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    public CartListAdapterV2(Context context, ArrayList<Integer> imageList, ArrayList<Food> foodArrayList) {
        this.context = context;
        this.imageList = imageList;
        this.foodArrayList = foodArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list_item_v2, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

//        final TestModel item = cartList.get(position);
//        holder.name.setText(item.getName());
//        holder.description.setText(item.getDescription());
//        holder.price.setText("₹" + item.getPrice());
        final Food item = foodArrayList.get(position);
        holder.name.setText(item.getFoodName());
        holder.description.setText(item.getFoodGroup());
        holder.price.setText(item.getFoodKcal());

//        Glide.with(context).load(item.getThumbnail()).into(holder.thumbnail);
        Glide.with(context).load(imageList.get(position)).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return foodArrayList.size();
    }

    public void removeItem(int position) {
        foodArrayList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Food item, int position) {
        foodArrayList.add(position, item);
        notifyItemInserted(position);
    }

    public void addItem(Food item, int image) {
        imageList.add(image);
        foodArrayList.add(item);
        notifyDataSetChanged();
    }
}
