package com.vamsigutha.atomichabitstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.applikeysolutions.cosmocalendar.model.Day;
import com.applikeysolutions.cosmocalendar.selection.BaseSelectionManager;
import com.applikeysolutions.cosmocalendar.selection.OnDaySelectedListener;
import com.applikeysolutions.cosmocalendar.selection.SingleSelectionManager;
import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.applikeysolutions.cosmocalendar.view.CalendarView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    CalendarView calendarView;
    RecyclerView recyclerView;
    Calendar calendar;
    CalendarRecyclerViewAdapter calendarRecyclerViewAdapter;
    ImageView noTaskImageView;
    TextView noTaskTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.calendarRecyclerView);
        noTaskImageView = findViewById(R.id.noTaskImageView);
        noTaskTextView = findViewById(R.id.noTaskTextView);

        calendarView.setCalendarOrientation(0);
        calendarView.setBackgroundColor(getResources().getColor(R.color.card));

        //getting data programmatically for first time
        calendar = Calendar.getInstance();
        getData();

        calendarView.setSelectionType(SelectionType.SINGLE);



        calendarView.setSelectionManager(new SingleSelectionManager(new OnDaySelectedListener(){

            @Override
            public void onDaySelected() {
                calendar = calendarView.getSelectedDates().get(0);
                Log.e("msg", String.valueOf(calendarView.getSelectedDates().get(0).get(Calendar.DAY_OF_MONTH)));

                getData();

            }

        }));

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(calendarRecyclerViewAdapter != null){
            calendarRecyclerViewAdapter.stopListening();
        }
    }

    public long getStartOfDayInMillis() {

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    public long getEndOfDayInMillis() {
        // Add one day's time to the beginning of the day.
        // 24 hours * 60 minutes * 60 seconds * 1000 milliseconds = 1 day

        return getStartOfDayInMillis() + (24 * 60 * 60 * 1000);
    }

    public void getData(){
        if(calendarRecyclerViewAdapter != null){
            calendarRecyclerViewAdapter.stopListening();
        }

        Query query = FirebaseFirestore.getInstance()
                .collection("tasks")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereGreaterThanOrEqualTo("timeRemainder",new Timestamp(new Date(getStartOfDayInMillis())))
                .whereLessThanOrEqualTo("timeRemainder",new Timestamp(new Date(getEndOfDayInMillis())))
                .orderBy("timeRemainder",Query.Direction.ASCENDING)
                .orderBy("completed",Query.Direction.ASCENDING);

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        Log.e("size",snapshotList.size()+"");
                        if(snapshotList.size() > 0){
                            noTaskImageView.setVisibility(View.GONE);
                            noTaskTextView.setVisibility(View.GONE);
                        }else{
                            noTaskImageView.setVisibility(View.VISIBLE);
                            noTaskTextView.setVisibility(View.VISIBLE);
                        }

//                        for(DocumentSnapshot snapshot:snapshotList){
//                            Log.e("qeury",snapshot.getData().toString());
//                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("failure list", e +"");
                    }
                });

        FirestoreRecyclerOptions<Task> options = new FirestoreRecyclerOptions.Builder<Task>()
                .setQuery(query, Task.class)
                .build();

        calendarRecyclerViewAdapter =  new CalendarRecyclerViewAdapter(options);
        recyclerView.setAdapter(calendarRecyclerViewAdapter);
        calendarRecyclerViewAdapter.startListening();
    }
}