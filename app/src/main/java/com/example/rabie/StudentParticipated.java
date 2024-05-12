package com.example.rabie;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;



public class StudentParticipated extends AppCompatActivity {

    private ListView listView;
    private List<Student> studentList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DatabaseReference databaseReference,dReference;
    private TextView teacher1;

    private DatabaseReference participatedRef;
    private ParticipationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_student_participated);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("userEmail", "");
        String teacherName = sharedPreferences.getString("teacherName", "");
        listView = findViewById(R.id.listView);
        studentList = new ArrayList<>();

        participatedRef = FirebaseDatabase.getInstance().getReference().child("participated");
        //String teacherName = getIntent().getStringExtra("teacherName");
        Query participatedQuery = participatedRef.orderByChild("teacherName").equalTo(teacherName);

        participatedQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    String studentName = studentSnapshot.child("userName").getValue(String.class);

                    DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference().child("scores");
                    scoresRef.orderByChild("teacherName").equalTo(teacherName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot scoreSnapshot) {
                            int score = -1;
                            if (scoreSnapshot.exists()) {
                                for (DataSnapshot scoreData : scoreSnapshot.getChildren()) {
                                    score = scoreData.child("score").getValue(Integer.class);
                                    break;
                                }
                            }

                            studentList.add(new Student(studentName, score));

                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Failed to read score value.", error.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(StudentParticipated.this, "Failed to fetch participated students.", Toast.LENGTH_SHORT).show();
            }
        });

        adapter = new ParticipationAdapter(StudentParticipated.this, R.layout.paricipated_table, studentList);
        listView.setAdapter(adapter);
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
                        Intent intent1 = new Intent(StudentParticipated.this,TeacherDashboard.class);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.nav_course_form:
                        Intent coursForm = new Intent(StudentParticipated.this,Create.class);
                        startActivity(coursForm);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.nav_form:
                        Intent formQuiz = new Intent(StudentParticipated.this,CreateQuizActivity.class);
//                        formQuiz.putExtra("teacherName", teacher.getText().toString());
//                        formQuiz.putExtra("userEmail", userEmail);
                        startActivity(formQuiz);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;

                    case R.id.nav_quiz:
                        Intent quizList = new Intent(StudentParticipated.this, QuizListActivity.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(quizList);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.nav_course_list:
                        Intent courseList = new Intent(StudentParticipated.this, CourseListActivity.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(courseList);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.pie:
                        Intent pie = new Intent(StudentParticipated.this, SondageAnalysis.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(pie);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.sondage:
                        Intent sondage = new Intent(StudentParticipated.this, SondageActivity.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(sondage);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.Message:
                        Intent message = new Intent(StudentParticipated.this, TeacherMessage.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(message);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.Logout:
                        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        Intent logoutIntent = new Intent(StudentParticipated.this, LoginTeacher.class);
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
        dReference = FirebaseDatabase.getInstance().getReference("teachers");
        dReference.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String teacherName = snapshot.child("name").getValue(String.class);


                        TextView userNameTextView = headerView.findViewById(R.id.user_name);
                        userNameTextView.setText(teacherName);
                    }
                } else {
                    Toast.makeText(StudentParticipated.this, "User not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StudentParticipated.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
