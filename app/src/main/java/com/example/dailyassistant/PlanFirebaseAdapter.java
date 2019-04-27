package com.example.dailyassistant;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class PlanFirebaseAdapter extends FirestoreRecyclerAdapter<Plan, PlanFirebaseAdapter.PlanViewHolder> {

    public PlanFirebaseAdapter(@NonNull FirestoreRecyclerOptions<Plan> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PlanViewHolder holder, int position, @NonNull Plan model) {
        holder.Title.setText(model.getTitle());
        holder.Description.setText(model.getDescription());
        holder.date.setText(model.getDateString());
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.plan_item_view, viewGroup,
                false);
        return new PlanViewHolder(v);
    }

    class PlanViewHolder extends RecyclerView.ViewHolder {

        TextView Title;
        TextView Description;
        Button editDate;
        TextView date;

        PlanViewHolder(View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.tv_title);
            Description = itemView.findViewById(R.id.tv_description);
            editDate = itemView.findViewById(R.id.tv_editDate);
            date = itemView.findViewById(R.id.tv_date);
        }
    }
}
