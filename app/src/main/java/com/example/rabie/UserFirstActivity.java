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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class UserFirstActivity extends AppCompatActivity {
    private static final String PARTICIPATED_COURSES_KEY = "";
    private HashSet<String> participatedCourses = new HashSet<>();

    private static final String TAG = "UserFirstActivity";
    private DatabaseReference mDatabaseReference;
    private TextView username1;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_user_first);
        ImageView homeIcon = findViewById(R.id.homeIcon);
        ImageView courseIcon = findViewById(R.id.courseIcon);
        ImageView accountIcon = findViewById(R.id.accountIcon);
        ImageView notificationIcon = findViewById(R.id.notificationIcon);
        ImageView logoutIcon = findViewById(R.id.logoutIcon);
        sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("fullName", "");

        fetchUserData();
        restoreParticipatedCourses();

        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        courseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = username1.getText().toString();

                Intent intent = new Intent(UserFirstActivity.this, Participated.class);
                intent.putExtra("userName", userName);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });

        accountIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserFirstActivity.this, AccountActivity.class);
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

                Intent intent = new Intent(UserFirstActivity.this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                finish();
            }
        });
        DatabaseReference coursesRef = FirebaseDatabase.getInstance().getReference().child("Courses");

        coursesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    LinearLayout courseContainer = findViewById(R.id.coursesContainer);
                    courseContainer.removeAllViews();

                    for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                        String courseCategory = courseSnapshot.child("category").getValue(String.class);
                        if (courseCategory != null) {
                            addCourseCategory(courseCategory);
                        }
                    }
                } else {
                    Log.d(TAG, "No courses found in the database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read courses data.", databaseError.toException());
                Toast.makeText(UserFirstActivity.this, "Failed to retrieve courses data from Firebase.", Toast.LENGTH_SHORT).show();
            }
        });



//        String userUid = getIntent().getStringExtra("userUid");
//        if (userUid != null) {
//            // Fetch user data using the UID
//            fetchUserData(userUid);
//        } else {
//            // Handle case where UID is not passed
//            Log.e(TAG, "User UID not found in intent extras");
//        }

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("teachers");

        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                String teacherName = dataSnapshot.child("teacherName").getValue(String.class);
                String courseName = dataSnapshot.child("course").getValue(String.class);
                sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
                String userName = sharedPreferences.getString("fullName", "");
                //String userName = username1.getText().toString();
                DatabaseReference participatedRef = FirebaseDatabase.getInstance().getReference().child("participated");
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
                        Toast.makeText(UserFirstActivity.this, "Failed to retrieve participation data from Firebase.", Toast.LENGTH_SHORT).show();
                    }
                });
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
                Toast.makeText(UserFirstActivity.this, "Failed to retrieve data from Firebase.", Toast.LENGTH_SHORT).show();
            }
        });
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
                sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
                String userName = sharedPreferences.getString("fullName", "");
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
                                Toast.makeText(UserFirstActivity.this, "No new notifications", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            redicon.setVisibility(View.GONE);
                            Toast.makeText(UserFirstActivity.this, "No new notifications", Toast.LENGTH_SHORT).show();
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(UserFirstActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        fetchUserData();
        restoreParticipatedCourses();
    }

    private void restoreParticipatedCourses() {
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        String serializedSet = sharedPreferences.getString(PARTICIPATED_COURSES_KEY, "");
        if (!serializedSet.isEmpty()) {
            participatedCourses = new HashSet<>(Arrays.asList(serializedSet.split(",")));
        }

        removeParticipatedCourseViews();
    }

    private void removeParticipatedCourseViews() {
        LinearLayout parentLayout = findViewById(R.id.coursesContainer);
        for (String courseName : participatedCourses) {
            View viewToRemove = parentLayout.findViewWithTag(courseName);
            if (viewToRemove != null) {
                parentLayout.removeView(viewToRemove);
            }
        }
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

            AlertDialog.Builder builder = new AlertDialog.Builder(UserFirstActivity.this);
            builder.setTitle("Notifications");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(UserFirstActivity.this, android.R.layout.simple_dropdown_item_1line, notifications);
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removeReclamation(dataSnapshot.getChildren().iterator().next().getKey());
                }
            });
            builder.setNegativeButton("Cancel", null);
            AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_container_bg);
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

    private void addTeacherCard(String teacherName, String courseName) {
        LinearLayout parentLayout = findViewById(R.id.teacherContainer);
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout teacherCard = (LinearLayout) inflater.inflate(R.layout.teacher_card_layout, parentLayout, false);

        LinearLayout cardLayout = teacherCard.findViewById(R.id.cardLayout);

        cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserFirstActivity.this);
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
                    for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                        String courseName = courseSnapshot.child("name").getValue(String.class);
                        String courseDescription = courseSnapshot.child("description").getValue(String.class);
                        String coursePdfUrl = courseSnapshot.child("pdfUrl").getValue(String.class);
                        String courseCategory = courseSnapshot.child("category").getValue(String.class);

                        Intent intent = new Intent(UserFirstActivity.this, CourseActivity.class);
                        intent.putExtra("teacherName", teacherName);
                        intent.putExtra("category", courseCategory);
                        intent.putExtra("description", courseDescription);
                        intent.putExtra("name", courseName);
                        intent.putExtra("pdfUrl", coursePdfUrl);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);

                        String userName = username1.getText().toString();
                        DatabaseReference participatedRef = FirebaseDatabase.getInstance().getReference().child("participated");
                        String participationKey = participatedRef.push().getKey();
                        Map<String, Object> participationMap = new HashMap<>();
                        participationMap.put("userName", userName);
                        participationMap.put("teacherName", teacherName);
                        participationMap.put("category", courseCategory);
                        participatedRef.child(participationKey).setValue(participationMap);
                    }
                } else {
                    Toast.makeText(UserFirstActivity.this, "Invalid key", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserFirstActivity.this, "Failed to verify key.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserData() {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUid);


        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("fullName").getValue(String.class);
                    SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("fullName", userName);
                    editor.apply();
                    updateUI(userName);
                } else {
                    Log.d(TAG, "User data not found in database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserFirstActivity.this, "Failed to retrieve user data from Firebase.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateUI(String userName) {
       username1 = findViewById(R.id.uername);
        username1.setText(userName);

    }
    private void addCourseCategory(String courseCategory) {
        LinearLayout parentLayout = findViewById(R.id.coursesContainer);
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout categoryLayout = (LinearLayout) inflater.inflate(R.layout.course_category_layout, parentLayout, false);

        TextView categoryTextView = categoryLayout.findViewById(R.id.categoryName);
        categoryTextView.setText(courseCategory);

        parentLayout.addView(categoryLayout);
    }

}
