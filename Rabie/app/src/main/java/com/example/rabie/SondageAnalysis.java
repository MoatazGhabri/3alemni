package com.example.rabie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SondageAnalysis extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
        private PieChart pieChart;
        private DatabaseReference sondageAnalysisRef;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sondage_analysis);
            SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
            String userEmail = sharedPreferences.getString("userEmail", "");
            String teacherName = sharedPreferences.getString("teacherName", "");
            pieChart = findViewById(R.id.pieChart);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            drawerLayout = findViewById(R.id.drawer_layout);
            actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.syncState();

            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setItemIconTintList(null);
            View headerView = navigationView.getHeaderView(0);
            TextView userEmailTextView = headerView.findViewById(R.id.user_email);
            userEmailTextView.setText(userEmail);

            sondageAnalysisRef = FirebaseDatabase.getInstance().getReference("sondageanalysis");
            ImageButton deleteIcon = findViewById(R.id.delete);
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteResponses();
                }
            });


            fetchAndDisplaySondageAnalysisData(teacherName);

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_dashboard:
                            Intent intent1 = new Intent(SondageAnalysis.this, TeacherDashboard.class);
                            startActivity(intent1);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            break;
                        case R.id.nav_course_form:
                            Intent coursForm = new Intent(SondageAnalysis.this, Create.class);
                            startActivity(coursForm);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            break;
                        case R.id.nav_form:
                            Intent formQuiz = new Intent(SondageAnalysis.this, CreateQuizActivity.class);
//                        formQuiz.putExtra("teacherName", teacher.getText().toString());
//                        formQuiz.putExtra("userEmail", userEmail);
                            startActivity(formQuiz);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            break;
                        case R.id.student:
                            Intent student = new Intent(SondageAnalysis.this, StudentParticipated.class);

                            startActivity(student);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            break;
                        case R.id.nav_quiz:
                            Intent quizList = new Intent(SondageAnalysis.this, QuizListActivity.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                            startActivity(quizList);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            break;
                        case R.id.nav_course_list:
                            Intent courseList = new Intent(SondageAnalysis.this, CourseListActivity.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                            startActivity(courseList);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            break;
                        case R.id.Message:
                            Intent message = new Intent(SondageAnalysis.this, TeacherMessage.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                            startActivity(message);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            break;
                        case R.id.sondage:
                            Intent sondage = new Intent(SondageAnalysis.this, SondageActivity.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                            startActivity(sondage);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            break;

                        case R.id.Logout:
                            SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear().apply();
                            FirebaseAuth.getInstance().signOut();
                            Intent logoutIntent = new Intent(SondageAnalysis.this, LoginTeacher.class);
                            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(logoutIntent);
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                            finish();
                            break;

                    }
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
            });
        }

        private void fetchAndDisplaySondageAnalysisData(String teacherName) {
            sondageAnalysisRef.child(teacherName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        List<String> selectedOptions = new ArrayList<>();

                        for (DataSnapshot sondageSnapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot optionSnapshot : sondageSnapshot.child("selectedOptions").getChildren()) {
                                String option = optionSnapshot.getValue(String.class);
                                selectedOptions.add(option);
                            }
                        }

                        displayPieChart(selectedOptions);
                    } else {
                        Toast.makeText(SondageAnalysis.this, "No sondage analysis data available", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(SondageAnalysis.this, "Error retrieving sondage analysis data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void displayPieChart(List<String> selectedOptions) {
            HashMap<String, Integer> optionCounts = new HashMap<>();

            for (String option : selectedOptions) {
                if (optionCounts.containsKey(option)) {
                    int count = optionCounts.get(option);
                    optionCounts.put(option, count + 1);
                } else {
                    optionCounts.put(option, 1);
                }
            }


            List<PieEntry> entries = new ArrayList<>();

            for (String option : optionCounts.keySet()) {
                int count = optionCounts.get(option);
                entries.add(new PieEntry(count, option));
            }

            PieDataSet dataSet = new PieDataSet(entries, "Sondage Analysis");
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setValueTextSize(12f);
            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter(pieChart));
            pieChart.setData(data);
            pieChart.getDescription().setEnabled(false);
            pieChart.setCenterText("Sondage Analysis");
            pieChart.setEntryLabelColor(Color.BLACK);
            pieChart.animateY(1000);
            pieChart.invalidate();

        }
    private void deleteResponses() {
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        String teacherName = sharedPreferences.getString("teacherName", "");

        DatabaseReference teacherRef = sondageAnalysisRef.child(teacherName);
        DatabaseReference sondagesRef = FirebaseDatabase.getInstance().getReference("sondages");

        teacherRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sondagesRef.child(teacherName).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SondageAnalysis.this, "Responses and question deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SondageAnalysis.this, "Failed to delete question", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SondageAnalysis.this, "Failed to delete responses", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("MenuItemClicked", "Item clicked: " + item.getItemId());

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    }
