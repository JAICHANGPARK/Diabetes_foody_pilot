package com.dreamwalker.diabetesfoodypilot.adapter.accessory.userdevice;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dreamwalker.diabetesfoodypilot.R;
import com.dreamwalker.diabetesfoodypilot.model.accessory.Device;

import java.util.ArrayList;

public class UserDeviceAdapter extends RecyclerView.Adapter<UserDeviceAdapter.UserDeviceViewHolder> {
    Context context;
    ArrayList<Device> deviceArrayList = new ArrayList<>();

    public UserDeviceAdapter(Context context, ArrayList<Device> deviceArrayList) {
        this.context = context;
        this.deviceArrayList = deviceArrayList;
    }

    @NonNull
    @Override
    public UserDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user_device_layout, viewGroup, false);
        return new UserDeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserDeviceViewHolder userDeviceViewHolder, int i) {
            userDeviceViewHolder.deviceNameTextView.setText(deviceArrayList.get(i).getDeviceName());
            userDeviceViewHolder.deviceAddressTextView.setText(deviceArrayList.get(i).getDeviceAddress());

            userDeviceViewHolder.syncButton.setOnClickListener(v -> {});
            userDeviceViewHolder.realtimeButton.setOnClickListener(v -> {});
            userDeviceViewHolder.deleteButton.setOnClickListener(v -> {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                builder.setTitle("알림");
                builder.setMessage(deviceArrayList.get(i).getDeviceName() + "삭제하시겠어요??");
                builder.setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    deviceArrayList.remove(i);
                    notifyDataSetChanged();
                    dialog.dismiss();
                });

                builder.setNegativeButton(android.R.string.no, (dialog, which) -> {
                   dialog.dismiss();
                });
                builder.show();

            });



    }

    @Override
    public int getItemCount() {
        return deviceArrayList.size();
    }


    class UserDeviceViewHolder extends RecyclerView.ViewHolder {

        TextView deviceNameTextView, deviceAddressTextView;
        MaterialButton syncButton, realtimeButton, deleteButton;

        public UserDeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceNameTextView = itemView.findViewById(R.id.device_name_text_view);
            deviceAddressTextView = itemView.findViewById(R.id.device_address_text_view);
            syncButton = itemView.findViewById(R.id.device_sync_button);
            realtimeButton = itemView.findViewById(R.id.device_realtime_button);
            deleteButton = itemView.findViewById(R.id.device_delete_button);
        }

    }
}
