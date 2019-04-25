package com.example.dailyassistant;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return  null;
    }

    public void onBindViewHolder(ViewHolder viewHolder, int position) {
    }

    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView Title;
        TextView Description;
        Button editDate;

        ViewHolder(View itemView) {
            super(itemView);

            Title = itemView.findViewById(R.id.tv_title);
            Description = itemView.findViewById(R.id.tv_description);
            editDate = itemView.findViewById(R.id.tv_editDate);
        }
    }
}
