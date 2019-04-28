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
import com.google.firebase.firestore.DocumentSnapshot;

public class PlanFirebaseAdapter extends FirestoreRecyclerAdapter<Plan, PlanFirebaseAdapter.PlanViewHolder> {

    private OnListItemClickListener mOnListItemClickListener;

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

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class PlanViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

            editDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnListItemClickListener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            mOnListItemClickListener.onCalendarClick(getSnapshots().getSnapshot(position), position);
                        }
                    }
                }
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION && mOnListItemClickListener != null)
            {
                mOnListItemClickListener.onListItemClick(getSnapshots().getSnapshot(position), position);
            }
        }
    }

    public interface OnListItemClickListener {
        void onListItemClick(DocumentSnapshot documentSnapshot, int position);
        void onCalendarClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnListItemClickListener listener) {
        this.mOnListItemClickListener = listener;
    }

}
