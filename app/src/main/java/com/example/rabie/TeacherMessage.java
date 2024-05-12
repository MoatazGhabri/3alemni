package com.example.rabie;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TeacherMessage extends AppCompatActivity {

    private ListView userListView;
    private DatabaseReference chatRef;
    private List<String> userList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DatabaseReference databaseReference,dReference;
    private Map<String, Message> lastMessageMap = new HashMap<>();

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_message);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("userEmail", "");
        String teacherName = sharedPreferences.getString("teacherName", "");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Courses");

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setItemIconTintList(null);
        TextView userEmailTextView = headerView.findViewById(R.id.user_email);

        userEmailTextView.setText(userEmail);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                return true;
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_dashboard:
                        Intent intent1 = new Intent(TeacherMessage.this,TeacherDashboard.class);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.nav_course_form:
                        Intent coursForm = new Intent(TeacherMessage.this,Create.class);
                        startActivity(coursForm);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.nav_form:
                        Intent formQuiz = new Intent(TeacherMessage.this,CreateQuizActivity.class);
//                        formQuiz.putExtra("teacherName", teacher.getText().toString());
//                        formQuiz.putExtra("userEmail", userEmail);
                        startActivity(formQuiz);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.student:
                        // Extract the text from the TextView and pass it as a String extra
                        Intent student = new Intent(TeacherMessage.this, StudentParticipated.class);

                        startActivity(student);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.nav_quiz:
                        Intent quizList = new Intent(TeacherMessage.this, QuizListActivity.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(quizList);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.pie:
                        Intent pie = new Intent(TeacherMessage.this, SondageAnalysis.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(pie);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.sondage:
                        Intent sondage = new Intent(TeacherMessage.this, SondageActivity.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(sondage);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.nav_course_list:
                        Intent courseList = new Intent(TeacherMessage.this, CourseListActivity.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(courseList);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        break;


                    case R.id.Logout:
                        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        Intent logoutIntent = new Intent(TeacherMessage.this, LoginTeacher.class);
                        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(logoutIntent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                        finish();
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        userListView = findViewById(R.id.userListView);


        userList = new ArrayList<>();
        adapter = new MessageListAdapter(this, userList, lastMessageMap);
        userListView.setAdapter(adapter);

        chatRef = FirebaseDatabase.getInstance().getReference("chats");


        loadUsernames();

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View modalView = getLayoutInflater().inflate(R.layout.modal_send_message, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(TeacherMessage.this);
                builder.setView(modalView);
                AlertDialog dialog = builder.create();
                dialog.show();
                RecyclerView chatHistoryRecyclerView = modalView.findViewById(R.id.chatHistoryRecyclerView);

                String selectedUser = userList.get(position);
                unreadMessagesMap.put(selectedUser, false);
                adapter.notifyDataSetChanged();
                if (chatHistoryRecyclerView != null) {
                    fetchAndDisplayChatHistory(chatHistoryRecyclerView, selectedUser);
                }
                Button sendMessageButton = modalView.findViewById(R.id.sendMessageButton);
                sendMessageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText messageEditText = modalView.findViewById(R.id.messageEditText);
                        String message = messageEditText.getText().toString().trim();
                        if (!message.isEmpty()) {
                            SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);

                            String teacherName = sharedPreferences.getString("teacherName", "");

                            sendMessageToUser(message, teacherName, selectedUser);
                            messageEditText.setText("");
                        } else {
                            Toast.makeText(TeacherMessage.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


    private Map<String, Boolean> unreadMessagesMap = new HashMap<>();

    protected void loadUsernames() {
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        String teacherName = sharedPreferences.getString("teacherName", "");

        DatabaseReference userRef = chatRef.child(teacherName);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                lastMessageMap.clear();
                Map<String, Long> lastMessageTimestampMap = new HashMap<>();

                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    String senderName = messageSnapshot.child("senderName").getValue(String.class);
                    if (senderName != null) {
                        Message message = messageSnapshot.getValue(Message.class);
                        if (message != null) {
                            long timestamp = message.getTimestamp();
                            if (!lastMessageTimestampMap.containsKey(senderName) || timestamp > lastMessageTimestampMap.get(senderName)) {
                                lastMessageTimestampMap.put(senderName, timestamp);
                                lastMessageMap.put(senderName, message);
                            }
                        }
                    }
                }

                userList.addAll(lastMessageTimestampMap.keySet());

                adapter.notifyDataSetChanged();

                for (Map.Entry<String, Long> entry : lastMessageTimestampMap.entrySet()) {
                    String user = entry.getKey();
                    Long timestamp = entry.getValue();
                    Log.d("LastMessageTime", "User: " + user + ", Last Message Time: " + timestamp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }



    private void sendMessageToUser(String message, String teacherName, String selectedUser) {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats").child(selectedUser);
        String messageId = chatRef.push().getKey();
        if (messageId != null) {
            Message newMessage = new Message(selectedUser, message, teacherName,"receivedMessage", System.currentTimeMillis());
            chatRef.child(messageId).setValue(newMessage);
        } else {
            Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchAndDisplayChatHistory(RecyclerView recyclerView, String selectedUser) {
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        String teacherName = sharedPreferences.getString("teacherName", "");

        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats");

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Message> sentMessages = new ArrayList<>();
                List<Message> receivedMessages = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                        Message message = messageSnapshot.getValue(Message.class);
                        if (message.getSenderName().equals(selectedUser) && message.getTeacherName().equals(teacherName)) {
                            sentMessages.add(message);
                        } else if (message.getTeacherName().equals(teacherName) && message.getSenderName().equals(selectedUser)) {
                            receivedMessages.add(message);
                        }
                    }
                }

                Collections.sort(sentMessages, (message1, message2) -> Long.compare(message1.getTimestamp(), message2.getTimestamp()));
                Collections.sort(receivedMessages, (message1, message2) -> Long.compare(message1.getTimestamp(), message2.getTimestamp()));

                List<Message> allMessages = new ArrayList<>();
                allMessages.addAll(sentMessages);
                //allMessages.addAll(receivedMessages);
                ChatAdapter adapter = new ChatAdapter(sentMessages, teacherName, selectedUser);

                //ChatAdapter adapter = new ChatAdapter(sentMessages, receivedMessages, selectedUser, new ArrayList<>(), new HashMap<>());

                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(TeacherMessage.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TeacherMessage.this, "Failed to fetch chat history", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

