package com.example.dailyassistant;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {

    private ArrayList<Plan> mPlans;
    final private OnListItemClickListener mOnListItemClickListener;

    public PlanAdapter(ArrayList<Plan> mPlans, OnListItemClickListener mOnListItemClickListener) {
        this.mPlans = mPlans;
        this.mOnListItemClickListener = mOnListItemClickListener;
    }

    @NonNull
    @Override
    public PlanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.plan_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanAdapter.ViewHolder viewHolder, int position) {
        viewHolder.Title.setText(mPlans.get(position).getTitle());
        viewHolder.Description.setText(mPlans.get(position).getDescription());
        viewHolder.date.setText(getDateInString(mPlans.get(position)));
    }

    @Override
    public int getItemCount() { return mPlans.size(); }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView Title;
        TextView Description;
        Button editDate;
        TextView date;

        ViewHolder(View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.tv_title);
            Description = itemView.findViewById(R.id.tv_description);
            editDate = itemView.findViewById(R.id.tv_editDate);
            date = itemView.findViewById(R.id.tv_date);
        }

        @Override
        public void onClick(View v) {
            mOnListItemClickListener.onListItemClick(getAdapterPosition());
        }
    }

    public interface OnListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public String getDateInString(Plan plan) {
        String date;
        date = plan.getDay() + "/" + plan.getMonth() + "/" + plan.getYear();
        return date;
    }
}
