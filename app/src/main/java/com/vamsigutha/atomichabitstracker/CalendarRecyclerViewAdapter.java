package com.vamsigutha.atomichabitstracker;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class CalendarRecyclerViewAdapter extends FirestoreRecyclerAdapter<Task,CalendarRecyclerViewAdapter.CalendarViewHolder > {



    public CalendarRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<Task> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CalendarViewHolder holder, int position, @NonNull Task model) {
        holder.titleTextView.setText(model.getTitle());
        CharSequence timeSequence = DateFormat.format("EEEE, dd MMM yyyy hh:mm a",model.getTimeRemainder().toDate());
        holder.dateTextView.setText(timeSequence);
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view =layoutInflater.inflate(R.layout.calendar_recycler_view_item,parent,false);
        return new CalendarRecyclerViewAdapter.CalendarViewHolder(view);
    }

    class CalendarViewHolder extends RecyclerView.ViewHolder{

        TextView titleTextView, dateTextView;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.todayTitle);
            dateTextView = itemView.findViewById(R.id.todaydateTextView);
        }
    }
}
