package com.vamsigutha.atomichabitstracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    TextView displayName, displayEmail;
    MaterialCardView logoutCardView;
    SwitchMaterial notificationSwitch, notificationSoundSwitch;
    SharedPreferences sharedPreferences;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        displayName = view.findViewById(R.id.displayName);
        displayEmail = view.findViewById(R.id.displayEmail);
        logoutCardView = view.findViewById(R.id.logoutCardView);
        notificationSwitch = view.findViewById(R.id.notification_switch);
        notificationSoundSwitch = view.findViewById(R.id.notification_sound_switch);

        sharedPreferences =  getActivity().getSharedPreferences("settings",0);

        handleUserInfo();

        getStoredNotificationSwitch();
        getStoredNotificationSoundSwitch();

        handleNotificationSwitchChange();

        handleNotificationSoundSwitchChange();

        handleLogout();

        return view;
    }

    public void handleUserInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            displayName.setText(user.getDisplayName());
        }else{
            displayName.setText(user.getPhoneNumber());
        }

        if(user.getEmail() != null){
            displayEmail.setText(user.getEmail());
        }
    }

    public void getStoredNotificationSwitch(){
        boolean isChecked = sharedPreferences.getBoolean("notification",true);
        notificationSwitch.setChecked(isChecked);

    }

    public void getStoredNotificationSoundSwitch(){
        boolean isChecked = sharedPreferences.getBoolean("notificationSound",true);
        notificationSoundSwitch.setChecked(isChecked);
    }


    public void handleNotificationSwitchChange(){
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("notification",isChecked);
                editor.apply();
            }
        });
    }

    public void handleNotificationSoundSwitchChange(){
        notificationSoundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("notificationSound",isChecked);
                editor.apply();
            }
        });
    }


    public void handleLogout(){
        logoutCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //cancle alarms before logout
                Intent i = new Intent(getActivity(), RemainderBroadcast.class);
                AlarmUtils.cancelAllAlarms(getActivity(), i);
                TaskAlarmUtils.cancelAllAlarms(getActivity(), i);

                //logout
                AuthUI.getInstance().signOut(getActivity());
            }
        });
    }
}