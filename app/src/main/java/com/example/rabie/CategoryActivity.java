package com.example.rabie;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private DatabaseReference mDatabaseReference;
    private static final String PARTICIPATED_COURSES_KEY = "";
    private HashSet<String> participatedCourses = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("fullName", "");
        String category = getIntent().getStringExtra("category");


        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("teachers");
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                String teacherName = dataSnapshot.child("teacherName").getValue(String.class);
                String courseName = dataSnapshot.child("course").getValue(String.class);

                sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
                String userName = sharedPreferences.getString("fullName", "");
                DatabaseReference participatedRef = FirebaseDatabase.getInstance().getReference().child("participated");

                if (courseName != null && courseName.equals(category)) {
                    participatedRef.orderByChild("userName").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean alreadyParticipated = false;
                            for (DataSnapshot participatedSnapshot : dataSnapshot.getChildren()) {
                                String participatedTeacherName = participatedSnapshot.child("teacherName").getValue(String.class);
                                String participatedCourseName = participatedSnapshot.child("category").getValue(String.class);
                                if (participatedTeacherName.equals(teacherName) && participatedCourseName.equals(courseName)) {
                                    alreadyParticipated = true;
                                    break;
                                }
                            }
                            if (!alreadyParticipated) {
                                addTeacherCard(teacherName, courseName);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(CategoryActivity.this, "Failed to retrieve participation course", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }


        @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CategoryActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
        ImageButton backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });
    }

    private void addTeacherCard(String teacherName, String courseName) {
        LinearLayout parentLayout = findViewById(R.id.teacherContainer);
        LayoutInflater inflater = LayoutInflater.from(this);


        LinearLayout teacherCard = (LinearLayout) inflater.inflate(R.layout.teacher_card_layout, parentLayout, false);

        LinearLayout cardLayout = teacherCard.findViewById(R.id.cardLayout);

        cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CategoryActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_enter_key, null);
                builder.setView(dialogView);
                EditText keyEditText = dialogView.findViewById(R.id.keyEditText);
                builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String enteredKey = keyEditText.getText().toString().trim();
                        verifyCourseKey(enteredKey, teacherName);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();

                dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_container_bg);
                dialog.show();
            }
        });

        TextView teacherNameTextView = teacherCard.findViewById(R.id.techersName);
        TextView courseNameTextView = teacherCard.findViewById(R.id.courseName);

        teacherNameTextView.setText(teacherName);
        courseNameTextView.setText(courseName);

        parentLayout.addView(teacherCard);

    }

    private void verifyCourseKey(String enteredKey, String teacherName) {
        DatabaseReference coursesRef = FirebaseDatabase.getInstance().getReference().child("Courses");
        coursesRef.orderByChild("key").equalTo(enteredKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<String> courseNames = new ArrayList<>();
                    List<String> coursePdfUrls = new ArrayList<>();
                    String courseCategory = "";
                    for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                        String courseName = courseSnapshot.child("name").getValue(String.class);
                        String coursePdfUrl = courseSnapshot.child("pdfUrl").getValue(String.class);
                        courseCategory = courseSnapshot.child("category").getValue(String.class);
                        String Teacher = courseSnapshot.child("teacherName").getValue(String.class);

                        if (teacherName.equals(Teacher)) {
                            courseNames.add(courseName);
                            coursePdfUrls.add(coursePdfUrl);
                        }
                    }

                    if (!courseNames.isEmpty()) {
                        Intent intent = new Intent(CategoryActivity.this, CourseActivity.class);
                        intent.putExtra("teacherName", teacherName);
                        intent.putExtra("category", courseCategory);
                        intent.putStringArrayListExtra("courseNames", (ArrayList<String>) courseNames);
                        intent.putStringArrayListExtra("coursePdfUrls", (ArrayList<String>) coursePdfUrls);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
                        String userName = sharedPreferences.getString("fullName", "");

                        DatabaseReference participatedRef = FirebaseDatabase.getInstance().getReference().child("participated");
                        String participationKey = participatedRef.push().getKey();
                        Map<String, Object> participationMap = new HashMap<>();
                        participationMap.put("userName", userName);
                        participationMap.put("teacherName", teacherName);
                        participationMap.put("category", courseCategory);
                        participatedRef.child(participationKey).setValue(participationMap);
                    } else {
                        Toast.makeText(CategoryActivity.this, "No courses found for the specified teacher.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CategoryActivity.this, "Invalid key", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CategoryActivity.this, "Failed to verify key.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
