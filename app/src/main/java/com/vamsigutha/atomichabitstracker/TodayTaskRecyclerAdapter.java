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
import com.google.firebase.firestore.DocumentSnapshot;

public class TodayTaskRecyclerAdapter extends FirestoreRecyclerAdapter<Task, TodayTaskRecyclerAdapter.TaskViewHolder> {

    TaskListener taskListener;

    public TodayTaskRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Task> options, TaskListener taskListener) {
        super(options);
        this.taskListener = taskListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull TaskViewHolder holder, int position, @NonNull Task model) {
        holder.titleTextView.setText(model.getTitle());
        holder.checkBox.setChecked(model.getCompleted());
        CharSequence timeSequence = DateFormat.format("EEEE, dd MMM yyyy hh:mm a",model.getTimeRemainder().toDate());
        holder.dateTextView.setText(timeSequence);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view =layoutInflater.inflate(R.layout.today_recyclerview_item,parent,false);
        return new TodayTaskRecyclerAdapter.TaskViewHolder(view);
    }

    class TaskViewHolder extends RecyclerView.ViewHolder{

        TextView titleTextView, dateTextView;
        CheckBox checkBox;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.todayTitle);
            dateTextView = itemView.findViewById(R.id.todaydateTextView);
            checkBox = itemView.findViewById(R.id.checkBoxItem);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());

                    taskListener.handleCheckChanged(isChecked, snapshot);

                }
            });
        }
    }

    interface TaskListener{
        public void handleCheckChanged(boolean isChecked, DocumentSnapshot snapshot);
    }
}
