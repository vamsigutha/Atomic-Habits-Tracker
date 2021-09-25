package com.vamsigutha.atomichabitstracker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateTaskFragment extends Fragment {

    private CardView datePicker;
    private TextView dateTextView;
    private CardView timePicker;
    private TextView timeTextView;
    private ProgressBar progressBar;
    private Button createTaskHandler;
    private long taskRemainderTime;
    private long taskRemainderDate;
    private TextInputLayout title, description;
    Calendar calendar1= Calendar.getInstance();

    SharedPreferences sharedPreferences;
    Boolean storedNotificationSound;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateTaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateTaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateTaskFragment newInstance(String param1, String param2) {
        CreateTaskFragment fragment = new CreateTaskFragment();
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
        View view = inflater.inflate(R.layout.fragment_create_task, container, false);

        datePicker = view.findViewById(R.id.datePicker);
        dateTextView = view.findViewById(R.id.dateTextView);

        timePicker = view.findViewById(R.id.timePicker);
        timeTextView = view.findViewById(R.id.timeTextView);

        createTaskHandler = view.findViewById(R.id.createTaskHandler);
        title = view.findViewById(R.id.taskTitle);
        description = view.findViewById(R.id.taskDescription);
        progressBar = view.findViewById(R.id.progressBar);

        sharedPreferences =  getActivity().getSharedPreferences("settings",0);

        Boolean storedNotification =  sharedPreferences.getBoolean("notification",true);
        storedNotificationSound = sharedPreferences.getBoolean("notificationSound",true);


        datePicker.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int YEAR = calendar.get(Calendar.YEAR);
                int MONTH = calendar.get(Calendar.MONTH);
                int DAY = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar1.set(Calendar.YEAR, year);
                        calendar1.set(Calendar.MONTH, month);
                        calendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        CharSequence dateCharSequence = DateFormat.format("EEEE, dd MMM yyyy",calendar1);
                        dateTextView.setText(dateCharSequence);
                    }
                }, YEAR, MONTH, DAY);
                datePickerDialog.show();
            }
        });

        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int HOUR = calendar.get(Calendar.HOUR_OF_DAY);
                int MINUTE = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        calendar1.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar1.set(Calendar.MINUTE,minute);

                        CharSequence charSequence = DateFormat.format("hh:mm a",calendar1);
                        timeTextView.setText(charSequence);

                    }
                },HOUR, MINUTE, false);
                timePickerDialog.show();
            }
        });

        createTaskHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String tempTitle = title.getEditText().getText().toString();
                String tempDescription = description.getEditText().getText().toString();
                taskRemainderTime = calendar1.getTimeInMillis();

                if(tempTitle.equals("")){
                    Toast.makeText(getActivity(), "Required habit name field", Toast.LENGTH_SHORT).show();
                }else{
                    view.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);

                    Task task = new Task(tempTitle,
                            tempDescription,
                            false,
                            new Timestamp(new Date(taskRemainderTime)),
                            userId);

                    FirebaseFirestore.getInstance()
                            .collection("tasks")
                            .add(task)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    view.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(),"Added a new Task",Toast.LENGTH_SHORT).show();

                                    if(storedNotification){
                                        getTaskFirebaseData();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    view.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(),"Failed to add Task",Toast.LENGTH_SHORT).show();
                                }
                            });

                }


            }
        });




        return view;
    }

    private void getTaskFirebaseData(){
        FirebaseFirestore.getInstance()
                .collection("tasks")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereGreaterThanOrEqualTo("timeRemainder",new Timestamp(new Date(getStartOfDayInMillis())))
                .whereLessThanOrEqualTo("timeRemainder",new Timestamp(new Date(getEndOfDayInMillis())))
                .orderBy("timeRemainder", Query.Direction.ASCENDING)
                .orderBy("completed",Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int code = 100;
                        Intent i = new Intent(getActivity(), RemainderBroadcast.class);
                        TaskAlarmUtils.cancelAllAlarms(getActivity(), i);

                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                        for(DocumentSnapshot documentSnapshot:snapshotList){
                            Log.e("data", documentSnapshot.getData().toString());

                            Timestamp timestamp = (Timestamp) documentSnapshot.getData().get("timeRemainder");
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY,timestamp.toDate().getHours());
                            calendar.set(Calendar.MINUTE, timestamp.toDate().getMinutes());
                            calendar.set(Calendar.SECOND,0);

                            if(calendar.getTimeInMillis()>=Calendar.getInstance().getTimeInMillis()){
                                Intent intent = new Intent(getActivity(), RemainderBroadcast.class);

                                intent.putExtra("title",((String)documentSnapshot.getData().get("title")));
                                intent.putExtra("code",code);
                                intent.putExtra("notificationSound",storedNotificationSound);
                                intent.putExtra("content","You have a Task to complete");


                                TaskAlarmUtils.addAlarm(getActivity(), intent, code, calendar);
                                code++;
                            }


                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("failure in task fetch",e+"");
                    }
                });
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
}