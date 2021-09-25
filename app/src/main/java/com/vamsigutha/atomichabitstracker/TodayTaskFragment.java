package com.vamsigutha.atomichabitstracker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TodayTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodayTaskFragment extends Fragment implements TodayTaskRecyclerAdapter.TaskListener{

    private RecyclerView recyclerView;
    private TodayTaskRecyclerAdapter todayTaskRecyclerAdapter;
    private ConstraintLayout noTaskLayout;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TodayTaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TodayTaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TodayTaskFragment newInstance(String param1, String param2) {
        TodayTaskFragment fragment = new TodayTaskFragment();
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
        View view = inflater.inflate(R.layout.fragment_today_task, container, false);
        recyclerView = view.findViewById(R.id.todayTaskRecyclerView);
        noTaskLayout = view.findViewById(R.id.noTaskLayout);



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
                            noTaskLayout.setVisibility(View.GONE);

                        }else{
                            noTaskLayout.setVisibility(View.VISIBLE);
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

        todayTaskRecyclerAdapter = new TodayTaskRecyclerAdapter(options, this);
        recyclerView.setAdapter(todayTaskRecyclerAdapter);
        todayTaskRecyclerAdapter.startListening();

        return view;
    }

    public long getStartOfDayInMillis() {
        Calendar calendar = Calendar.getInstance();
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(todayTaskRecyclerAdapter != null){
            todayTaskRecyclerAdapter.stopListening();
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