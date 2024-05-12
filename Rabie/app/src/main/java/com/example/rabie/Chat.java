package com.example.rabie;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ListView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ChildEventListener;
public class Chat extends AppCompatActivity {

        private DatabaseReference mDatabase;
        private EditText mMessageEditText;
        private Button mSendButton;
        private ListView mMessageListView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_chat);

            mDatabase = FirebaseDatabase.getInstance().getReference();
            mMessageEditText = findViewById(R.id.messageEditText);
            mSendButton = findViewById(R.id.sendButton);
            mMessageListView = findViewById(R.id.messageListView);

            mSendButton.setOnClickListener(view -> {
                String message = mMessageEditText.getText().toString();
                if (!message.isEmpty()) {
                    sendMessage(message);
                    mMessageEditText.setText("");
                }
            });

            mDatabase.child("messages").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    String message = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

            private void sendMessage(String message) {
            mDatabase.child("messages").push().setValue(message);
        }
    }
