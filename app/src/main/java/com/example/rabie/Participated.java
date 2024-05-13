package com.example.rabie;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Participated extends AppCompatActivity {

    private static final String TAG = "ParticipatedActivity";
    private TextView username1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_paticipated);
        ImageView homeIcon = findViewById(R.id.homeIcon);
        ImageView courseIcon = findViewById(R.id.courseIcon);
        ImageView accountIcon = findViewById(R.id.accountIcon);
        ImageView notificationIcon = findViewById(R.id.notificationIcon);
        ImageView logoutIcon = findViewById(R.id.logoutIcon);

        fetchUserData();

        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Participated.this, UserFirstActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });

        courseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        accountIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Participated.this, AccountActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });

        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        logoutIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("checkBox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear().apply();

                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(Participated.this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                finish();
            }
        });

        //String userName = getIntent().getStringExtra("userName");
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("fullName", "");
        SharedPreferences.Editor editor = getSharedPreferences("userPrefs", MODE_PRIVATE).edit();
        editor.putString("fullName", userName);
        editor.apply();
        fetchParticipatedCourses(userName);
        EditText searchEditText = findViewById(R.id.recherche);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTeacherCards(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference reclamationRef = FirebaseDatabase.getInstance().getReference("reclamations");
                reclamationRef.orderByChild("userName").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ImageView redicon = findViewById(R.id.redCircleIcon);

                        if (dataSnapshot.exists()) {
                            boolean notification = false;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String status = snapshot.child("status").getValue(String.class);
                                if (status != null && status.equals("Approved")) {
                                    notification = true;
                                    break;
                                }
                            }

                            if (notification) {

                                redicon.setVisibility(View.VISIBLE);
                                showNotificationDiolog(dataSnapshot);
                            } else {

                                redicon.setVisibility(View.GONE);
                                Toast.makeText(Participated.this, "No new notifications", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            redicon.setVisibility(View.GONE);
                            Toast.makeText(Participated.this, "No new notifications", Toast.LENGTH_SHORT).show();
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                        Toast.makeText(Participated.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
    private void showNotificationDiolog(DataSnapshot dataSnapshot) {
        List<String> notifications = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            String userName = snapshot.child("userName").getValue(String.class);
            //String status = snapshot.child("status").getValue(String.class);
            String teacherName = snapshot.child("teacherName").getValue(String.class);
            String status = snapshot.child("status").getValue(String.class);
            if (status != null && status.equals("Approved")) {
                String dropdownItem = teacherName + " apporove your reclamation";
                notifications.add(dropdownItem);

            }

            AlertDialog.Builder builder = new AlertDialog.Builder(Participated.this);
            builder.setTitle("Notifications");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(Participated.this, android.R.layout.simple_dropdown_item_1line, notifications);
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removeReclamation(dataSnapshot.getChildren().iterator().next().getKey());
                }
            });
            builder.setNegativeButton("Cancel", null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
    private void removeReclamation(String reclamationKey) {
        DatabaseReference reclamationRef = FirebaseDatabase.getInstance().getReference("reclamations").child(reclamationKey);
        reclamationRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Reclamation removed successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to remove reclamation: " + e.getMessage());
                    }
                });
    }
    private void filterTeacherCards(String searchText) {
        LinearLayout teacherContainer = findViewById(R.id.teacherContainer);

        for (int i = 0; i < teacherContainer.getChildCount(); i++) {
            View view = teacherContainer.getChildAt(i);
            if (view instanceof LinearLayout) {
                LinearLayout teacherCard = (LinearLayout) view;
                TextView courseNameTextView = teacherCard.findViewById(R.id.courseName);
                String courseName = courseNameTextView.getText().toString().toLowerCase();
                if (courseName.contains(searchText.toLowerCase())) {
                    teacherCard.setVisibility(View.VISIBLE);
                } else {
                    teacherCard.setVisibility(View.GONE);
                }
            }
        }
    }
    private void fetchParticipatedCourses(String userName) {
        DatabaseReference participatedRef = FirebaseDatabase.getInstance().getReference().child("participated");
        participatedRef.orderByChild("userName").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LinearLayout participatedContainer = findViewById(R.id.teacherContainer);

                participatedContainer.removeAllViews();

                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    String teacherName = courseSnapshot.child("teacherName").getValue(String.class);
                    String courseName = courseSnapshot.child("category").getValue(String.class);

                    addParticipatedCourse(teacherName, courseName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Participated.this, "Failed to fetch participated courses.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addParticipatedCourse(String teacherName, String courseName) {


        LinearLayout participatedContainer = findViewById(R.id.teacherContainer);
        LayoutInflater inflater = LayoutInflater.from(this);
        View participatedCourseView = inflater.inflate(R.layout.teacher_card_layout, participatedContainer, false);

        TextView teacherNameTextView = participatedCourseView.findViewById(R.id.techersName);
        TextView courseNameTextView = participatedCourseView.findViewById(R.id.courseName);
        LinearLayout cardLayout = participatedCourseView.findViewById(R.id.cardLayout);

        cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference coursesRef = FirebaseDatabase.getInstance().getReference().child("Courses");
                coursesRef.orderByChild("teacherName").equalTo(teacherName).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                Intent intent = new Intent(Participated.this, CourseActivity.class);
                                intent.putExtra("teacherName", teacherName);
                                intent.putExtra("category", courseCategory);
                                intent.putStringArrayListExtra("courseNames", (ArrayList<String>) courseNames);
                                intent.putStringArrayListExtra("coursePdfUrls", (ArrayList<String>) coursePdfUrls);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            }
                        } else {
                            Toast.makeText(Participated.this, "No courses found for this teacher", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Participated.this, "Failed to retrieve course data.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        teacherNameTextView.setText(teacherName);
        courseNameTextView.setText(courseName);

        participatedContainer.addView(participatedCourseView);
    }
    private void fetchUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("fullName", "");

        if (!userName.isEmpty()) {
            updateUI(userName);
        } else {
            Log.d(TAG, "User data not found");
        }
    }

    private void updateUI(String userName) {
        username1 = findViewById(R.id.uername);
        username1.setText(userName);

    }
}
