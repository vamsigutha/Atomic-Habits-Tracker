package com.vamsigutha.atomichabitstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener{

    private static final String TAG = "MainActivity";
    SharedPreferences sharedPreferences;
    Boolean storedNotificationSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startLoginActivity();
        }else{

            sharedPreferences =  getSharedPreferences("settings",0);

            Boolean storedNotification =  sharedPreferences.getBoolean("notification",true);
            storedNotificationSound = sharedPreferences.getBoolean("notificationSound",true);

            if(storedNotification){
                createNotificationChannel();
                getHabitFirebaseData();
                getTaskFirebaseData();
                setDailyAlarm();
            }

        }





    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel notificationChannel = new NotificationChannel("notify",
                    "sample", NotificationManager.IMPORTANCE_HIGH);

            Uri sound = Uri.parse("android.resource://"
                    + this.getPackageName() + "/" + R.raw.waterdrop);

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();



            notificationChannel.setDescription("this is sample");
            if(storedNotificationSound){

                notificationChannel.setSound(sound, attributes);
            }

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }



    private void startLoginActivity(){
        startActivity(new Intent(this, LoginSignupActivity.class));
        finish();
    }

    public void setDailyAlarm(){
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Calendar cur = Calendar.getInstance();

        if (cur.after(calendar)) {
            calendar.add(Calendar.DATE, 1);

        }

        int code = 100001;
        Intent intent = new Intent(getApplicationContext(), RemainderBroadcast.class);
        DailyAlarmUtils.cancelAllAlarms(getApplicationContext(), intent);

        intent.putExtra("title","Good Morning "+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());
        intent.putExtra("code",code);
        intent.putExtra("notificationSound",storedNotificationSound);
        intent.putExtra("content","Click here to check habits and tasks for the day");

        DailyAlarmUtils.addAlarm(getApplicationContext(), intent, code, calendar);
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

                        Intent i = new Intent(getApplicationContext(), RemainderBroadcast.class);
                        AlarmUtils.cancelAllAlarms(getApplicationContext(), i);


                        for(DocumentSnapshot documentSnapshot:snapshotList){

                            Timestamp timestamp = (Timestamp) documentSnapshot.getData().get("remainder");
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY,timestamp.toDate().getHours());
                            calendar.set(Calendar.MINUTE, timestamp.toDate().getMinutes());
                            calendar.set(Calendar.SECOND,0);

                            if(calendar.getTimeInMillis()>=Calendar.getInstance().getTimeInMillis()){

//                                Log.e("time",calendar.getTimeInMillis() +"  " +Calendar.getInstance().getTimeInMillis());

                                Intent intent = new Intent(getApplicationContext(), RemainderBroadcast.class);

                                intent.putExtra("title",((String)documentSnapshot.getData().get("title")));
                                intent.putExtra("code",code);
                                intent.putExtra("notificationSound",storedNotificationSound);
                                intent.putExtra("content","It's time to complete your habit");


                                AlarmUtils.addAlarm(getApplicationContext(), intent, code, calendar);
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

    private void getTaskFirebaseData(){
        FirebaseFirestore.getInstance()
                .collection("tasks")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereGreaterThanOrEqualTo("timeRemainder",new Timestamp(new Date(getStartOfDayInMillis())))
                .whereLessThanOrEqualTo("timeRemainder",new Timestamp(new Date(getEndOfDayInMillis())))
                .orderBy("timeRemainder",Query.Direction.ASCENDING)
                .orderBy("completed",Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int code = 100;
                        Intent i = new Intent(getApplicationContext(), RemainderBroadcast.class);
                        TaskAlarmUtils.cancelAllAlarms(getApplicationContext(), i);

                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                        for(DocumentSnapshot documentSnapshot:snapshotList){
                            Log.e("data", documentSnapshot.getData().toString());

                            Timestamp timestamp = (Timestamp) documentSnapshot.getData().get("timeRemainder");
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY,timestamp.toDate().getHours());
                            calendar.set(Calendar.MINUTE, timestamp.toDate().getMinutes());
                            calendar.set(Calendar.SECOND,0);

                            if(calendar.getTimeInMillis()>=Calendar.getInstance().getTimeInMillis()){
                                Intent intent = new Intent(getApplicationContext(), RemainderBroadcast.class);

                                intent.putExtra("title",((String)documentSnapshot.getData().get("title")));
                                intent.putExtra("code",code);
                                intent.putExtra("notificationSound",storedNotificationSound);
                                intent.putExtra("content","You have a Task to complete");


                                TaskAlarmUtils.addAlarm(getApplicationContext(), intent, code, calendar);
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


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if(firebaseAuth.getCurrentUser() == null){
            startLoginActivity();
            return;
        }
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }




}