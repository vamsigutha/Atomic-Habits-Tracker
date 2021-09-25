package com.vamsigutha.atomichabitstracker;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateReglarHabitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateReglarHabitFragment extends Fragment {

    private Chip sunday, monday, tuesday, wednesday, thursday, friday, saturday;
    private ArrayList<String> days = new ArrayList<>();
    private CheckBox checkBox;
    private CardView timePicker;
    private TextView getRemainderTextView;
    private Button createHabitHandler;
    private TextInputLayout title, description;
    private long remainderTime;
    private ProgressBar progressBar;

    SharedPreferences sharedPreferences;
    boolean storedNotificationSound;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateReglarHabitFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateReglarHabitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateReglarHabitFragment newInstance(String param1, String param2) {
        CreateReglarHabitFragment fragment = new CreateReglarHabitFragment();
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
        View view = inflater.inflate(R.layout.fragment_create_reglar_habit, container, false);
        sunday = view.findViewById(R.id.sunday);
        monday = view.findViewById(R.id.monday);
        tuesday = view.findViewById(R.id.tuesday);
        wednesday = view.findViewById(R.id.wednesday);
        thursday = view.findViewById(R.id.thursday);
        friday = view.findViewById(R.id.friday);
        saturday = view.findViewById(R.id.saturday);

        checkBox = view.findViewById(R.id.checkBox);

        getRemainderTextView = view.findViewById(R.id.getRemainderTextView);
        timePicker = view.findViewById(R.id.timePicker);

        createHabitHandler = view.findViewById(R.id.createHabitHandler);

        title = view.findViewById(R.id.habitTitle);
        description = view.findViewById(R.id.habitDescription);

        progressBar = view.findViewById(R.id.progressBar);

        sharedPreferences =  getActivity().getSharedPreferences("settings",0);

        boolean storedNotification =  sharedPreferences.getBoolean("notification",true);
        storedNotificationSound = sharedPreferences.getBoolean("notificationSound",true);


        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.e("msg", buttonView.getResources().getResourceEntryName(buttonView.getId()));
                    days.add(buttonView.getResources().getResourceEntryName(buttonView.getId()));
                    ((Chip)buttonView).setChipBackgroundColorResource(R.color.red);

                }else{
                    days.remove(buttonView.getResources().getResourceEntryName(buttonView.getId()));
                    ((Chip)buttonView).setChipBackgroundColorResource(R.color.chip);
                }
            }
        };



        sunday.setOnCheckedChangeListener(checkedChangeListener);
        monday.setOnCheckedChangeListener(checkedChangeListener);
        tuesday.setOnCheckedChangeListener(checkedChangeListener);
        wednesday.setOnCheckedChangeListener(checkedChangeListener);
        thursday.setOnCheckedChangeListener(checkedChangeListener);
        friday.setOnCheckedChangeListener(checkedChangeListener);
        saturday.setOnCheckedChangeListener(checkedChangeListener);



        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    sunday.setChecked(true);
                    monday.setChecked(true);
                    tuesday.setChecked(true);
                    wednesday.setChecked(true);
                    thursday.setChecked(true);
                    friday.setChecked(true);
                    saturday.setChecked(true);


                }else{
                    sunday.setChecked(false);
                    monday.setChecked(false);
                    tuesday.setChecked(false);
                    wednesday.setChecked(false);
                    thursday.setChecked(false);
                    friday.setChecked(false);
                    saturday.setChecked(false);
                    days.clear();
                }
            }
        });

        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int HOUR = calendar.get(Calendar.HOUR_OF_DAY);
                int MINUTE = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.set(Calendar.YEAR,1970);
                        calendar1.set(Calendar.MONTH,1);
                        calendar1.set(Calendar.DATE,1);
                        calendar1.set(Calendar.HOUR_OF_DAY,hour);
                        calendar1.set(Calendar.MINUTE,minute);
                        Log.e("msg",calendar1.getTimeInMillis() +"");

                        remainderTime = calendar1.getTimeInMillis();

                        CharSequence charSequence = DateFormat.format("hh:mm a",calendar1);
                        getRemainderTextView.setText(charSequence);
                    }
                },HOUR, MINUTE, false);
                timePickerDialog.show();

            }
        });

        createHabitHandler.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){


                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String tempTitle = title.getEditText().getText().toString();
                String tempDescription = description.getEditText().getText().toString();

                if(tempTitle.equals("")){
                    Toast.makeText(getActivity(), "Required habit name field", Toast.LENGTH_SHORT).show();
                }else{
                    view.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);

                    Habit habit = new Habit(tempTitle,
                            tempDescription,
                            false,
                            days,
                            new Timestamp(new Date(remainderTime)),
                            userId);

                    FirebaseFirestore.getInstance()
                            .collection("habits")
                            .add(habit)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    view.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(),"Added a new Habit",Toast.LENGTH_SHORT).show();

                                    if(storedNotification){
                                        getHabitFirebaseData();
                                    }


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    view.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(),"Failed to Habit",Toast.LENGTH_LONG).show();
                                }
                            });
                }

            }
        });



        return view;
    }

    public void getHabitFirebaseData(){

        Calendar sCalendar = Calendar.getInstance();
        String dayLongName = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        FirebaseFirestore.getInstance()
                .collection("habits")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("completed",false)
//                .whereGreaterThanOrEqualTo("remainder",Calendar.getInstance().getTimeInMillis())
                .whereArrayContains("days",dayLongName.toLowerCase())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        int code = 1;

                        //cancel all alarms

                        Intent i = new Intent(getActivity(), RemainderBroadcast.class);
                        AlarmUtils.cancelAllAlarms(getActivity(), i);


                        for(DocumentSnapshot documentSnapshot:snapshotList){

                            Timestamp timestamp = (Timestamp) documentSnapshot.getData().get("remainder");
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY,timestamp.toDate().getHours());
                            calendar.set(Calendar.MINUTE, timestamp.toDate().getMinutes());
                            calendar.set(Calendar.SECOND,0);

                            if(calendar.getTimeInMillis()>=Calendar.getInstance().getTimeInMillis()){

//                                Log.e("time",calendar.getTimeInMillis() +"  " +Calendar.getInstance().getTimeInMillis());

                                Intent intent = new Intent(getActivity(), RemainderBroadcast.class);

                                intent.putExtra("title",((String)documentSnapshot.getData().get("title")));
                                intent.putExtra("code",code);
                                intent.putExtra("notificationSound",storedNotificationSound);
                                intent.putExtra("content","It's time to complete your habit");


                                AlarmUtils.addAlarm(getActivity(), intent, code, calendar);
                                code++;

                            }


                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("failure",e.toString());
                    }
                });
    }

}