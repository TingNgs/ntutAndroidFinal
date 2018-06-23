package com.example.ngs.androidfinal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
public class chatroom extends AppCompatActivity {

    TextView tv_classRoom;
    EditText edt_chat;
    Button btn_input;
    classRoom CR;
    String classRoomSelected;
    DatabaseReference selectedRef,chatRoom;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);
        lv = (ListView)findViewById(R.id.lv_chat);
        tv_classRoom = (TextView) findViewById(R.id.tv_classRoom);
        edt_chat = (EditText) findViewById(R.id.edt_chat);
        btn_input = (Button) findViewById(R.id.btn_inputChat);
        classRoomSelected = getIntent().getStringExtra("classRoomSelected");
        tv_classRoom.setText("教室 ： " + classRoomSelected);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("classRoom");
        myRef.addListenerForSingleValueEvent(getChatRoom);
        btn_input.setOnClickListener(btnInputOnClick);
    }

    ValueEventListener getChatRoom = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for(DataSnapshot ds:dataSnapshot.getChildren()){
                CR = ds.getValue(classRoom.class);
                if(CR.getName().equals(classRoomSelected)){
                    selectedRef = ds.getRef();
                    selectedRef.addValueEventListener(initChatRoom);
                    break;
                }
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) { }
    };
    ValueEventListener initChatRoom = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for(DataSnapshot ds:dataSnapshot.getChildren()) {
                if (ds.getKey().equals("chatRoom")) {
                    chatRoom = ds.getRef();
                    chatRoom.addListenerForSingleValueEvent(readChatRoom);
                    break;
                }
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) { }
    };
    ValueEventListener readChatRoom = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            list.clear();
            for(DataSnapshot ds:dataSnapshot.getChildren()) {
                Log.i("test",ds.getValue().toString());
                list.add(ds.getValue().toString());
            }
            Collections.reverse(list);
            lv.setAdapter(adapter);
        }
        @Override
        public void onCancelled(DatabaseError databaseError) { }
    };
    View.OnClickListener btnInputOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!edt_chat.getText().toString().isEmpty()){
                if(chatRoom == null) selectedRef.child("chatRoom").push().setValue(edt_chat.getText().toString());
                else chatRoom.push().setValue(edt_chat.getText().toString());
            }
            edt_chat.setText("");
            if(chatRoom == null)selectedRef.addListenerForSingleValueEvent(initChatRoom);
            else chatRoom.addValueEventListener(readChatRoom);
        }
    };

}
