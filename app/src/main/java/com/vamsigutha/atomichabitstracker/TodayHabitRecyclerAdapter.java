package com.vamsigutha.atomichabitstracker;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

public class TodayHabitRecyclerAdapter extends FirestoreRecyclerAdapter<Habit, TodayHabitRecyclerAdapter.HabitViewHolder> {

    HabitListener habitListener;



    public TodayHabitRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Habit> options, HabitListener habitListener) {
        super(options);
        this.habitListener = habitListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull HabitViewHolder holder, int position, @NonNull Habit model) {
        holder.titleTextView.setText(model.getTitle());
        holder.checkBox.setChecked(model.getCompleted());
        CharSequence timeSequence = DateFormat.format("hh:mm a",model.getRemainder().toDate());
        holder.dateTextView.setText(timeSequence);
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view =layoutInflater.inflate(R.layout.today_recyclerview_item,parent,false);
        return new HabitViewHolder(view);
    }

    class HabitViewHolder extends RecyclerView.ViewHolder{

        TextView titleTextView, dateTextView;
        CheckBox checkBox;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.todayTitle);
            dateTextView = itemView.findViewById(R.id.todaydateTextView);
            checkBox = itemView.findViewById(R.id.checkBoxItem);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                    Habit habit = getItem(getAdapterPosition());

                        habitListener.handleCheckChanged(isChecked, snapshot);

                }
            });

        }
    }

    interface HabitListener{
        public void handleCheckChanged(boolean isChecked, DocumentSnapshot snapshot);
    }
}
