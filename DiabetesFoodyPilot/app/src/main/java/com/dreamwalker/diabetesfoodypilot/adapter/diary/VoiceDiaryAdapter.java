package com.dreamwalker.diabetesfoodypilot.adapter.diary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dreamwalker.diabetesfoodypilot.R;
import com.dreamwalker.diabetesfoodypilot.database.diary.VoiceMemo;

import java.util.ArrayList;

public class VoiceDiaryAdapter extends RecyclerView.Adapter<VoiceDiaryAdapter.VoiceDiaryViewHolder> {

    Context context;
    ArrayList<VoiceMemo> memoArrayList = new ArrayList<>();

    public VoiceDiaryAdapter(Context context, ArrayList<VoiceMemo> memoArrayList) {
        this.context = context;
        this.memoArrayList = memoArrayList;
    }

    @NonNull
    @Override
    public VoiceDiaryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_voice_diary, viewGroup, false);
        return new VoiceDiaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoiceDiaryViewHolder voiceDiaryViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return memoArrayList.size();
    }


    class VoiceDiaryViewHolder extends RecyclerView.ViewHolder {

        TextView timeTextView, momoTextView;

        public VoiceDiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.time_text_view);
            momoTextView = itemView.findViewById(R.id.memo_text_view);
        }
    }
}
