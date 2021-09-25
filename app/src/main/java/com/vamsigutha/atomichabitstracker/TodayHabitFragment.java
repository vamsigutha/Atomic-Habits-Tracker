package com.vamsigutha.atomichabitstracker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TodayHabitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodayHabitFragment extends Fragment implements TodayHabitRecyclerAdapter.HabitListener{

    TodayHabitRecyclerAdapter todayHabitRecyclerAdapter;
    RecyclerView recyclerView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TodayHabitFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TodayHabitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TodayHabitFragment newInstance(String param1, String param2) {
        TodayHabitFragment fragment = new TodayHabitFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_today_habit, container, false);
        recyclerView = view.findViewById(R.id.todayHabitRecyclerView);

        Calendar sCalendar = Calendar.getInstance();
        String dayLongName = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        Log.e("msg",dayLongName.toLowerCase());

        Query query = FirebaseFirestore.getInstance()
                .collection("habits")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereArrayContains("days",dayLongName.toLowerCase())
                .orderBy("completed", Query.Direction.ASCENDING)
                .orderBy("remainder", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Habit> options = new FirestoreRecyclerOptions.Builder<Habit>()
                .setQuery(query, Habit.class)
                .build();

        todayHabitRecyclerAdapter = new TodayHabitRecyclerAdapter(options,this);
        recyclerView.setAdapter(todayHabitRecyclerAdapter);
        todayHabitRecyclerAdapter.startListening();

        //handle check changed


        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(todayHabitRecyclerAdapter != null){
            todayHabitRecyclerAdapter.stopListening();
        }
    }

    @Override
    public void handleCheckChanged(boolean isChecked, DocumentSnapshot snapshot) {
        snapshot.getReference().update("completed",isChecked)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("msg", "updated");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("msg",e +"");
                    }
                });
    }
}