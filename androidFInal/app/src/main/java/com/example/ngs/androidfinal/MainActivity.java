package com.example.ngs.androidfinal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    classRoom CR;
    ListView lv;
    Spinner spnWeek,spnTimeStart,spnTimeEnd,spnBuilding;
    int timeStart,timeEnd,week,building;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spnBuilding = (Spinner) findViewById(R.id.spn_building);
        spnTimeStart = (Spinner) findViewById(R.id.spn_time_start);
        spnTimeEnd = (Spinner)findViewById(R.id.spn_time_end);
        spnWeek = (Spinner) findViewById(R.id.spn_week);
        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);
        CR = new classRoom();
        lv = (ListView) findViewById(R.id.listView);
        spnWeek.setOnItemSelectedListener(weekListener);
        spnTimeStart.setOnItemSelectedListener(timeStartListener);
        spnTimeEnd.setOnItemSelectedListener(timeEndListener);
        spnBuilding.setOnItemSelectedListener(buildingListener);
        SetNow();

    }
    void SetNow(){
        LocalDate date = LocalDate.now();
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int dayOfWeekIntValue = dayOfWeek.getValue();
        if(dayOfWeekIntValue == 7)dayOfWeekIntValue=0;
        spnWeek.setSelection(dayOfWeekIntValue);
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        int min = rightNow.get(Calendar.MINUTE);
        int timeSelect = 0;
        for(int i=0;i<4;i++){
            if(hour == 8+i){
                timeSelect=i;
            }
        }
        for(int i=4;i<8;i++){
            if(hour == 9+i){
                timeSelect=i;
            }
        }
        if(hour==18 ||(hour==19 && min<20)){
            timeSelect=9;
        }
        if((hour==19 && min>=20) ||(hour==20 && min<10)){
            timeSelect=10;
        }
        if((hour==20 && min>=10) ||(hour==21 && min<10)){
            timeSelect=11;
        }
        if((hour==21 && min>=10)){
            timeSelect=12;
        }
        spnTimeStart.setSelection(timeSelect);
        spnTimeEnd.setSelection(timeSelect);
    }
    AdapterView.OnItemSelectedListener weekListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            week = position;
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("classRoom");
            myRef.addListenerForSingleValueEvent(serachClassRoom);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    AdapterView.OnItemSelectedListener timeStartListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            timeStart = position;
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("classRoom");
            myRef.addListenerForSingleValueEvent(serachClassRoom);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    AdapterView.OnItemSelectedListener timeEndListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            timeEnd = position;
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("classRoom");
            myRef.addListenerForSingleValueEvent(serachClassRoom);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    AdapterView.OnItemSelectedListener buildingListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            building = position;
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("classRoom");
            myRef.addListenerForSingleValueEvent(serachClassRoom);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    ValueEventListener serachClassRoom = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            list.clear();
            String buildingName = getResources().getStringArray(R.array.building)[building];
            for(DataSnapshot ds:dataSnapshot.getChildren()){
                CR = ds.getValue(classRoom.class);
                String[] testChar = {"1"};
                if(week==0)testChar = CR.getW0().toString().split(",");
                if(week==1)testChar = CR.getW1().toString().split(",");
                if(week==2)testChar = CR.getW2().toString().split(",");
                if(week==3)testChar = CR.getW3().toString().split(",");
                if(week==4)testChar = CR.getW4().toString().split(",");
                if(week==5)testChar = CR.getW5().toString().split(",");
                if(week==6)testChar = CR.getW6().toString().split(",");
                Log.i("test",testChar[0]);
                boolean pass = true;
                if(timeStart<=timeEnd){
                    for(int i=timeStart;i<=timeEnd;i++){
                        if(testChar[i].equals("1"))
                            pass = false;
                    }
                }
                else {
                    pass = false;
                    Toast.makeText(MainActivity.this, "開始節數比結束少", Toast.LENGTH_LONG).show();
                }
                if(building>0 && !CR.getName().toString().contains(buildingName)){pass = false ;}
                if(pass)
                    list.add(CR.getName().toString());
            }
            lv.setAdapter(adapter);
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
