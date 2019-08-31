package com.example.inclass01_advancemad;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class ChatroomFragment extends Fragment implements IDoTask {

    DatabaseReference mroot;
    FirebaseUser firebaseUser;
    FirebaseAuth mauth;
    String userId;
    ArrayList<Chatroom> chatroomArrayList;
    RecyclerView recyclerView;
    RecyclerView.Adapter rec_adapter;
    RecyclerView.LayoutManager rec_layout;

    User current_user;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_chatroom,container,false);

        mroot= FirebaseDatabase.getInstance().getReference();
        mauth= FirebaseAuth.getInstance();
        firebaseUser=mauth.getCurrentUser();
        userId =firebaseUser.getUid();
        chatroomArrayList = new ArrayList<>();
        Button btnAddChatroom = (Button)v.findViewById(R.id.btnAddChatroom);
        final TextView txtChatroomName = (TextView)v.findViewById(R.id.editAddChatroom);
        recyclerView = (RecyclerView)v.findViewById(R.id.recyclerchatroom);
        recyclerView.setHasFixedSize(true);
        rec_layout=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(rec_layout);
        rec_adapter=new ChatroomAdapter(chatroomArrayList,this);
        recyclerView.setAdapter(rec_adapter);
        getCurrentUser();
        getChatroomDetails();

        btnAddChatroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String chatroomId = UUID.randomUUID().toString();
                DatabaseReference chatroom_ref = mroot.child("Chatroom");
                DatabaseReference new_chatroom = chatroom_ref.child(chatroomId);
                Chatroom chatroom = new Chatroom();
                chatroom.chatroomId = chatroomId;
                chatroom.chatroomName = txtChatroomName.getText().toString().trim();
                chatroom.time = "04/05/2010";
                ArrayList<User> us = new ArrayList<>();
                us.add(current_user);
                chatroom.userList = us;
                new_chatroom.setValue(chatroom);
            }
        });

        return v;

    }
    public void getCurrentUser()
    {
        DatabaseReference myRef = mroot.child("Users/"+ userId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                current_user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void getChatroomDetails()
    {
        DatabaseReference chatroom_ref = mroot.child("Chatroom");
        chatroom_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                chatroomArrayList.clear();
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    Chatroom chat=(Chatroom)data.getValue(Chatroom.class);
                    chatroomArrayList.add(chat);
                }

                recyclerView.setHasFixedSize(true);
                rec_layout=new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(rec_layout);
                //rec_adapter=new ChatroomAdapter(chatroomArrayList, this);
                recyclerView.setAdapter(rec_adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    @Override
    public void joinChatroom(Chatroom chatroom) {
        Log.d("Chatroom", chatroom.toString());
    }
}
